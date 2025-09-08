package com.example.bankcards.exception.user;

public class NotUserCardException extends RuntimeException {
    public NotUserCardException(String message) {
        super(message);
    }
}
