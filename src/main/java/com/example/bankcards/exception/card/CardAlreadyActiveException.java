package com.example.bankcards.exception.card;

public class CardAlreadyActiveException extends RuntimeException {
    public CardAlreadyActiveException(String message) {
        super(message);
    }
}
