package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;

public record TransferRequestDto(@NotNull Long senderCardId,@NotNull Long receiverCardId,@NotNull Double amount, String description) {
}
