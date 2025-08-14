package Logger.service.impl;

import Logger.config.Config;
import Logger.enums.LogLevel;
import Logger.model.MessageInfo;
import Logger.service.Logger;
import lombok.extern.java.Log;

import java.util.*;

public class AsyncLogger implements Logger {
    public final Config config;
    private final List<AsyncSink> sinks;

    public AsyncLogger(Config config) {
        this.config = config;
        this.sinks = new ArrayList<>();
        config.getSinkConfigs().forEach(sinkConfig -> {
            sinks.add(new AsyncSink(sinkConfig.getTs_format(), sinkConfig.getSinkType(), sinkConfig.getMinLogLevel(), config.getBufferSize()));
        });


    }


    private void ingestInSink(LogLevel level, String message) {
        sinks.forEach(sink -> sink.accept(message, level));
    }

    @Override
    public void debug(String message) {
        ingestInSink(LogLevel.DEBUG, message);

    }

    @Override
    public void info(String message) {

        ingestInSink(LogLevel.INFO, message);
    }

    @Override
    public void warn(String message) {
        ingestInSink(LogLevel.WARN, message);

    }

    @Override
    public void error(String message) {
        ingestInSink(LogLevel.ERROR, message);

    }

    @Override
    public void fatal(String message) {
        ingestInSink(LogLevel.FATAL, message);

    }


}
