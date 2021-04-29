package com.orange.credicard.card;

import com.orange.credicard.proposal.Proposal;
import com.orange.credicard.proposal.ProposalRepository;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.AccountsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.orange.credicard.proposal.ProposalStatus.ELEGIVEL;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardRepository cardRepository;
    private final AccountsClient accountsClient;
    private final ProposalRepository proposalRepository;

    public CardController(CardRepository cardRepository, AccountsClient accountsClient, ProposalRepository proposalRepository) {
        this.cardRepository = cardRepository;
        this.accountsClient = accountsClient;
        this.proposalRepository = proposalRepository;
    }

    @PostMapping("/check")
    @Scheduled(cron = "*/5 * * * *")
    public ResponseEntity<?> checkProposalStatusAndCreateCard()  {

        List<Proposal> approvedProposals = proposalRepository.findByStatus(ELEGIVEL);

        approvedProposals.stream()
            .map(AccountsRequest::new)
            .map(accountsClient::cardSituation)
            .forEach(response -> {
                Proposal proposal = proposalRepository.getOne(response.getIdProposta());
                Card card = response.toCard(proposal);
                cardRepository.save(card);
            }
        );

        return ResponseEntity.ok().build();
    }
}
