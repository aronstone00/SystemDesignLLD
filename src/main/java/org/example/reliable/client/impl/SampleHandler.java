package org.example.reliable.client.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Handler;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.model.impl.SampleHandlerRequest;
import org.example.reliable.util.HandlerSerializationUtil;

@Slf4j
public class SampleHandler extends Handler {
    
    static {
        // Register the request type for deserialization
        HandlerSerializationUtil.registerRequestType("SampleHandlerRequest", SampleHandlerRequest.class);
        HandlerSerializationUtil.registerResponseType("SampleHandlerResponse", SampleHandlerResponse.class);
    }
    
    @Override
    public HandlerBaseResponse handle(HandlerBaseRequest request) {
        if (request instanceof SampleHandlerRequest) {
            SampleHandlerRequest sampleRequest = (SampleHandlerRequest) request;
            log.info("Processing sample request: message={}, priority={}, urgent={}", 
                    sampleRequest.getMessage(), sampleRequest.getPriority(), sampleRequest.isUrgent());
            
            // Process the request and return response
            return new SampleHandlerResponse(true, "Processed: " + sampleRequest.getMessage());
        }
        
        throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
    }
    
    /**
     * Sample response implementation
     */
    @Getter
    @Setter
    public static class SampleHandlerResponse extends HandlerBaseResponse {
        private final boolean success;
        private final String message;
        
        public SampleHandlerResponse(boolean success, String message) {
            super();
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
