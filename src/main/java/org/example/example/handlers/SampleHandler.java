package org.example.example.handlers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Handler;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.example.handlers.request.SampleHandlerRequest;
import org.example.reliable.util.HandlerSerializationUtil;

@Slf4j
public class SampleHandler extends Handler<SampleHandlerRequest, SampleHandler.SampleHandlerResponse> {


    @Override
    public SampleHandlerResponse handle(SampleHandlerRequest request) {
        if (request != null) {
            SampleHandlerRequest sampleRequest = request;
            log.info("Processing sample request: message={}, priority={}, urgent={}",
                    sampleRequest.getMessage(), sampleRequest.getPriority(), sampleRequest.isUrgent());

            // Process the request and return response
            return new SampleHandlerResponse(true, "Processed: " + sampleRequest.getMessage());
        }

        throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
    }

    @Override
    public Class getRequestType() {
        return SampleHandlerRequest.class;
    }

    @Override
    public Class getResponseType() {
        return SampleHandlerResponse.class;
    }

    /**
     * Sample response implementation
     */
    @Getter
    @Setter
    public static class SampleHandlerResponse extends HandlerBaseResponse {
        private boolean success;
        private String message;

        public SampleHandlerResponse(boolean success, String message) {
            super();
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
