package org.example.reliable.client.repo;

import org.example.reliable.client.repo.entity.JobEntity;


import java.util.Optional;

public interface JobDao {

    Optional<JobEntity> fetchJobsByRefId(String refId);

    void  persist(JobEntity jobEntity);
    JobEntity fetchByJobId(String jobId);

    void update(JobEntity jobEntity);
}
