package com.orange.credicard.block;

import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.proposal.Address;
import com.orange.credicard.proposal.Proposal;
import com.orange.credicard.proposal.ProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.orange.credicard.card.CardStatus.BLOCKED;
import static com.orange.credicard.proposal.PersonType.PF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class CardBlockControllerTest {

    @PersistenceContext
    private EntityManager manager;

    @Autowired private MockMvc mockMvc;
    @Autowired private CardRepository cardRepository;
    @Autowired private CardBlockerRepository cardBlockerRepository;
    @Autowired private ProposalRepository proposalRepository;

    private Card card;

    @BeforeEach
    void setup() {
        Address address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        Proposal proposal = proposalRepository.save(new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", address, new BigDecimal("40000"), PF));

        card = cardRepository.save(new Card("5352-7465-5791-9495", LocalDateTime.now(), 20, new BigDecimal("20000"), proposal));
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
    public void cardBlock__should_return_422_unprocessableEntity_if_card_is_already_blocked() throws Exception {

        cardBlockerRepository.save(new BlockedCard("127.0.0.1", "Mozilla/5.0", card));

        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/block", card.getId()))
            .header("user-agent", "Mozilla/5.0"))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(422));
    }

    @Test
    @Transactional
    public void cardBlock__should_return_200_success_and_block_card() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/block", card.getId()))
            .header("user-agent", "Mozilla/5.0"))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(200));

        assertThat(cardBlockerRepository.findById(card.getId())).isNotEmpty();
        assertEquals(BLOCKED, card.getStatus());
    }
}