package org.example.reliable.util;

import org.example.reliable.client.Handler;

import java.util.HashMap;
import java.util.Map;

public class HandlerUtil {

    private static final Map<String, Handler> handlerMapping = new HashMap<>();

    public static Handler getTaskHandler(String handlerName) {
        // use reflection ,
        if(handlerMapping.containsKey(handlerName)){
            return handlerMapping.get(handlerName);
        }
        throw new IllegalArgumentException("handler not registered");
    }

    public static void registerHandler(Handler handler) {
        handlerMapping.put(handler.getClass().getSimpleName(), handler);
    }
}
