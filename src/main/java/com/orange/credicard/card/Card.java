package com.orange.credicard.card;

import com.orange.credicard.proposal.Proposal;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.orange.credicard.card.CardStatus.BLOCKED;
import static com.orange.credicard.card.CardStatus.NORMAL;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String cardNumber;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Range(min = 1, max = 31)
    private Integer expirationDay;

    @NotNull
    @Positive
    private BigDecimal cardLimit;

    @NotNull
    @OneToOne
    private Proposal proposal;

    private CardStatus status = NORMAL;

    public Card() {
    }

    public Card(@NotBlank String cardNumber, @NotNull @Range(min = 1, max = 31) Integer expirationDay,
                @NotNull @Positive BigDecimal cardLimit, @NotNull Proposal proposal) {
        this.cardNumber = cardNumber;
        this.expirationDay = expirationDay;
        this.cardLimit = cardLimit;
        this.proposal = proposal;
    }

    public Long getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getExpirationDay() {
        return expirationDay;
    }

    public BigDecimal getCardLimit() {
        return cardLimit;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public Long getProposalId() {
        return proposal.getId();
    }

    public CardStatus getStatus() {
        return status;
    }

    public void block() {
        this.status = BLOCKED;
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
        return Objects.hash(cardNumber);
    }
}
