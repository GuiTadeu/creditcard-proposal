package com.orange.credicard.travel;

import com.orange.credicard.card.Card;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class TravelNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Future
    private LocalDate endOfTrip;

    @NotNull
    @ManyToOne
    private Card card;

    @NotBlank private String destination;
    @NotBlank private String requesterIp;
    @NotBlank private String userAgent;

    private LocalDateTime createdAt = LocalDateTime.now();

    public TravelNotice(@NotNull Card card, @NotBlank String destination, @Future LocalDate endOfTrip,
                        @NotBlank String requesterIp, @NotBlank String userAgent) {
        this.card = card;
        this.destination = destination;
        this.endOfTrip = endOfTrip;
        this.requesterIp = requesterIp;
        this.userAgent = userAgent;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getEndOfTrip() {
        return endOfTrip;
    }

    public Card getCard() {
        return card;
    }

    public String getDestination() {
        return destination;
    }

    public String getRequesterIp() {
        return requesterIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
