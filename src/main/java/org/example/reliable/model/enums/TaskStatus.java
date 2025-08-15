package org.example.reliable.model.enums;

import java.util.List;

public enum TaskStatus {

    SUBMITTED,
    PROCESSING,
    SUCCESS,
    FAILURE;


    private static final List<TaskStatus> terminalStatus = List.of(SUCCESS,FAILURE);


    public static boolean isStatusTerminal(TaskStatus status){
        return terminalStatus.contains(status);
    }

}
