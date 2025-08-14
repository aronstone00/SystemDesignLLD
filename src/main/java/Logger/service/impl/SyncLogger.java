package Logger.service.impl;

import Logger.config.Config;
import Logger.enums.LogLevel;
import Logger.service.Logger;

import java.util.ArrayList;
import java.util.List;

//singelton class

public class SyncLogger implements Logger {

    private static SyncLogger localInstance;
    public final Config config;
    private final List<Sink> sinks;
    private final String loggerType = "SYNCH";


    public SyncLogger(Config config) {
        this.config = config;
        sinks = new ArrayList<>();
        config.getSinkConfigs().forEach(sinkConfig -> {
            sinks.add(new Sink(sinkConfig.getTs_format(), sinkConfig.getSinkType(), sinkConfig.getMinLogLevel()));
        });
    }

    @Deprecated
    public static SyncLogger getInstance(Config config) {
        if(localInstance == null){
            synchronized (SyncLogger.class){
                if (localInstance == null) {
                    localInstance = new SyncLogger(config);
                }
            }
        }


        return localInstance;
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
