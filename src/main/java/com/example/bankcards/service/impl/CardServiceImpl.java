package com.example.bankcards.service.impl;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.card.*;
import com.example.bankcards.exception.request.RequestAlreadyApprovedException;
import com.example.bankcards.exception.request.RequestAlreadyDeniedException;
import com.example.bankcards.exception.request.RequestNotFoundException;
import com.example.bankcards.exception.user.NotUserCardException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardGenerator;
import com.example.bankcards.util.PageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardBlockRequestRepository cardBlockRequestRepository;
    private final TransferRepository transferRepository;

    private final List<String> allowedSortFields = Arrays.asList("id", "balance", "createdAt", "expirationDate", "status");
    private final List<String> allowedSortFieldsProcess = Arrays.asList("id", "status");

    @Override
    public CardResponseDto createCard(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Card card = new Card();
        card.setCardNumber(CardGenerator.generateCardNumber());
        card.setOwner(user);
        card.setExpirationDate(OffsetDateTime.now().plusYears(5));
        card.setBalance(10000.0);
        card.setStatus(Card.Status.ACTIVE);
        card.onCreate();
        return CardResponseDto.fromEntity(cardRepository.save(card));
    }

    @Override
    public CardResponseDto blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card not found"));
        if(card.getStatus().equals(Card.Status.BLOCKED)) {
            throw new CardAlreadyBlockedException("Card already blocked");
        }

        card.setStatus(Card.Status.BLOCKED);

        return CardResponseDto.fromEntity(cardRepository.save(card));
    }

    @Override
    public CardResponseDto activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card not found"));
        if(card.getStatus().equals(Card.Status.ACTIVE)) {
            throw new CardAlreadyActiveException("Card already active");
        }

        card.setStatus(Card.Status.ACTIVE);

        return CardResponseDto.fromEntity(cardRepository.save(card));
    }

    @Override
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Override
    public Page<CardResponseDto> getAllCards(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageUtil.setPage(page, size, sortBy, sortDir, allowedSortFields);

        return cardRepository.findAll(pageable).map(CardResponseDto::fromEntity);
    }

    @Override
    public Page<CardProcessDto> getAllCardRequests(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageUtil.setPage(page, size, sortBy, sortDir, allowedSortFieldsProcess);

        return cardBlockRequestRepository.findAll(pageable).map(CardProcessDto::fromEntity);
    }

    @Override
    public CardProcessDto approveRequest(Long requestId) {
        CardBlockRequest request = cardBlockRequestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException("Card not found"));
        if(request.getStatus().equals(CardBlockRequest.Status.APPROVED)) {
            throw new RequestAlreadyApprovedException("Card already active");
        }

        request.setStatus(CardBlockRequest.Status.APPROVED);
        request.setProcessedBy(request.getRequestedBy());
        request.setProcessedAt(OffsetDateTime.now());

        return CardProcessDto.fromEntity(cardBlockRequestRepository.save(request));
    }

    @Override
    public CardProcessDto declineRequest(Long requestId) {
        CardBlockRequest request = cardBlockRequestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException("Card not found"));
        if(request.getStatus().equals(CardBlockRequest.Status.REJECTED)) {
            throw new RequestAlreadyDeniedException("Card already declined");
        }

        request.setStatus(CardBlockRequest.Status.REJECTED);
        request.setProcessedBy(request.getRequestedBy());
        request.setProcessedAt(OffsetDateTime.now());

        return CardProcessDto.fromEntity(cardBlockRequestRepository.save(request));
    }

    @Override
    public Page<CardResponseDto> getAllCardsByUser(int page, int size, String sortBy, String sortDir, User user) {
        Pageable pageable = PageUtil.setPage(page, size, sortBy, sortDir, allowedSortFields);

        return cardRepository.findAllByOwner(user, pageable).map(CardResponseDto::fromEntity);
    }

    @Override
    public CardProcessDto sendRequest(Long cardId, User user) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card not found"));

        if(!Objects.equals(card.getOwner().getId(), user.getId())) {
            throw new NotUserCardException("Its not your card");
        }
        if(card.getStatus().equals(Card.Status.BLOCKED)) {
            throw new CardAlreadyBlockedException("Card already blocked");
        }

        CardBlockRequest request = new CardBlockRequest();
        request.setRequestedBy(card.getOwner());
        request.setCard(card);
        request.setStatus(CardBlockRequest.Status.PENDING);

        return CardProcessDto.fromEntity(cardBlockRequestRepository.save(request));
    }

    @Override
    public TransferDto sendTransfer(TransferRequestDto transferRequestDto, User user) {
        Card senderCard = cardRepository.findById(transferRequestDto.senderCardId()).orElseThrow(() -> new CardNotFoundException("Card not found"));
        Card receiverCard = cardRepository.findById(transferRequestDto.receiverCardId()).orElseThrow(() -> new CardNotFoundException("Card not found"));

        if(!Objects.equals(senderCard.getOwner().getId(), user.getId()) || !Objects.equals(receiverCard.getOwner().getId(), user.getId())) {
            throw new NotUserCardException("Its not your card");
        }
        if(senderCard.getStatus().equals(Card.Status.BLOCKED) || receiverCard.getStatus().equals(Card.Status.BLOCKED)) {
            throw new CardBlockedException("Card blocked");
        }
        if (senderCard.getBalance() < transferRequestDto.amount()) {
            throw new NotEnoughBalanceException("Not enough balance");
        }

        Transfer transfer = new Transfer();
        transfer.setSourceCard(senderCard);
        transfer.setTargetCard(receiverCard);
        transfer.setAmount(transferRequestDto.amount());
        transfer.setCurrency("RUB");
        transfer.setDescription(transferRequestDto.description());

        return TransferDto.fromEntity(transferRepository.save(transfer));
    }
}
