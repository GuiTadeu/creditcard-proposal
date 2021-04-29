package com.orange.credicard.biometry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BiometryRepository extends JpaRepository<Biometry, Long> {

    List<Biometry> findByCardId(Long cardId);
}
