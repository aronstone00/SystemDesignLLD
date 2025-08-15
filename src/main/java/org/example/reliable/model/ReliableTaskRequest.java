package org.example.reliable.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.reliable.client.Handler;

@AllArgsConstructor
@Data
public class ReliableTaskRequest {

    private HandlerBaseRequest payload;
    private String referenceId;
    private Handler handler;

}
