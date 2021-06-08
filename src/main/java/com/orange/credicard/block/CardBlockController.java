package com.orange.credicard.block;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.card.CardStatus;
import com.orange.credicard.exception.NotFoundException;
import com.orange.credicard.exception.UnprocessableEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
public class CardBlockController {

    private final CardRepository cardRepository;
    private final CardBlockerRepository cardBlockerRepository;

    public CardBlockController(CardRepository cardRepository, CardBlockerRepository cardBlockerRepository) {
        this.cardRepository = cardRepository;
        this.cardBlockerRepository = cardBlockerRepository;
    }

    @PostMapping("/cards/{cardId}/block")
    public ResponseEntity<?> cardBlock(@NotNull @PathVariable Long cardId, HttpServletRequest request) {
        Card card = cardRepository.findById(cardId).orElseThrow(NotFoundException::new);

        cardBlockerRepository.findById(cardId).ifPresent(it -> {
                throw new UnprocessableEntityException();
            }
        );

        card.block();

        cardBlockerRepository.save(
            new BlockedCard(
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                card
            )
        );

        return ResponseEntity.ok(new BlockedCardDTO(cardId, card.getStatus()));
    }

    public class BlockedCardDTO {

        private Long cardId;
        private CardStatus status;

        public BlockedCardDTO(Long cardId, CardStatus status) {
            this.cardId = cardId;
            this.status = status;
        }

        public Long getCardId() {
            return cardId;
        }

        public CardStatus getStatus() {
            return status;
        }
    }
}
