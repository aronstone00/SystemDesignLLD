package org.example.reliable.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessingQueuePayload {
    private String  jobId;
    private Long expiryTime;
}
