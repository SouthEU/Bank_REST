package com.example.bankcards.exception.user;

public class UserAlreadyDeactivatedException extends RuntimeException {
    public UserAlreadyDeactivatedException(String message) {
        super(message);
    }
}
