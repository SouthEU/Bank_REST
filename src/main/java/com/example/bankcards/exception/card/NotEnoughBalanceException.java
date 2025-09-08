package com.example.bankcards.exception.card;

public class NotEnoughBalanceException extends RuntimeException {
  public NotEnoughBalanceException(String message) {
    super(message);
  }
}
