package com.orange.credicard.card;

import com.orange.credicard.proposal.Address;
import com.orange.credicard.proposal.Proposal;
import com.orange.credicard.proposal.ProposalRepository;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.AccountsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.proposal.ProposalStatus.ELEGIVEL;
import static com.orange.credicard.proposal.ProposalStatus.NAO_ELEGIVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private CardRepository cardRepository;
    @Autowired private ProposalRepository proposalRepository;

    @Mock
    private AccountsClient accountsClient;

    private Proposal proposal;
    private Address address;

    @BeforeEach
    void setup() {
        address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        proposal = proposalRepository.save(new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", address, new BigDecimal("40000"), PF));
    }

    @Test
    @Transactional
    void checkProposalStatusAndCreateCard__should_create_card_if_proposal_status_is_ELEGIVEL() {
        proposal.setStatus(ELEGIVEL);

        var serviceResponse = new AccountsResponse("5352-7465-5791-9495", LocalDateTime.now(), new BigDecimal("200000"),
            new AccountsResponse.ExpirationResponse("99576ab3-ac11-4860-b8e7-7b2d89ac6386", 20, LocalDateTime.now()), proposal);

        Mockito.when(accountsClient.cardSituation(any())).thenReturn(serviceResponse);
        new CardController(cardRepository, accountsClient, proposalRepository).checkProposalStatusAndCreateCard();

        assertThat(cardRepository.findByCardNumber(serviceResponse.getId())).isNotEmpty();
    }

    @Test
    @Transactional
    void checkProposalStatusAndCreateCard__should_not_create_card_if_proposal_status_is_NAO_ELEGIVEL() {
        proposal.setStatus(NAO_ELEGIVEL);

        var serviceResponse = new AccountsResponse();

        Mockito.when(accountsClient.cardSituation(any())).thenReturn(serviceResponse);
        new CardController(cardRepository, accountsClient, proposalRepository).checkProposalStatusAndCreateCard();

        assertThat(cardRepository.findByCardNumber(serviceResponse.getId())).isEmpty();
    }

    @Test
    @Transactional
    void checkProposalStatusAndCreateCard__should_create_card_if_list_has_proposal_with_status_ELEGIVEL() {

        var firstProposal = proposalRepository.save(new Proposal("32547434075", "Moss",
            "moss@gmail.com", address, new BigDecimal("10000"), PF));

        var secondProposal = proposalRepository.save(new Proposal("88054243073", "Jen",
            "jen@gmail.com", address, new BigDecimal("20000"), PF));

        var thirdProposal = proposalRepository.save(new Proposal("19598399001", "Roy",
            "roy@gmail.com", address, new BigDecimal("30000"), PF));

        firstProposal.setStatus(ELEGIVEL);
        secondProposal.setStatus(NAO_ELEGIVEL);
        thirdProposal.setStatus(ELEGIVEL);

        Mockito.when(accountsClient.cardSituation(any())).thenAnswer(new Answer() {
            private int count = 0;

            public Object answer(InvocationOnMock invocation) {

                // First Service Response
                if (count++ == 1)
                    return new AccountsResponse("4362-4370-9866-5649", LocalDateTime.now(), new BigDecimal("100000"),
                        new AccountsResponse.ExpirationResponse("041e9efe-5cfe-453e-9988-04755ac3073f", 21, LocalDateTime.now()), firstProposal);

                // Second Service Response
                return new AccountsResponse("3926-3564-5265-2201", LocalDateTime.now(), new BigDecimal("300000"),
                    new AccountsResponse.ExpirationResponse("3748843f-7f7c-4b57-a640-7d2db27eac5b", 23, LocalDateTime.now()), thirdProposal);
            }
        });

        new CardController(cardRepository, accountsClient, proposalRepository).checkProposalStatusAndCreateCard();

        assertEquals(2, cardRepository.findAll().size());
    }
}