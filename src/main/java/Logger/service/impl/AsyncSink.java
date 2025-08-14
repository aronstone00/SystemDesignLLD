package Logger.service.impl;

import Logger.enums.LogLevel;
import Logger.model.MessageInfo;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncSink {

    private final String tsFormat;
    private final Integer buffer;
    private final String type;
    private final LogLevel minLogLevelSupport;
    private final Queue<MessageInfo> messageInfoQueue;

    public void accept(String message, LogLevel messageLevel) {
        if (messageLevel.getPriority() < minLogLevelSupport.getPriority()) {
            return;
        }
        if (messageInfoQueue.size() >= buffer) {

            throw new IllegalArgumentException("size full");
        }
        synchronized (messageInfoQueue) {
            if (messageInfoQueue.size() >= buffer) {
                throw new IllegalArgumentException("size full");
            }
            messageInfoQueue.add(MessageInfo.builder().message(message).logLevel(messageLevel).build());
        }

    }


    private void execute(String message, LogLevel messageLevel) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(tsFormat);
        String log = LocalDateTime.now().format(format) + " " + "[" + messageLevel.toString() + "]" + " " + message;
        System.out.println(log);
    }

    // constructor
    public AsyncSink(String tsFormat, String type, LogLevel minLogLevelSupport, Integer buffer) {
        this.tsFormat = tsFormat;
        this.type = type;
        this.minLogLevelSupport = minLogLevelSupport;
        this.messageInfoQueue = new LinkedList<>();
        this.buffer = buffer;
        init();

    }


    void init() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::runIndefinately);

    }

    private void runIndefinately() {
        while (true) {
            while (!messageInfoQueue.isEmpty()) {
                MessageInfo messageInfo = messageInfoQueue.poll();
                execute(messageInfo.getMessage(), messageInfo.getLogLevel());
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
