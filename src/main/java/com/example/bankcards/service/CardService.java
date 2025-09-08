package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;

public interface CardService {
    CardResponseDto createCard(Long userId);
    CardResponseDto blockCard(Long cardId);
    CardResponseDto activateCard(Long cardId);
    void deleteCard(Long cardId);
    Page<CardResponseDto> getAllCards(int page, int size, String sortBy, String sortDir);
    Page<CardProcessDto> getAllCardRequests(int page, int size, String sortBy, String sortDir);
    CardProcessDto approveRequest(Long requestId);
    CardProcessDto declineRequest(Long requestId);
    Page<CardResponseDto> getAllCardsByUser(int page, int size, String sortBy, String sortDir, User user);
    CardProcessDto sendRequest(Long cardId, User user);
    TransferDto sendTransfer(TransferRequestDto transferRequestDto, User user);
}
