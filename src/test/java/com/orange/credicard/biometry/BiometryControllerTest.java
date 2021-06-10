package com.orange.credicard.biometry;

import com.google.gson.Gson;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;

import static com.orange.credicard.proposal.PersonType.PF;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class BiometryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private BiometryRepository biometryRepository;
    @Autowired private ProposalRepository proposalRepository;
    @Autowired private CardRepository cardRepository;

    @PersistenceContext
    private EntityManager manager;

    private Card card;

    @BeforeEach
    void setup() {
        Address address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        Proposal proposal = proposalRepository.save(new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", address, new BigDecimal("40000"), PF));

        card = cardRepository.save(new Card("5352-7465-5791-9495", 20, new BigDecimal("20000"), proposal));
    }

    @Test
    @Transactional
    void create_should_save_biometric_if_base64_is_valid() throws Exception {
        URI uri = new URI("/biometrics/card/" + card.getId());

        var validBiometric = "SEFIQUhBSEFIQUhBSEFIQUhB";
        var createForm = new BiometryCreateForm();
        createForm.setValue(validBiometric);

        String json = new Gson().toJson(createForm);
        postAndExpected(uri, json, 201);

        assertEquals(biometryRepository.findByCardId(card.getId()).size(), 1);
    }

    @Test
    @Transactional
    void create_should_not_save_biometric_if_base64_not_is_valid() throws Exception {
        URI uri = new URI("/biometrics/card/" + card.getId());

        var invalidBiometric = "aW52YWxpZA==+++//aW52YWxpZA";
        var createForm = new BiometryCreateForm();
        createForm.setValue(invalidBiometric);

        String json = new Gson().toJson(createForm);
        postAndExpected(uri, json, 400);

        assertEquals(biometryRepository.findByCardId(card.getId()).size(), 0);
    }

    @Test
    @Transactional
    void create_should_save_two_valid_biometrics_and_return_by_card() throws Exception {
        URI uri = new URI("/biometrics/card/" + card.getId());

        // First Valid Biometric
        var firstBiometric = "SEFIQUhBSEFIQUhBSEFIQUhB";
        var firstCreateForm = new BiometryCreateForm();
        firstCreateForm.setValue(firstBiometric);

        String firstJson = new Gson().toJson(firstCreateForm);
        postAndExpected(uri, firstJson, 201);

        // Second Valid Biometric
        var secondBiometric = "dmFsaWQ=";
        var secondCreateForm = new BiometryCreateForm();
        secondCreateForm.setValue(secondBiometric);

        String secondJson = new Gson().toJson(secondCreateForm);
        postAndExpected(uri, secondJson, 201);

        // Third Invalid Biometric
        var thirdBiometric = "aW52YWxpZA==+++//aW52YWxpZA";
        var thirdCreateForm = new BiometryCreateForm();
        thirdCreateForm.setValue(thirdBiometric);

        String thirdJson = new Gson().toJson(thirdCreateForm);
        postAndExpected(uri, thirdJson, 400);

        assertEquals(biometryRepository.findByCardId(card.getId()).size(), 2);
    }

    private MvcResult postAndExpected(URI uri, String json, Integer expectedStatus) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
            .post(uri)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(expectedStatus))
            .andReturn();
    }
}
