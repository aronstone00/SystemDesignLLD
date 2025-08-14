package Logger.model;

import Logger.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MessageInfo {
    private String message;
    private LogLevel logLevel;
}
