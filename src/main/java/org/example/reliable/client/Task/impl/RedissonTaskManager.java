package org.example.reliable.client.Task.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Handler;
import org.example.reliable.client.Task.TaskManager;
import org.example.reliable.client.config.RedissonConfig;
import org.example.reliable.client.helper.JobHelper;
import org.example.reliable.client.repo.entity.JobEntity;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.model.enums.TaskStatus;
import org.example.reliable.util.HandlerSerializationUtil;
import org.example.reliable.util.HandlerUtil;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryEvent;
import org.redisson.api.map.event.EntryExpiredListener;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Redisson-backed task manager with at-least-once semantics.
 * <p>
 * Strategy:
 * - Atomic pop: MAIN -> PROCESSING via takeLastAndOfferFirstTo (blocking)
 * - Lease per job in RMapCache with TTL; expiry listener re-queues job
 * - ACK removes job from PROCESSING and clears lease
 * <p>
 * Notes:
 * - For true single-consumer claim, prefer optimistic locking/CAS at the DB layer (JobHelper).
 * - Make handlers idempotent (pass jobId through and dedupe downstream side-effects).
 */
@Slf4j
public final class RedissonTaskManager implements TaskManager, AutoCloseable {

    // Queue / map names
    private static final String QUEUE_MAIN = "taskQueue";
    private static final String QUEUE_PROCESSING = "processingQueue";
    private static final String MAP_LEASES = "processingLeases";


    private static final int DEFAULT_PARALLELISM = 5;
    private static final long DEFAULT_HANDLER_TIMEOUT_MS = 30L;
    private static final long WORKER_SHUTDOWN_WAIT_MS = 10_000L;

    private final int parallelism;
    private final long handlerTimeoutMs;


    private final RedissonClient redisson;
    private final RBlockingDeque<String> mainQueue;
    private final RBlockingDeque<String> processingQueue;
    private final RMapCache<String, Long> leases;

    // Listener id so we can remove it on shutdown
    private final int leaseExpiryListenerId;

    private final ExecutorService workers;


    private final JobHelper jobHelper;

    // State
    private volatile boolean running = true;

    public RedissonTaskManager(JobHelper jobHelper) {
        this(jobHelper, DEFAULT_PARALLELISM, DEFAULT_HANDLER_TIMEOUT_MS);
    }

    public RedissonTaskManager(JobHelper jobHelper, int parallelism, long handlerTimeoutMs) {
        this.jobHelper = Objects.requireNonNull(jobHelper, "jobHelper");
        this.parallelism = Math.max(1, parallelism);
        this.handlerTimeoutMs = handlerTimeoutMs > 0 ? handlerTimeoutMs : DEFAULT_HANDLER_TIMEOUT_MS;

        this.redisson = RedissonConfig.getRedissonClient();
        this.mainQueue = redisson.getBlockingDeque(QUEUE_MAIN);
        this.processingQueue = redisson.getBlockingDeque(QUEUE_PROCESSING);
        this.leases = redisson.getMapCache(MAP_LEASES);

        this.leaseExpiryListenerId = leases.addListener(new LeaseExpiryListener());

        this.workers = newFixedThreadPoolWithNames(this.parallelism, "rq-worker-");
        for (int i = 0; i < this.parallelism; i++) {
            workers.submit(this::runWorkerLoop);
        }

        log.info("RedissonTaskManager started: parallelism={} lease={}ms", this.parallelism, this.handlerTimeoutMs);
    }


