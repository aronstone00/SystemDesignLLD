package org.example.reliable.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.reliable.model.HandlerBaseRequest;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmailHandlerRequest extends HandlerBaseRequest {
    private String to;
    private String subject;
    private String body;
    private String from;
    private boolean isHtml;
}
