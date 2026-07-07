package com.taskmanagementsystem.exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(String msg){
        super(msg);
    }
}
