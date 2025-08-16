package org.example.reliable.client;

import org.example.reliable.model.HandlerBaseRequest;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.util.HandlerSerializationUtil;

import static org.example.reliable.util.HandlerUtil.registerHandler;

public abstract class Handler<T extends HandlerBaseRequest, R extends HandlerBaseResponse> {

    public Handler() {
        registerHandler(this);
        HandlerSerializationUtil.registerRequestType(getRequestType().getSimpleName(), getRequestType());
        HandlerSerializationUtil.registerResponseType(getResponseType().getSimpleName(), getResponseType());
    }


    public abstract R handle(T request);

    public abstract Class<T> getRequestType();

    public abstract Class<R> getResponseType();
}
