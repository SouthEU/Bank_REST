package com.example.bankcards.entity;

import com.example.bankcards.util.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true)
    @Convert(converter = EncryptedStringConverter.class)
    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cards_owner"))
    private User owner;

    @Column(name = "expiration_date", nullable = false)
    private OffsetDateTime expirationDate;

    @Column(nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "sourceCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transfer> outgoingTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "targetCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transfer> incomingTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardBlockRequest> blockRequests = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public enum Status {
        ACTIVE, BLOCKED, EXPIRED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;
        return cardNumber.equals(card.cardNumber);
    }

    @Override
    public int hashCode() {
        return cardNumber.hashCode();
    }
}
