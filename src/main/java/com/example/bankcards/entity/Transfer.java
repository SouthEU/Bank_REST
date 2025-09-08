package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_card_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfers_source"))
    private Card sourceCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_card_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfers_target"))
    private Card targetCard;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "transfer_date", nullable = false)
    private OffsetDateTime transferDate;

    private String description;

    @PrePersist
    protected void onCreate() {
        transferDate = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return id.equals(transfer.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}