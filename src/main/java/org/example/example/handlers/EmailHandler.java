package org.example.example.handlers;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.Handler;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.example.handlers.request.EmailHandlerRequest;
import org.example.reliable.util.HandlerSerializationUtil;

@Slf4j
public class EmailHandler extends Handler {

    static {
        // Register the request type for deserialization
        HandlerSerializationUtil.registerRequestType("EmailHandlerRequest", EmailHandlerRequest.class);
        HandlerSerializationUtil.registerResponseType("EmailHandlerResponse", EmailHandlerResponse.class);
    }

    @Override
    public HandlerBaseResponse handle(HandlerBaseRequest request) {
        if (request instanceof EmailHandlerRequest) {
            EmailHandlerRequest emailRequest = (EmailHandlerRequest) request;
            log.info("Processing email request: to={}, subject={}, from={}",
                    emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getFrom());

            // Simulate email sending
            boolean success = sendEmail(emailRequest);

            return new EmailHandlerResponse(success,
                    success ? "Email sent successfully" : "Failed to send email");
        }

        throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
    }

    private boolean sendEmail(EmailHandlerRequest request) {
        // Simulate email sending logic
        log.info("Sending email to: {} with subject: {}", request.getTo(), request.getSubject());
        return true; // Simulate success
    }

    /**
     * Email response implementation
     */
    @Getter
    @Setter

    public static class EmailHandlerResponse extends HandlerBaseResponse {
        private boolean success;
        private String message;

        public EmailHandlerResponse(boolean success, String message) {
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
