package org.example.reliable.util;

import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.impl.SampleHandler;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.model.impl.SampleHandlerRequest;

@Slf4j
public class SerializationExample {
    
    public static void main(String[] args) {
        try {
            // Create a sample request
            SampleHandlerRequest request = new SampleHandlerRequest("Hello World", 5, true);
            log.info("Original request: {}", request);
            
            // Serialize the request
            String serialized = HandlerSerializationUtil.serialize(request);
            log.info("Serialized request: {}", serialized);
            
            // Deserialize the request
            HandlerBaseRequest deserialized = HandlerSerializationUtil.deserialize(serialized);
            log.info("Deserialized request: {}", deserialized);
            
            // Verify they are equal
            if (request.equals(deserialized)) {
                log.info("Serialization/deserialization successful!");
            } else {
                log.error("Serialization/deserialization failed!");
            }
            
            // Test with handler
            SampleHandler handler = new SampleHandler();
            HandlerBaseResponse response = handler.handle(deserialized);
            log.info("Handler response: {}", response);
            
            // Test type extraction
            String requestType = HandlerSerializationUtil.getRequestType(serialized);
            log.info("Extracted request type: {}", requestType);
            
        } catch (Exception e) {
            log.error("Error in serialization example", e);
        }
    }
}
