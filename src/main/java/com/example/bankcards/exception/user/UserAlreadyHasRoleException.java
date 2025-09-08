package com.example.bankcards.exception.user;

public class UserAlreadyHasRoleException extends RuntimeException {
    public UserAlreadyHasRoleException(String message) {
        super(message);
    }
}
