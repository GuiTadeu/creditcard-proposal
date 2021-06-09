package com.orange.credicard.travel;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
public class TravelNoticeController {

    private final CardRepository cardRepository;
    private final TravelNoticeRepository travelNoticeRepository;

    public TravelNoticeController(CardRepository cardRepository, TravelNoticeRepository travelNoticeRepository) {
        this.cardRepository = cardRepository;
        this.travelNoticeRepository = travelNoticeRepository;
    }

    @PostMapping("/cards/{cardId}/travel")
    public ResponseEntity<?> createTravelNotice(@NotNull @PathVariable Long cardId, HttpServletRequest request,
                                                @Valid @RequestBody TravelNoticeCreateForm form) {

        Card card = cardRepository.findById(cardId).orElseThrow(NotFoundException::new);

        String userAgent = request.getHeader("User-Agent");
        String requesterIp = request.getRemoteAddr();

        TravelNotice travelNotice = form.toModel(card, requesterIp, userAgent);
        travelNoticeRepository.save(travelNotice);

        return ResponseEntity.ok().build();
    }

    public static class TravelNoticeCreateForm {

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
}
