package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;

public record CardResponseDto(
        Long id,
        String maskedNumber,
        String ownerName,
        Double balance,
        String status,
        String createdAt
) {

    public static CardResponseDto fromEntity(Card card) {
        String fullName = card.getOwner().getFirstName() + " " + card.getOwner().getLastName();
        String masked = maskCardNumber(card.getCardNumber());

        return new CardResponseDto(
                card.getId(),
                masked,
                fullName,
                card.getBalance(),
                card.getStatus().name(),
                card.getCreatedAt().toString()
        );
    }

    private static String maskCardNumber(String fullNumber) {
        if (fullNumber == null || fullNumber.length() < 4) {
            return "**** **** **** ****";
        }

        String last4 = fullNumber.substring(fullNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
