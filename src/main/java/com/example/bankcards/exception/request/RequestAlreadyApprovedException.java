package com.example.bankcards.exception.request;

public class RequestAlreadyApprovedException extends RuntimeException {
    public RequestAlreadyApprovedException(String message) {
        super(message);
    }
}
