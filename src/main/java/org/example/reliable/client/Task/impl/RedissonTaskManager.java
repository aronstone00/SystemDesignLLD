package org.example.reliable.client.Task.impl;

import lombok.Data;
import lombok.SneakyThrows;
import org.example.reliable.client.Handler;
import org.example.reliable.client.Task.TaskManager;
import org.example.reliable.client.config.RedissonConfig;
import org.example.reliable.client.helper.JobHelper;
import org.example.reliable.client.repo.entity.JobEntity;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.model.ProcessingQueuePayload;
import org.example.reliable.model.enums.TaskStatus;
import org.example.reliable.util.HandlerSerializationUtil;
import org.example.reliable.util.HandlerUtil;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data

//todo : make this singelton
public class RedissonTaskManager implements TaskManager {


    private final int parallelTask = 5;
    private final RedissonClient redissonClient;
    private final Long handlerTimeoutInMs = 30000L;
    
    private RQueue<String> taskQueue;
    private RQueue<ProcessingQueuePayload> processingQueue;
    private final JobHelper jobHelper;

    public RedissonTaskManager(JobHelper jobHelper) {
        this.redissonClient = RedissonConfig.getRedissonClient();
        this.jobHelper = jobHelper;
        
        // Initialize Redisson queues
        this.taskQueue = redissonClient.getQueue("taskQueue");
        this.processingQueue = redissonClient.getQueue("processingQueue");
        
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ExecutorService processingExecutor = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            try {
                executeTask();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        processingExecutor.submit(() -> {
            try {
                initiateReaper();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }


    private void executeTask() throws InterruptedException {
        while (true) {
            String taskJobId = taskQueue.poll();
            if (taskJobId != null) {
                synchronized (taskJobId) {
                    addToProcessingQueue(taskJobId);
                    executeTaskForJob(taskJobId);
                }
            } else {
                Thread.sleep(100);
            }
        }
    }


    private void initiateReaper() throws InterruptedException {
        while (true) {
            ProcessingQueuePayload payload = processingQueue.peek();
            if (payload != null) {
                if (payload.getExpiryTime() <= System.currentTimeMillis()) {
                    processingQueue.poll(); // Remove from queue
                    updateJobPostExpiry(payload.getJobId());
                }
            } else {
                Thread.sleep(100);
            }
        }
    }




    private void updateJobPostExpiry(String jobId) {
        JobEntity job = jobHelper.fetchByJobId(jobId);
        if (TaskStatus.isStatusTerminal(TaskStatus.valueOf(job.getStatus()))) {
            return;
        }
        updateJobToFailure(job, "task expired");
    }


    private void updateJobToFailure(JobEntity entity, String reason) {
        entity.setStatus(TaskStatus.FAILURE.name());
        entity.setError(reason);
        jobHelper.update(entity);
    }

    private void executeTaskForJob(String jobId) {

        JobEntity entity = jobHelper.fetchByJobId(jobId);
        TaskStatus existingStatus = TaskStatus.valueOf(entity.getStatus());
        if(existingStatus!=TaskStatus.SUBMITTED){
            return;
        }
        entity.setStatus(TaskStatus.PROCESSING.name());
        jobHelper.update(entity);

        Handler taskHandler = HandlerUtil.getTaskHandler(entity.getHandlerName());
        HandlerBaseResponse response = null;
        try {
            // Deserialize the payload back to HandlerBaseRequest
            HandlerBaseRequest request = HandlerSerializationUtil.deserialize(entity.getPayload());
            response = taskHandler.handle(request);
        } catch (Exception ex) {
            updateJobToFailure(entity, ex.getMessage());
        }


        if (TaskStatus.isStatusTerminal(TaskStatus.valueOf(entity.getStatus()))) {
            return;
        }

        entity.setStatus(TaskStatus.SUCCESS.name());
        entity.setResponse(response);
        jobHelper.update(entity);
    }


    private void addToProcessingQueue(String jobId) {
        Long expiryTs = System.currentTimeMillis() + handlerTimeoutInMs;
        processingQueue.add(ProcessingQueuePayload.builder()
                .jobId(jobId)
                .expiryTime(expiryTs)
                .build());
    }




    @Override
    public void add(String jobId) {
        taskQueue.add(jobId);
    }
}
