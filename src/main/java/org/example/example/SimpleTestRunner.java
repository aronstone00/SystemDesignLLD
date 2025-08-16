package org.example.example;

import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Reliable;
import org.example.reliable.client.Task.TaskManager;
import org.example.reliable.client.Task.impl.RedissonTaskManager;
import org.example.reliable.client.helper.JobHelper;
import org.example.reliable.client.impl.ReliableImpl;
import org.example.example.handlers.TestHandlers;
import org.example.reliable.client.repo.JobDao;
import org.example.reliable.client.repo.impl.PostgresJobDao;
import org.example.reliable.model.ReliableTaskRequest;
import org.example.reliable.model.ReliableTaskStatus;
import org.example.example.handlers.request.SampleHandlerRequest;

@Slf4j
public class SimpleTestRunner {
    
    private final Reliable reliable;
    
    public SimpleTestRunner() {
        // Initialize components
        JobDao jobDao = new PostgresJobDao();
        JobHelper jobHelper = new JobHelper(jobDao);
        TaskManager taskManager = new RedissonTaskManager(jobHelper);
        this.reliable = new ReliableImpl(jobHelper, taskManager);
    }
    
    public void runSimpleTest() {
        log.info("=== Starting Simple Reliable Test ===");

        try {
            // Test 1: Submit a fast task
            log.info("--- Testing Fast Task ---");
            SampleHandlerRequest request = new SampleHandlerRequest("Hello World", 1, false);
            ReliableTaskRequest taskRequest = new ReliableTaskRequest(request, "test1-ref-1", new TestHandlers.FastHandler());
            
            String jobId = reliable.submit(taskRequest);
            log.info("Submitted task with jobId: {}", jobId);
            
            // Wait a bit and check status
            Thread.sleep(2000);
            ReliableTaskStatus status = reliable.status("test1-ref-1");
            log.info("Task status: {} - {}", status.getStatus(), status.getResponse());
            
            // Test 2: Submit a slow task
            log.info("--- Testing Slow Task ---");
            SampleHandlerRequest slowRequest = new SampleHandlerRequest("Slow processing", 5, true);
            ReliableTaskRequest slowTaskRequest = new ReliableTaskRequest(slowRequest, "test1-ref-2", new TestHandlers.SlowHandler());
            
            String slowJobId = reliable.submit(slowTaskRequest);
            log.info("Submitted slow task with jobId: {}", slowJobId);
            
            // Check status immediately (should be processing)
            status = reliable.status("test1-ref-2");
            log.info("Slow task status: {} - {}", status.getStatus(), status.getResponse());
            
            // Test 3: Submit multiple tasks
            log.info("--- Testing Multiple Tasks ---");
            for (int i = 1; i <= 3; i++) {
                SampleHandlerRequest multiRequest = new SampleHandlerRequest("Task " + i, i, false);
                ReliableTaskRequest multiTaskRequest = new ReliableTaskRequest(multiRequest, "test1-ref-multi-" + i, new TestHandlers.FastHandler());
                
                String multiJobId = reliable.submit(multiTaskRequest);
                log.info("Submitted task {} with jobId: {}", i, multiJobId);
            }
            
            // Wait for all tasks to complete
            Thread.sleep(5000);
            
            // Check status of all tasks
            for (int i = 1; i <= 3; i++) {
                status = reliable.status("test1-ref-multi-" + i);
                log.info("Task {} status: {} - {}", i, status.getStatus(), status.getResponse());
            }
            
            log.info("=== Simple test completed successfully ===");
            
        } catch (Exception e) {
            log.error("Test failed", e);
        }


    }
    
    public static void main(String[] args) {
        SimpleTestRunner runner = new SimpleTestRunner();
        runner.runSimpleTest();
    }
}
