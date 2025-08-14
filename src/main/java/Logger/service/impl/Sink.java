package Logger.service.impl;

import Logger.enums.LogLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class Sink {

    private final String tsFormat;
    private final String type;
    private final LogLevel minLogLevelSupport;


    public  void accept(String message, LogLevel messageLevel) {
        if (messageLevel.getPriority() < minLogLevelSupport.getPriority()) {
            return;
        }
        // todo : make this a global variable
        DateTimeFormatter format = DateTimeFormatter.ofPattern(tsFormat);
        //  todo :  use stringbuilder
        String log = LocalDateTime.now().format(format) + " " + "[" + messageLevel.toString() + "]" + " " + message;
        System.out.println(log);
    }
}
