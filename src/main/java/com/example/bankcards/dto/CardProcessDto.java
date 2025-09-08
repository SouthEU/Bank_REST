package com.example.bankcards.dto;

import com.example.bankcards.entity.CardBlockRequest;

public record CardProcessDto(Long id, String username, String cardNumber, String status) {
    public static CardProcessDto fromEntity(CardBlockRequest entity) {
        return new CardProcessDto(
                entity.getId(),
                entity.getRequestedBy().getUsername(),
                entity.getCard().getCardNumber(),
                entity.getStatus().toString()
        );
    }
}
