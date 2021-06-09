package com.orange.credicard.block;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardBlockerRepository extends JpaRepository<BlockedCard, Long> {

    Optional<BlockedCard> findByCardId(Long cardId);
}
