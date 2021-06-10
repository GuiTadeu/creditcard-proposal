package com.orange.credicard.wallet;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.NotFoundException;
import com.orange.credicard.exception.UnprocessableEntityException;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.ServiceCreateWalletRequest;
import com.orange.credicard.service.accounts.ServiceCreateWalletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

import static com.orange.credicard.service.accounts.ServiceCreateWalletResponse.WalletStatus.ASSOCIADA;

@RestController
public class WalletController {

    private final CardRepository cardRepository;
    private final AccountsClient accountsClient;
    private final WalletRepository walletRepository;

    public WalletController(CardRepository cardRepository, AccountsClient accountsClient, WalletRepository walletRepository) {
        this.cardRepository = cardRepository;
        this.accountsClient = accountsClient;
        this.walletRepository = walletRepository;
    }

    @PostMapping("/cards/{cardId}/wallets")
    ResponseEntity<?> createWallet(@NotNull @PathVariable Long cardId, @Valid @RequestBody WalletCreateForm form, UriComponentsBuilder uriBuilder) {
        Card card = cardRepository.findById(cardId).orElseThrow(NotFoundException::new);

        walletRepository.findByNameAndCardId(form.getWalletName(), card.getId()).ifPresent(it -> {
                throw new UnprocessableEntityException();
            }
        );

        ServiceCreateWalletResponse serviceResponse;
        try {
            serviceResponse = accountsClient.createWallet(card.getCardNumber(), new ServiceCreateWalletRequest(card.getProposal().getEmail(), form.getWalletName()));
        } catch (Exception exception) {
            return ResponseEntity.status(500).body("Falha sistema externo - Não foi possível criar o aviso viagem");
        }

        if(ASSOCIADA.equals(serviceResponse.getResultado())) {
            Wallet savedWallet = walletRepository.save(new Wallet(form.getWalletName(), card));
            URI uri = uriBuilder.path("/wallets/{id}").buildAndExpand(savedWallet.getId()).toUri();
            return ResponseEntity.created(uri).build();
        }

        return ResponseEntity.status(422).body("Não foi possível criar o aviso viagem");
    }

}
