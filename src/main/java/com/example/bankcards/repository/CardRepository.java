package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByOwner(User user, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.balance), 0.0) FROM Card c WHERE c.owner.id = :userId")
    Double getTotalBalanceByUserId(@Param("userId") Long userId);
}