package com.orange.credicard.block;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.card.CardStatus;
import com.orange.credicard.exception.NotFoundException;
import com.orange.credicard.exception.UnprocessableEntityException;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.ServiceCardStatus;
import com.orange.credicard.service.accounts.ServiceNameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import static com.orange.credicard.service.accounts.ServiceCardStatus.BlockCardStatus.BLOQUEADO;

@RestController
public class CardBlockController {

    private final CardRepository cardRepository;
    private final CardBlockerRepository cardBlockerRepository;
    private final AccountsClient accountsClient;

    public CardBlockController(CardRepository cardRepository, CardBlockerRepository cardBlockerRepository, AccountsClient accountsClient) {
        this.cardRepository = cardRepository;
        this.cardBlockerRepository = cardBlockerRepository;
        this.accountsClient = accountsClient;
    }

    @PostMapping("/cards/{cardId}/block")
    public ResponseEntity<?> cardBlock(@NotNull @PathVariable Long cardId, HttpServletRequest request) {
        Card card = cardRepository.findById(cardId).orElseThrow(NotFoundException::new);

        cardBlockerRepository.findById(cardId).ifPresent(it -> {
                throw new UnprocessableEntityException();
            }
        );

        ServiceCardStatus serviceCardStatus;
        try {
            serviceCardStatus = accountsClient.blockCard(card.getCardNumber(), new ServiceNameRequest());
        } catch (Exception exception) {
            return ResponseEntity.status(500).body("Falha no serviço - Não foi possível bloquear o cartão");
        }

        if(BLOQUEADO.equals(serviceCardStatus.getResultado())) {
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

        return ResponseEntity.status(422).body("Não foi possível bloquear o cartão");
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
