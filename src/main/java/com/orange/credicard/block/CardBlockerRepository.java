package com.orange.credicard.block;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBlockerRepository extends JpaRepository<BlockedCard, Long> {
}
