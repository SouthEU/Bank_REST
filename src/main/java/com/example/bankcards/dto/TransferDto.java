package com.example.bankcards.dto;

import com.example.bankcards.entity.Transfer;

import java.time.OffsetDateTime;

public record TransferDto(Long senderCardId, Long receiverCardId, Double amount, OffsetDateTime transferDate) {
    public static TransferDto fromEntity(Transfer transfer) {
        return new TransferDto(transfer.getSourceCard().getId(), transfer.getTargetCard().getId(), transfer.getAmount(), transfer.getTransferDate());
    }
}
