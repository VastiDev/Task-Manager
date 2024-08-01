package com.vastidev.taskmanager.exceptions;

import java.util.UUID;

public class AppUserNotFoundException extends RuntimeException {
    public AppUserNotFoundException(UUID idUser) {
        super("User not found " + idUser);
    }
}