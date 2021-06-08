package com.orange.credicard.block;

import com.orange.credicard.card.Card;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
public class BlockedCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String requesterIp;

    @NotBlank
    private String requesterUserAgent;

    @NotNull
    @ManyToOne
    private Card card;

    @PastOrPresent
    private final LocalDateTime createdAt = LocalDateTime.now();

    public BlockedCard(@NotBlank String requesterIp, @NotBlank String requesterUserAgent, @NotNull Card card) {
        this.requesterIp = requesterIp;
        this.requesterUserAgent = requesterUserAgent;
        this.card = card;
    }

    public BlockedCard() {}

    public Long getId() {
        return id;
    }

    public String getRequesterIp() {
        return requesterIp;
    }

    public String getRequesterUserAgent() {
        return requesterUserAgent;
    }

    public Card getCard() {
        return card;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
