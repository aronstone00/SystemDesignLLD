package org.example.reliable.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.reliable.model.enums.TaskStatus;

@Data
@AllArgsConstructor
@Builder
public class ReliableTaskStatus {
    private TaskStatus status;
    private HandlerBaseResponse response;
}
