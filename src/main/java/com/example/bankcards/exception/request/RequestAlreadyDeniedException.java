package com.example.bankcards.exception.request;

public class RequestAlreadyDeniedException extends RuntimeException {
    public RequestAlreadyDeniedException(String message) {
        super(message);
    }
}
