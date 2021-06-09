package com.orange.credicard.travel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelNoticeRepository extends JpaRepository<TravelNotice, Long> {

    Optional<TravelNotice> findByCardId(Long cardId);
}
