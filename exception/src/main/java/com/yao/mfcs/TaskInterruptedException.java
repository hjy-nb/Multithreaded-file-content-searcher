package com.yao.mfcs;

public class TaskInterruptedException extends RuntimeException{

    public TaskInterruptedException(String message) {
        super(message);
    }

    public TaskInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
