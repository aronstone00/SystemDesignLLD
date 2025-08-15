package org.example.reliable.client;

import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.util.HandlerSerializationUtil;

import static org.example.reliable.util.HandlerUtil.registerHandler;

public abstract class Handler {

    public Handler() {
        registerHandler(this);
    }

    /**
     * Handle the request with proper type safety
     * @param request The typed request object
     * @return Response from the handler
     */
    public abstract HandlerBaseResponse handle(HandlerBaseRequest request);
}
