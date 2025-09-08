package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.card.*;
import com.example.bankcards.exception.request.RequestAlreadyApprovedException;
import com.example.bankcards.exception.request.RequestNotFoundException;
import com.example.bankcards.exception.user.NotUserCardException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.*;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.impl.CardServiceImpl;
import com.example.bankcards.util.CardGenerator;
import com.example.bankcards.util.PageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private CardBlockRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        card = new Card();
        card.setId(100L);
        card.setCardNumber("4111111111111111");
        card.setOwner(user);
        card.setExpirationDate(OffsetDateTime.now().plusYears(5));
        card.setBalance(10_000.0);
        card.setStatus(Card.Status.ACTIVE);
        card.setCreatedAt(OffsetDateTime.now().minusDays(1));
        card.setUpdatedAt(OffsetDateTime.now());

        request = new CardBlockRequest();
        request.setId(1L);
        request.setCard(card);
        request.setRequestedBy(user);
        request.setStatus(CardBlockRequest.Status.PENDING);
        request.setRequestDate(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Создание карты")
    void createCard_ShouldCreateCard_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        // When
        CardResponseDto result = cardService.createCard(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.balance()).isEqualTo(10000.0);
        assertThat(result.status()).isEqualTo("ACTIVE");
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    @DisplayName("Создание карты — пользователь не найден")
    void createCard_ShouldThrowUserNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("Блокировка карты")
    void blockCard_ShouldBlockCard_WhenCardIsActive() {
        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        CardResponseDto result = cardService.blockCard(100L);

        assertThat(result.status()).isEqualTo("BLOCKED");
        verify(cardRepository, times(1)).save(argThat(c -> c.getStatus() == Card.Status.BLOCKED));
    }

    @Test
    @DisplayName("Блокировка уже заблокированной карты")
    void blockCard_ShouldThrowCardAlreadyBlockedException_WhenCardIsBlocked() {
        card.setStatus(Card.Status.BLOCKED);
        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.blockCard(100L))
                .isInstanceOf(CardAlreadyBlockedException.class)
                .hasMessage("Card already blocked");
    }

    @Test
    @DisplayName("Перевод средств")
    void sendTransfer_ShouldProcessTransfer_WhenValid() {
        Card receiverCard = new Card();
        receiverCard.setId(101L);
        receiverCard.setOwner(user);
        receiverCard.setBalance(5000.0);
        receiverCard.setStatus(Card.Status.ACTIVE);

        TransferRequestDto requestDto = new TransferRequestDto(100L, 101L, 1000.0, "Test transfer");

        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));
        when(cardRepository.findById(101L)).thenReturn(Optional.of(receiverCard));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> {
            Transfer t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        // When
        TransferDto result = cardService.sendTransfer(requestDto, user);

        // Then
        assertThat(result.amount()).isEqualTo(1000.0);
        assertThat(result.senderCardId()).isEqualTo(100L);
        assertThat(result.receiverCardId()).isEqualTo(101L);
        verify(cardRepository, times(2)).findById(anyLong());
        verify(transferRepository, times(1)).save(any(Transfer.class));
    }

    @Test
    @DisplayName("Перевод средств — недостаточно средств")
    void sendTransfer_ShouldThrowNotEnoughBalanceException_WhenInsufficientFunds() {
        TransferRequestDto requestDto = new TransferRequestDto(100L, 101L, 15000.0, "Too much");

        when(cardRepository.findById(100L)).thenReturn(Optional.of(card)); // balance = 10_000
        when(cardRepository.findById(101L)).thenReturn(Optional.of(new Card() {{
            setOwner(user);
            setStatus(Card.Status.ACTIVE);
        }}));

        assertThatThrownBy(() -> cardService.sendTransfer(requestDto, user))
                .isInstanceOf(NotEnoughBalanceException.class)
                .hasMessage("Not enough balance");
    }

    @Test
    @DisplayName("Перевод средств — чужая карта")
    void sendTransfer_ShouldThrowNotUserCardException_WhenCardNotOwned() {
        User anotherUser = new User();
        anotherUser.setId(2L);

        Card foreignCard = new Card();
        foreignCard.setId(101L);
        foreignCard.setOwner(anotherUser); // не владелец
        foreignCard.setStatus(Card.Status.ACTIVE);

        TransferRequestDto requestDto = new TransferRequestDto(100L, 101L, 1000.0, "Invalid");

        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));
        when(cardRepository.findById(101L)).thenReturn(Optional.of(foreignCard));

        assertThatThrownBy(() -> cardService.sendTransfer(requestDto, user))
                .isInstanceOf(NotUserCardException.class)
                .hasMessage("Its not your card");
    }

    @Test
    @DisplayName("Отправка запроса на блокировку")
    void sendRequest_ShouldCreateRequest_WhenCardIsOwnedAndActive() {
        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));
        when(cardBlockRequestRepository.save(any(CardBlockRequest.class))).thenAnswer(i -> {
            CardBlockRequest r = i.getArgument(0);
            r.setId(1L);
            r.setProcessedAt(null);
            return r;
        });

        CardProcessDto result = cardService.sendRequest(100L, user);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.username()).isEqualTo("testuser");
        verify(cardBlockRequestRepository, times(1)).save(any(CardBlockRequest.class));
    }

    @Test
    @DisplayName("Отправка запроса на блокировку — чужая карта")
    void sendRequest_ShouldThrowNotUserCardException_WhenCardNotOwned() {
        User foreignUser = new User();
        foreignUser.setId(999L);

        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.sendRequest(100L, foreignUser))
                .isInstanceOf(NotUserCardException.class)
                .hasMessage("Its not your card");
    }

    @Test
    @DisplayName("Одобрение запроса")
    void approveRequest_ShouldApprove_WhenPending() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(cardBlockRequestRepository.save(any(CardBlockRequest.class))).thenAnswer(i -> i.getArgument(0));

        CardProcessDto result = cardService.approveRequest(1L);

        assertThat(result.status()).isEqualTo("APPROVED");
        assertThat(result.username()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Одобрение уже одобренного запроса")
    void approveRequest_ShouldThrowRequestAlreadyApprovedException_WhenAlreadyApproved() {
        request.setStatus(CardBlockRequest.Status.APPROVED);
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> cardService.approveRequest(1L))
                .isInstanceOf(RequestAlreadyApprovedException.class)
                .hasMessage("Card already active");
    }

    @Test
    @DisplayName("Пагинация всех карт")
    void getAllCards_ShouldReturnPageOfCards() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Card> cardPage = new PageImpl<>(List.of(card), pageable, 1);

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);

        Page<CardResponseDto> result = cardService.getAllCards(0, 10, "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).id()).isEqualTo(100L);
        verify(cardRepository, times(1)).findAll(pageable);
    }
}