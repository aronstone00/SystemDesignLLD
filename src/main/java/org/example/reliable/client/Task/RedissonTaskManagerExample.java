package org.example.reliable.client.Task;

import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Task.impl.RedissonTaskManager;
import org.example.reliable.client.helper.JobHelper;
import org.example.reliable.client.repo.JobDao;
import org.example.reliable.client.repo.impl.PostgresJobDao;

@Slf4j
public class RedissonTaskManagerExample {
    
    public static void main(String[] args) {
        try {
            // Initialize JobDao and JobHelper
            JobDao jobDao = new PostgresJobDao();
            JobHelper jobHelper = new JobHelper(jobDao);
            
            // Create RedissonTaskManager
            RedissonTaskManager taskManager = new RedissonTaskManager(jobHelper);
            
            // Add some tasks to the queue
            taskManager.add("job-001");
            taskManager.add("job-002");
            taskManager.add("job-003");
            
            log.info("Added tasks to Redisson queue");
            
            // Keep the application running for a while to see the processing
            Thread.sleep(10000);
            
        } catch (Exception e) {
            log.error("Error in Redisson TaskManager example", e);
        }
    }
}
