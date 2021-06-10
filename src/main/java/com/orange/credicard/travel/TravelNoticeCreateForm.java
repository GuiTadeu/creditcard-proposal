package com.orange.credicard.travel;

import com.orange.credicard.card.Card;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class TravelNoticeCreateForm {

    @NotBlank
    private String destination;

    @Future
    @NotNull
    private LocalDate endOfTrip;

    public TravelNoticeCreateForm(@NotBlank String destination, @Future @NotNull LocalDate endOfTrip) {
        this.destination = destination;
        this.endOfTrip = endOfTrip;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getEndOfTrip() {
        return endOfTrip;
    }

    public void setEndOfTrip(LocalDate endOfTrip) {
        this.endOfTrip = endOfTrip;
    }

    public TravelNotice toModel(Card card, String requesterIp, String userAgent) {
        return new TravelNotice(card, destination, endOfTrip, requesterIp, userAgent);
    }
}
