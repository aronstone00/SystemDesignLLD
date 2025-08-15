package org.example.reliable.client.repo;

import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.repo.entity.JobEntity;
import org.example.reliable.client.repo.impl.PostgresJobDao;
import org.example.reliable.model.HandlerBaseResponse;

import java.util.Optional;

@Slf4j
public class JobDaoExample {
    
    public static void main(String[] args) {
        JobDao jobDao = new PostgresJobDao();
        
        // Example usage
        try {
            // Create a sample job
            JobEntity job = JobEntity.builder()
                    .jobId("job-123")
                    .status("PENDING")
                    .payload("{\"data\": \"sample payload\"}")
                    .payloadHash("abc123hash")
                    .referenceId("ref-456")
                    .handlerName("SampleHandler")
                    .response(null) // HandlerBaseResponse is abstract, setting to null for example
                    .error(null)
                    .createdAt(System.currentTimeMillis())
                    .updatedAt(System.currentTimeMillis())
                    .build();
            
            // Persist the job
            jobDao.persist(job);
            log.info("Job persisted: {}", job.getJobId());
            
            // Fetch by job ID
            JobEntity fetchedJob = jobDao.fetchByJobId("job-123");
            if (fetchedJob != null) {
                log.info("Fetched job: {}", fetchedJob.getJobId());
            }
            
            // Fetch by reference ID
            Optional<JobEntity> jobByRef = jobDao.fetchJobsByRefId("ref-456");
            jobByRef.ifPresent(j -> log.info("Found job by ref: {}", j.getJobId()));
            
            // Update the job
            job.setStatus("COMPLETED");
            job.setUpdatedAt(System.currentTimeMillis());
            jobDao.update(job);
            log.info("Job updated: {}", job.getJobId());
            
        } catch (Exception e) {
            log.error("Error in example", e);
        }
    }
}
