package com.vastidev.taskmanager.exceptions;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(UUID id){
        super("Task not found " + id);
    }
}
