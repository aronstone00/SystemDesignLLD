package org.example.example.handlers.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.reliable.model.HandlerBaseRequest;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SampleHandlerRequest extends HandlerBaseRequest {
    private String message;
    private int priority;
    private boolean urgent;
}
