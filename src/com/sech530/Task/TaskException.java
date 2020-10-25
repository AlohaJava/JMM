package com.sech530.Task;

public class TaskException extends RuntimeException{
    public TaskException(Exception exception) {
        super(exception);
    }
}
