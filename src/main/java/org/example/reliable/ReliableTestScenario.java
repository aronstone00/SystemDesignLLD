package org.example.reliable;

import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Reliable;
import org.example.reliable.client.Task.TaskManager;
import org.example.reliable.client.Task.impl.RedissonTaskManager;
import org.example.reliable.client.helper.JobHelper;
import org.example.reliable.client.impl.ReliableImpl;
import org.example.reliable.client.impl.TestHandlers;
import org.example.reliable.client.repo.JobDao;
import org.example.reliable.client.repo.impl.PostgresJobDao;
import org.example.reliable.model.ReliableTaskRequest;
import org.example.reliable.model.ReliableTaskStatus;
import org.example.reliable.model.enums.TaskStatus;
import org.example.reliable.model.impl.SampleHandlerRequest;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReliableTestScenario {

    private final Reliable reliable;
    private final ScheduledExecutorService statusChecker;

    public ReliableTestScenario() {
        // Initialize components
        JobDao jobDao = new PostgresJobDao();
        JobHelper jobHelper = new JobHelper(jobDao);
        TaskManager taskManager = new RedissonTaskManager(jobHelper);
        this.reliable = new ReliableImpl(jobHelper, taskManager);

        // Create status checker for monitoring
        this.statusChecker = Executors.newScheduledThreadPool(1);
    }

    public void runTestScenario() {
        log.info("=== Starting Reliable Test Scenario ===");

        try {
            // Test 1: Fast processing tasks
            testFastProcessing();

            // Test 2: Slow processing tasks
            testSlowProcessing();

            // Test 3: Error-prone tasks
            testErrorProneProcessing();

            // Test 4: Batch processing tasks
            testBatchProcessing();

            // Test 5: Duplicate submission (should return existing job)
            testDuplicateSubmission();

            // Test 6: Concurrent task submission
            testConcurrentSubmission();

            log.info("=== All tests completed successfully ===");

        } catch (Exception e) {
            log.error("Test scenario failed", e);
        } finally {
            shutdown();
        }
    }

    private void testFastProcessing() {
        log.info("--- Testing Fast Processing ---");

        SampleHandlerRequest request = new SampleHandlerRequest("Fast task", 1, false);
        ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "fast-ref-1", new TestHandlers.FastHandler());

        String jobId = reliable.submit(taskRequest);
        log.info("Submitted fast task with jobId: {}", jobId);

        // Monitor status
        monitorTaskStatus("fast-ref-1", "Fast Processing");
    }

    private void testSlowProcessing() {
        log.info("--- Testing Slow Processing ---");

        SampleHandlerRequest request = new SampleHandlerRequest("Slow task", 5, true);
        ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "slow-ref-1", new TestHandlers.SlowHandler());

        String jobId = reliable.submit(taskRequest);
        log.info("Submitted slow task with jobId: {}", jobId);

        // Monitor status
        monitorTaskStatus("slow-ref-1", "Slow Processing");
    }

    private void testErrorProneProcessing() {
        log.info("--- Testing Error-Prone Processing ---");

        for (int i = 1; i <= 5; i++) {
            SampleHandlerRequest request = new SampleHandlerRequest("Error-prone task " + i, i, false);
            ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "error-ref-" + i, new TestHandlers.ErrorHandler());

            String jobId = reliable.submit(taskRequest);
            log.info("Submitted error-prone task {} with jobId: {}", i, jobId);

            // Monitor status
            monitorTaskStatus("error-ref-" + i, "Error-Prone Processing " + i);
        }
    }

    private void testBatchProcessing() {
        log.info("--- Testing Batch Processing ---");

        for (int i = 1; i <= 3; i++) {
            SampleHandlerRequest request = new SampleHandlerRequest("Batch task " + i, i, true);
            ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "batch-ref-" + i, new TestHandlers.BatchHandler());

            String jobId = reliable.submit(taskRequest);
            log.info("Submitted batch task {} with jobId: {}", i, jobId);

            // Monitor status
            monitorTaskStatus("batch-ref-" + i, "Batch Processing " + i);
        }
    }

    private void testDuplicateSubmission() {
        log.info("--- Testing Duplicate Submission ---");

        SampleHandlerRequest request = new SampleHandlerRequest("Duplicate task", 1, false);
        ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "duplicate-ref", new TestHandlers.FastHandler());

        // First submission
        String jobId1 = reliable.submit(taskRequest);
        log.info("First submission - jobId: {}", jobId1);

        // Second submission with same reference ID
        String jobId2 = reliable.submit(taskRequest);
        log.info("Second submission - jobId: {}", jobId2);

        if (jobId1.equals(jobId2)) {
            log.info("✓ Duplicate submission correctly returned same jobId");
        } else {
            log.warn("✗ Duplicate submission returned different jobIds");
        }

        // Monitor status
        monitorTaskStatus("duplicate-ref", "Duplicate Submission");
    }

    private void testConcurrentSubmission() {
        log.info("--- Testing Concurrent Submission ---");

        for (int i = 1; i <= 10; i++) {
            final int taskNum = i;
            new Thread(() -> {
                try {
                    SampleHandlerRequest request = new SampleHandlerRequest("Concurrent task " + taskNum, taskNum, false);
                    ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "concurrent-ref-" + taskNum, new TestHandlers.FastHandler());

                    String jobId = reliable.submit(taskRequest);
                    log.info("Submitted concurrent task {} with jobId: {}", taskNum, jobId);

                    // Monitor status
                    monitorTaskStatus("concurrent-ref-" + taskNum, "Concurrent Processing " + taskNum);

                } catch (Exception e) {
                    log.error("Error in concurrent task {}", taskNum, e);
                }
            }).start();
        }
    }

    private void monitorTaskStatus(String referenceId, String testName) {
        statusChecker.scheduleAtFixedRate(() -> {
            try {
                ReliableTaskStatus status = reliable.status(referenceId);
                log.info("[{}] Status: {} - {}", testName, status.getStatus(), status.getResponse());

                // Stop monitoring if task is in terminal state
                if (TaskStatus.isStatusTerminal(status.getStatus())) {
                    log.info("[{}] Task completed with status: {}", testName, status.getStatus());
                    return;
                }

            } catch (Exception e) {
                log.error("Error checking status for {}", referenceId, e);
            }
        }, 1, 2, TimeUnit.SECONDS);
    }

    private void shutdown() {
        log.info("Shutting down test scenario...");
        if (statusChecker != null) {
            statusChecker.shutdown();
            try {
                if (!statusChecker.awaitTermination(10, TimeUnit.SECONDS)) {
                    statusChecker.shutdownNow();
                }
            } catch (InterruptedException e) {
                statusChecker.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        ReliableTestScenario scenario = new ReliableTestScenario();
        scenario.runTestScenario();
    }
}
