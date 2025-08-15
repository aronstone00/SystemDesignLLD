package org.example.reliable.client.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Handler;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.model.impl.SampleHandlerRequest;
import org.example.reliable.util.HandlerSerializationUtil;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestHandlers {
    
    /**
     * Fast processing handler - completes quickly
     */
    public static class FastHandler extends Handler {
        
        static {
            HandlerSerializationUtil.registerRequestType("SampleHandlerRequest", SampleHandlerRequest.class);
            HandlerSerializationUtil.registerResponseType("SampleHandlerResponse", SampleHandler.SampleHandlerResponse.class);
            HandlerSerializationUtil.registerResponseType("TestResponse", TestResponse.class);HandlerSerializationUtil.registerResponseType("TestResponse", TestResponse.class);
        }
        
        @Override
        public HandlerBaseResponse handle(HandlerBaseRequest request) {
            if (request instanceof SampleHandlerRequest) {
                SampleHandlerRequest sampleRequest = (SampleHandlerRequest) request;
                log.info("FastHandler processing: {}", sampleRequest.getMessage());
                
                // Simulate quick processing
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                return new TestResponse(true, "Fast processing completed: " + sampleRequest.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }
    }
    
    /**
     * Slow processing handler - takes time to complete
     */
    public static class SlowHandler extends Handler {
        
        static {
            HandlerSerializationUtil.registerRequestType("SampleHandlerRequest", SampleHandlerRequest.class);
            HandlerSerializationUtil.registerResponseType("SampleHandlerResponse", SampleHandler.SampleHandlerResponse.class);
            HandlerSerializationUtil.registerResponseType("TestResponse", TestResponse.class);
        }
        
        @Override
        public HandlerBaseResponse handle(HandlerBaseRequest request) {
            if (request instanceof SampleHandlerRequest) {
                SampleHandlerRequest sampleRequest = (SampleHandlerRequest) request;
                log.info("SlowHandler processing: {}", sampleRequest.getMessage());
                
                // Simulate slow processing
                try {
                    Thread.sleep(5000); // 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new TestResponse(false, "Processing interrupted");
                }
                
                return new TestResponse(true, "Slow processing completed: " + sampleRequest.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }
    }
    
    /**
     * Error-prone handler - sometimes fails
     */
    public static class ErrorHandler extends Handler {
        
        static {
            HandlerSerializationUtil.registerRequestType("SampleHandlerRequest", SampleHandlerRequest.class);
            HandlerSerializationUtil.registerResponseType("SampleHandlerResponse", SampleHandler.SampleHandlerResponse.class);
            HandlerSerializationUtil.registerResponseType("TestResponse", TestResponse.class);
        }
        
        @Override
        public HandlerBaseResponse handle(HandlerBaseRequest request) {
            if (request instanceof SampleHandlerRequest) {
                SampleHandlerRequest sampleRequest = (SampleHandlerRequest) request;
                log.info("ErrorHandler processing: {}", sampleRequest.getMessage());
                
                // Simulate random failures
                if (Math.random() < 0.3) { // 30% chance of failure
                    throw new RuntimeException("Random processing error occurred");
                }
                
                return new TestResponse(true, "Error-prone processing completed: " + sampleRequest.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }
    }
    
    /**
     * Batch processing handler - processes multiple items
     */
    public static class BatchHandler extends Handler {
        
        static {
            HandlerSerializationUtil.registerRequestType("SampleHandlerRequest", SampleHandlerRequest.class);
            HandlerSerializationUtil.registerResponseType("SampleHandlerResponse", SampleHandler.SampleHandlerResponse.class);
            HandlerSerializationUtil.registerResponseType("TestResponse", TestResponse.class);
        }
        
        @Override
        public HandlerBaseResponse handle(HandlerBaseRequest request) {
            if (request instanceof SampleHandlerRequest) {
                SampleHandlerRequest sampleRequest = (SampleHandlerRequest) request;
                log.info("BatchHandler processing: {}", sampleRequest.getMessage());
                
                // Simulate batch processing
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                return new TestResponse(true, "Batch processing completed: " + sampleRequest.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }
    }
    
    /**
     * Test response implementation
     */
    @Getter
    @Setter
    public static class TestResponse extends HandlerBaseResponse {
        private final boolean success;
        private final String message;
        private final long timestamp;
        
        public TestResponse(boolean success, String message) {
            super();
            this.success = success;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("TestResponse{success=%s, message='%s', timestamp=%d}", 
                    success, message, timestamp);
        }
    }
}
