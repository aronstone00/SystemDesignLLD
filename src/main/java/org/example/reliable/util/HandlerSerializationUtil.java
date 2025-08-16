package org.example.reliable.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HandlerSerializationUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, Class<? extends HandlerBaseRequest>> requestTypeRegistry = new HashMap<>();
    private static final Map<String, Class<? extends HandlerBaseResponse>> responseTypeRegistry = new HashMap<>();
    
    /**
     * Register a request type for deserialization
     */
    public static void registerRequestType(String typeName, Class<? extends HandlerBaseRequest> requestClass) {
        requestTypeRegistry.put(typeName, requestClass);
        log.debug("Registered request type: {} -> {}", typeName, requestClass.getSimpleName());
    }

    public static void registerResponseType(String typeName, Class<? extends HandlerBaseResponse> requestClass) {
        responseTypeRegistry.put(typeName, requestClass);
        log.debug("Registered request type: {} -> {}", typeName, requestClass.getSimpleName());
    }
    
    /**
     * Serialize HandlerBaseRequest to JSON string with type information
     */
    public static String serialize(HandlerBaseRequest request) {
        if (request == null) {
            return null;
        }
        
        try {
            RequestWrapper wrapper = new RequestWrapper();
            wrapper.setType(request.getClass().getSimpleName());
            wrapper.setData(objectMapper.writeValueAsString(request));
            
            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            log.error("Error serializing HandlerBaseRequest", e);
            throw new RuntimeException("Failed to serialize HandlerBaseRequest", e);
        }
    }

    public static String serialize(HandlerBaseResponse response) {
        if (response == null) {
            return null;
        }

        try {
            RequestWrapper wrapper = new RequestWrapper();
            wrapper.setType(response.getClass().getSimpleName());
            wrapper.setData(objectMapper.writeValueAsString(response));

            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            log.error("Error serializing HandlerBaseResponse", e);
            throw new RuntimeException("Failed to serialize HandlerBaseResponse", e);
        }
    }
    
    /**
     * Deserialize JSON string back to HandlerBaseRequest
     */
    public static HandlerBaseRequest deserialize(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            RequestWrapper wrapper = objectMapper.readValue(json, RequestWrapper.class);
            Class<? extends HandlerBaseRequest> requestClass = requestTypeRegistry.get(wrapper.getType());
            
            if (requestClass == null) {
                throw new RuntimeException("Unknown request type: " + wrapper.getType());
            }
            
            return objectMapper.readValue(wrapper.getData(), requestClass);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing HandlerBaseRequest: {}", json, e);
            throw new RuntimeException("Failed to deserialize HandlerBaseRequest", e);
        }
    }

    public static HandlerBaseResponse deserializeResponse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            RequestWrapper wrapper = objectMapper.readValue(json, RequestWrapper.class);
            Class<? extends HandlerBaseResponse> requestClass = responseTypeRegistry.get(wrapper.getType());

            if (requestClass == null) {
                throw new RuntimeException("Unknown request type: " + wrapper.getType());
            }

            return objectMapper.readValue(wrapper.getData(), requestClass);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing HandlerBaseResponse: {}", json, e);
            throw new RuntimeException("Failed to deserialize HandlerBaseResponse", e);
        }
    }
    
    /**
     * Get the type name from serialized JSON without full deserialization
     */
    public static String getRequestType(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            RequestWrapper wrapper = objectMapper.readValue(json, RequestWrapper.class);
            return wrapper.getType();
        } catch (JsonProcessingException e) {
            log.error("Error extracting request type from JSON: {}", json, e);
            return null;
        }
    }
    
    /**
     * Inner class to wrap request with type information
     */
    private static class RequestWrapper {
        private String type;
        private String data;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}
