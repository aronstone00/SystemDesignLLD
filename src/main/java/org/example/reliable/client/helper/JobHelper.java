package org.example.reliable.client.helper;

import org.example.reliable.client.repo.JobDao;
import org.example.reliable.client.repo.entity.JobEntity;
import org.example.reliable.model.ReliableTaskRequest;
import org.example.reliable.model.enums.TaskStatus;
import org.example.reliable.util.HandlerSerializationUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class JobHelper {

    private final JobDao jobDao;


    public JobHelper(JobDao jobDao) {
        this.jobDao = jobDao;
    }

    //jobId
    public JobEntity validateAndPersist(ReliableTaskRequest request) {
        String jobId = null;
        Optional<JobEntity> entityOpt = jobDao.fetchJobsByRefId(request.getReferenceId());
        if (entityOpt.isEmpty()) {
            jobId = UUID.randomUUID().toString();

            // Serialize the payload if it's a HandlerBaseRequest
            String serializedPayload = HandlerSerializationUtil.serialize(request.getPayload());
            JobEntity jobEntity = JobEntity.builder().jobId(jobId)
                    .referenceId(request.getReferenceId())
                    .handlerName(request.getHandler().getClass().getSimpleName())
                    .payload(serializedPayload)
                    .status(TaskStatus.SUBMITTED.name())
                    .payloadHash(serializedPayload)
                    .createdAt(System.currentTimeMillis())
                    .updatedAt(System.currentTimeMillis())
                    .build();
            jobDao.persist(jobEntity);
            return jobEntity;
        }
        String reqHash = HandlerSerializationUtil.serialize(request.getPayload());;
        JobEntity persistedEntity = entityOpt.get();
        if (Objects.equals(persistedEntity.getPayloadHash(), reqHash)) {
            return persistedEntity;
        }
        throw new IllegalArgumentException("conflict");
    }

    public JobEntity fetchByRefId(String refId) {
        Optional<JobEntity> entityOptional = jobDao.fetchJobsByRefId(refId);
        return entityOptional.orElseThrow(() -> new IllegalArgumentException("job doesn't exist"));

    }

    public JobEntity fetchByJobId(String jobId) {
        JobEntity entity = jobDao.fetchByJobId(jobId);
        if (entity == null) {
            throw new RuntimeException("job doesn't exist");
        }
        return entity;
    }

    public void update(JobEntity entity) {
        entity.setUpdatedAt(System.currentTimeMillis());
        jobDao.update(entity);
    }
}
