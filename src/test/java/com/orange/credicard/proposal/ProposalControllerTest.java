package com.orange.credicard.proposal;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class ProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProposalRepository proposalRepository;

    @PersistenceContext
    private EntityManager manager;

    private ProposalCreateForm createForm;
    private Address address;

    @BeforeEach
    void setup() {
        address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        createForm = new ProposalCreateForm("88564395819", "jubileu@gmail.com", address, new BigDecimal("40000"), PF);
    }

    @Test
    @Transactional
    public void create__should_save_if_form_has_valid_arguments_and_return_status_201() throws Exception {
        URI uri = new URI("/proposals/create");

        String json = new Gson().toJson(createForm);
        MvcResult result = postAndExpected(uri, json, 201);

        Integer proposalId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Proposal retrievedProposal = proposalRepository.getOne(proposalId.longValue());

        assertNotNull(retrievedProposal);
        assertEquals(retrievedProposal.getAddress(), address);
        assertEquals(retrievedProposal, createForm.toModel());
    }

    @Test
    @Transactional
    public void create__should_not_save_if_form_has_invalid_arguments_and_return_status_400() throws Exception {
        URI uri = new URI("/proposals/create");

        ProposalCreateForm createInvalidForm = new ProposalCreateForm("", "Invalid Email", null, new BigDecimal("-1"), PF);
        String json = new Gson().toJson(createInvalidForm);
        postAndExpected(uri, json, 400);
    }

    @Test
    @Transactional
    public void create__should_not_save_if_proposal_document_already_exists_and_return_status_422() throws Exception {
        URI uri = new URI("/proposals/create");

        String json = new Gson().toJson(createForm);
        MvcResult result = postAndExpected(uri, json, 201);

        Integer proposalId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Proposal retrievedProposal = proposalRepository.getOne(proposalId.longValue());

        assertNotNull(retrievedProposal);
        assertEquals(retrievedProposal.getAddress(), address);
        assertEquals(retrievedProposal, createForm.toModel());

        postAndExpected(uri, json, 422);
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
