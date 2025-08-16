package org.example.example.handlers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Handler;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.example.handlers.request.SampleHandlerRequest;
import org.example.reliable.util.HandlerSerializationUtil;

@Slf4j
public class TestHandlers {

    /**
     * Fast processing handler - completes quickly
     */
    public static class FastHandler extends Handler<SampleHandlerRequest, TestResponse> {


        @Override
        public TestResponse handle(SampleHandlerRequest request) {
            if (request != null) {
                log.info("FastHandler processing: {}", request.getMessage());

                // Simulate quick processing
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return new TestResponse(true, "Fast processing completed: " + request.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }

        @Override
        public Class<SampleHandlerRequest> getRequestType() {
            return SampleHandlerRequest.class;
        }

        @Override
        public Class<TestResponse> getResponseType() {
            return TestResponse.class;
        }
    }

    /**
     * Slow processing handler - takes time to complete
     */
    public static class SlowHandler extends Handler<SampleHandlerRequest, TestResponse> {

        @Override
        public TestResponse handle(SampleHandlerRequest request) {
            if (request != null) {
                log.info("SlowHandler processing: {}", request.getMessage());

                // Simulate slow processing
                try {
                    Thread.sleep(5000); // 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new TestResponse(false, "Processing interrupted");
                }

                return new TestResponse(true, "Slow processing completed: " + request.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }

        @Override
        public Class<SampleHandlerRequest> getRequestType() {
            return SampleHandlerRequest.class;
        }

        @Override
        public Class<TestResponse> getResponseType() {
            return TestResponse.class;
        }
    }

    /**
     * Error-prone handler - sometimes fails
     */
    public static class ErrorHandler extends Handler<SampleHandlerRequest, TestResponse> {


        @Override
        public TestResponse handle(SampleHandlerRequest request) {
            if (request != null) {
                log.info("ErrorHandler processing: {}", request.getMessage());

                // Simulate random failures
                if (Math.random() < 0.3) { // 30% chance of failure
                    throw new RuntimeException("Random processing error occurred");
                }

                return new TestResponse(true, "Error-prone processing completed: " + request.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }

        @Override
        public Class<SampleHandlerRequest> getRequestType() {
            return SampleHandlerRequest.class;
        }

        @Override
        public Class<TestResponse> getResponseType() {
            return TestResponse.class;
        }
    }

    /**
     * Batch processing handler - processes multiple items
     */
    public static class BatchHandler extends Handler<SampleHandlerRequest, TestResponse> {


        @Override
        public TestResponse handle(SampleHandlerRequest request) {
            if (request != null) {
                log.info("BatchHandler processing: {}", request.getMessage());

                // Simulate batch processing
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return new TestResponse(true, "Batch processing completed: " + request.getMessage());
            }
            throw new IllegalArgumentException("Unsupported request type");
        }

        @Override
        public Class<SampleHandlerRequest> getRequestType() {
            return SampleHandlerRequest.class;
        }

        @Override
        public Class<TestResponse> getResponseType() {
            return TestResponse.class;
        }
    }

    /**
     * Test response implementation
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestResponse extends HandlerBaseResponse {
        private boolean success;
        private String message;
        private long timestamp;

        public TestResponse(boolean success, String message) {
            super();
            this.success = success;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("TestResponse{success=%s, message='%s', timestamp=%d}",
                    success, message, timestamp);
        }
    }
}