    @Override
    public void add(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return;
        }
        mainQueue.add(jobId);
        log.debug("Enqueued job {}", jobId);
    }


    // Worker logic


    private void runWorkerLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            String jobId = null;
            boolean shouldAck = false;

            try {

                jobId = mainQueue.takeLastAndOfferFirstTo(processingQueue.getName());


                registerLease(jobId);

                final JobEntity entity = jobHelper.fetchByJobId(jobId);
                if (entity == null) {
                    log.warn("Job {} not found; acknowledging", jobId);
                    shouldAck = true;
                    continue;
                }

                final TaskStatus current = TaskStatus.valueOf(entity.getStatus());
                if (current != TaskStatus.SUBMITTED) {
                    log.info("Job {} is {}; skipping and acknowledging", jobId, current);
                    shouldAck = true;
                    continue;
                }


                entity.setStatus(TaskStatus.PROCESSING.name());
                jobHelper.update(entity);

                final Handler handler = HandlerUtil.getTaskHandler(entity.getHandlerName());
                if (handler == null) {
                    markFailure(entity, "Handler not found: " + entity.getHandlerName());
                    shouldAck = true;
                    continue;
                }

                final HandlerBaseRequest request = HandlerSerializationUtil.deserialize(entity.getPayload());
                final HandlerBaseResponse response = handler.handle(request);

                // If someone raced us to terminal, don't overwrite
                final JobEntity latest = jobHelper.fetchByJobId(jobId);
                if (latest != null && TaskStatus.isStatusTerminal(TaskStatus.valueOf(latest.getStatus()))) {
                    log.info("Job {} already terminal as {}; acknowledging", jobId, latest.getStatus());
                    shouldAck = true;
                } else {
                    entity.setStatus(TaskStatus.SUCCESS.name());
                    entity.setResponse(response);
                    jobHelper.update(entity);
                    shouldAck = true;
                    log.info("Job {} completed successfully", jobId);
                }

            } catch (Throwable t) {

                log.warn("Job {} failed with error: {}", jobId, t.toString(), t);
            } finally {
                if (shouldAck && jobId != null) {
                    acknowledge(jobId);
                }
            }
        }
    }


    private void registerLease(String jobId) {
        // Value stores expiry time for observability; TTL drives re-delivery
        leases.fastPut(jobId,
                System.currentTimeMillis() + handlerTimeoutMs,
                handlerTimeoutMs,
                TimeUnit.MILLISECONDS);
    }

    private void acknowledge(String jobId) {
        // Best-effort; each operation is idempotent
        try {
            leases.fastRemove(jobId);
        } catch (Exception ignore) {
        }
        try {
            processingQueue.removeFirstOccurrence(jobId);
        } catch (Exception ignore) {
        }
    }

    private void markFailure(JobEntity entity, String reason) {
        try {
            entity.setStatus(TaskStatus.FAILURE.name());
            entity.setError(reason);
            jobHelper.update(entity);
        } catch (Exception e) {
            log.warn("Failed to persist FAILURE for job {}: {}", entity.getJobId(), e.toString(), e);
        }
    }


    private final class LeaseExpiryListener implements EntryExpiredListener<String, Long> {

        @Override
        public void onExpired(EntryEvent<String, Long> event) {
            final String jobId = event.getKey();
            try {
                // Clean up processing entry if it still exists
                processingQueue.removeFirstOccurrence(jobId);

                final JobEntity job = jobHelper.fetchByJobId(jobId);

                final TaskStatus status = TaskStatus.valueOf(job.getStatus());
                if (TaskStatus.isStatusTerminal(status)) {
                    log.debug("Lease expired but job {} already terminal as {}", jobId, status);
                    return;
                }

                // Requeue for at-least-once delivery

                mainQueue.offerFirst(jobId);
                log.info("Lease expired; re-queued job {}", jobId);

            } catch (Exception e) {
                log.error("Error handling lease expiry for job {}: {}", jobId, e.toString(), e);
            }

        }
    }


    @Override
    public void close() {
        shutdown();
    }

    public void shutdown() {
        running = false;

        try {
            leases.removeListener(leaseExpiryListenerId);
        } catch (Exception ignore) {
        }

        workers.shutdownNow();
        try {
            if (!workers.awaitTermination(WORKER_SHUTDOWN_WAIT_MS, TimeUnit.MILLISECONDS)) {
                log.warn("Worker pool did not terminate cleanly");
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        log.info("RedissonTaskManager shutdown complete");
    }


    private static ExecutorService newFixedThreadPoolWithNames(int size, String prefix) {
        AtomicInteger seq = new AtomicInteger(1);
        ThreadFactory tf = r -> {
            Thread t = new Thread(r, prefix + seq.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
        return Executors.newFixedThreadPool(size, tf);
    }
}
