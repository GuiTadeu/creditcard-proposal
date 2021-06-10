package com.orange.credicard.travel;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.NotFoundException;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.ServiceTravelNoticeRequest;
import com.orange.credicard.service.accounts.ServiceTravelNoticeStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.orange.credicard.service.accounts.ServiceTravelNoticeStatus.NoticeStatus.CRIADO;

@RestController
public class TravelNoticeController {

    private final CardRepository cardRepository;
    private final AccountsClient accountsClient;
    private final TravelNoticeRepository travelNoticeRepository;

    public TravelNoticeController(CardRepository cardRepository, AccountsClient accountsClient,
                                  TravelNoticeRepository travelNoticeRepository) {
        this.cardRepository = cardRepository;
        this.accountsClient = accountsClient;
        this.travelNoticeRepository = travelNoticeRepository;
    }

    @PostMapping("/cards/{cardId}/travel")
    public ResponseEntity<?> createTravelNotice(@NotNull @PathVariable Long cardId, HttpServletRequest request,
                                                @Valid @RequestBody TravelNoticeCreateForm form) {

        Card card = cardRepository.findById(cardId).orElseThrow(NotFoundException::new);

        String userAgent = request.getHeader("User-Agent");
        String requesterIp = request.getRemoteAddr();

        TravelNotice travelNotice = form.toModel(card, requesterIp, userAgent);

        ServiceTravelNoticeStatus noticeStatus;
        try {
            noticeStatus = accountsClient.travelNotice(card.getCardNumber(),
                new ServiceTravelNoticeRequest(form.getDestination(), form.getEndOfTrip()));
        } catch (Exception exception) {
            return ResponseEntity.status(500).body("Falha sistema externo - Não foi possível criar o aviso viagem");
        }

        if(CRIADO.equals(noticeStatus.getResultado())) {
            travelNoticeRepository.save(travelNotice);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(422).body("Não foi possível criar o aviso viagem");
    }
}
