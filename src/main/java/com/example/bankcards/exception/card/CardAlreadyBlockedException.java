package com.example.bankcards.exception.card;

public class CardAlreadyBlockedException extends RuntimeException {
    public CardAlreadyBlockedException(String message) {
        super(message);
    }
}
