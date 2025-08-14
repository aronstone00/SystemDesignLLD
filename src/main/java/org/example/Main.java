package org.example;


import Logger.config.Config;
import Logger.enums.LogLevel;
import Logger.service.Logger;
import Logger.service.impl.AsyncLogger;
import Logger.service.impl.SyncLogger;
import com.google.errorprone.annotations.ThreadSafe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {



        Config config = Config.builder()
                .loggerType("SYNC")
                .bufferSize(5)
                .sinkConfigs(List.of(new Config.SinkConfig("DD-mm-YYYY-HH-MM-SS", LogLevel.INFO, "FILE"),
                        new Config.SinkConfig("YYYY-mm-DD-HH-MM-SS", LogLevel.WARN, "STDOUT")))
                .build();



        Logger log = new AsyncLogger(config);
        log.info("Info warning");
        log.fatal("Fatal warning");
        log.debug("Debug warning");


//        ExecutorService executorService = Executors.newFixedThreadPool(14);
//
//        for(int i = 0;i<4;i++){
//            String message  = "Info message "  + "by" + i;
//            executorService.submit(()->log.info(message));
//        }
//
//
//        for(int i = 4;i<8;i++){
//            String message  = "Warn message "  + "by" + i;
//            executorService.submit(()->log.warn(message));
//        }
//
//
//        for(int i = 8;i<12;i++){
//            String message  = "Debug message "  + "by" + i;
//            executorService.submit(()->log.debug(message));
//        }



        System.out.println("Main method end");

    }









    void synch(){
        Config.SinkConfig sinkConfig1 = new Config.SinkConfig("dd-mm-YYYY-HH-MM-SS", LogLevel.INFO, "FILE");

        Config.SinkConfig sinkConfig2 = new Config.SinkConfig("YYYY-mm-DD-HH-MM-SS", LogLevel.WARN, "STDOUT");

        Config config = Config.builder()
                .loggerType("SYNC")
                .bufferSize(null)
                .sinkConfigs(List.of(new Config.SinkConfig("DD-mm-YYYY-HH-MM-SS", LogLevel.INFO, "FILE"), new Config.SinkConfig("YYYY-mm-DD-HH-MM-SS", LogLevel.WARN, "STDOUT")))
                .build();




        Logger log = new SyncLogger(config);


        log.info("Info message");
        log.warn("Warn message");
        log.debug("Debug message");
        log.error("Error message");


    }
}