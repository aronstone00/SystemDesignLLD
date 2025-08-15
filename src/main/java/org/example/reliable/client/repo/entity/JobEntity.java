package org.example.reliable.client.repo.entity;

import lombok.Builder;
import lombok.Data;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;

@Data
@Builder
public class JobEntity {
    private String jobId;
    private String status;
    private String payload;
    private String payloadHash;
    private String referenceId;
    private String handlerName;
    private HandlerBaseResponse response;
    private String error;
    private Long createdAt;
    private Long updatedAt;

}
