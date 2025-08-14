package Logger.config;

import Logger.enums.LogLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Builder
public class Config {

    /// Sample Config:
    /// Logger level config
    /// ================
    /// logger_type:ASYNC
    /// buffer_size:25
    ///
    /// sink level config
    /// ============
    /// Ts_format: any format
    /// log_level:INFO
    /// sink_type:STDOUT

    // todo : make this an enum
    private final String loggerType;
    private final Integer bufferSize;
    private final List<SinkConfig> sinkConfigs;

    @Getter
    @RequiredArgsConstructor
    public static class SinkConfig {
        private final String  ts_format;
        private final LogLevel minLogLevel;
        // todo : make this an enum
        private final String sinkType;

    }
}
