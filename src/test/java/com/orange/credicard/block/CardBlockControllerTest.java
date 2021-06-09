package com.orange.credicard.block;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.UnprocessableEntityException;
import com.orange.credicard.proposal.Address;
import com.orange.credicard.proposal.Proposal;
import com.orange.credicard.proposal.ProposalRepository;
import com.orange.credicard.service.accounts.AccountsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.orange.credicard.card.CardStatus.BLOCKED;
import static com.orange.credicard.card.CardStatus.NORMAL;
import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.service.accounts.AccountsClient.ServiceCardStatus.BLOQUEADO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class CardBlockControllerTest {

    @PersistenceContext
    private EntityManager manager;

    @Autowired private MockMvc mockMvc;
    @Autowired private CardRepository cardRepository;
    @Autowired private ProposalRepository proposalRepository;
    @Autowired private CardBlockerRepository cardBlockerRepository;

    @Mock private AccountsClient accountsClient;

    private Long cardId;

    @BeforeEach
    void setup() {
        Address address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        Proposal proposal = proposalRepository.save(new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", address, new BigDecimal("40000"), PF));

        cardId = cardRepository.save(
            new Card("5352-7465-5791-9495", LocalDateTime.now(),
                20, new BigDecimal("20000"), proposal)).getId();
    }

    @Test
    @Transactional
    public void cardBlock__should_return_400_badRequest_if_cardId_is_null() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/block", (String) null)))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(400));
    }

    @Test
    @Transactional
    public void cardBlock__should_return_404_notFound_if_card_not_exists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/block", 0L)))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(404));
    }

    @Test
    @Transactional
    public void cardBlock__should_return_422_unprocessableEntity_if_card_is_already_blocked() {
        Card card = cardRepository.getOne(cardId);

        CardBlockerRepository mockBlocker = mock(CardBlockerRepository.class);

        Mockito.when(mockBlocker.findById(cardId)).thenReturn(Optional.of(
            new BlockedCard("127.0.0.1", "Mozilla/5.0", card)
        ));

        assertThrows(UnprocessableEntityException.class, () ->
                new CardBlockController(cardRepository, mockBlocker, accountsClient)
                    .cardBlock(card.getId(), mock(HttpServletRequest.class))
        );
    }

    @Test
    @Transactional
    public void cardBlock__should_return_200_success_and_block_card() {
        Card card = cardRepository.getOne(cardId);
        assertEquals(NORMAL, card.getStatus());

        Mockito.when(accountsClient.blockCard(card.getId())).thenReturn(BLOQUEADO);

        HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(mockServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        new CardBlockController(cardRepository, cardBlockerRepository, accountsClient)
            .cardBlock(card.getId(), mockServletRequest);

        assertEquals(BLOCKED, card.getStatus());
        assertThat(cardBlockerRepository.findByCardId(card.getId())).isNotEmpty();
    }
}