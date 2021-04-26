package com.orange.credicard.proposal;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.orange.credicard.service.solicitation.AnalysisResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.proposal.ProposalStatus.*;
import static com.orange.credicard.service.solicitation.AnalysisEndpoints.POST_SOLICITATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class ProposalControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProposalRepository proposalRepository;

    @Mock
    private RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager manager;

    private Address address;
    private ProposalCreateForm createForm;

    @BeforeEach
    void setup() {
        address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        createForm = new ProposalCreateForm("88564395819", "Jubileu Irineu da Silva", "jubileu@gmail.com", address, new BigDecimal("40000"), PF);
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

        ProposalCreateForm createInvalidForm = new ProposalCreateForm("", "", "Invalid Email", null, new BigDecimal("-1"), PF);
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

    @Test
    @Transactional
    public void analysis__should_save_status_NAO_ELEGIVEL_if_service_returns_COM_RESTRICAO() throws Exception {
        URI proposalCreateUri = new URI("/proposals/create");

        String jsonToCreateProposal = new Gson().toJson(createForm);
        MvcResult result = postAndExpected(proposalCreateUri, jsonToCreateProposal, 201);

        Integer proposalId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Proposal retrievedProposal = proposalRepository.getOne(proposalId.longValue());

        assertEquals(retrievedProposal.getStatus(), CRIADO);

        var formToAnalysis = new ProposalAnalysisForm(retrievedProposal);
        var serviceResponse = new AnalysisResponseDTO(retrievedProposal, "COM_RESTRICAO");

        Mockito.when(restTemplate.postForEntity(POST_SOLICITATION, formToAnalysis, AnalysisResponseDTO.class))
                .thenReturn(new ResponseEntity(serviceResponse, HttpStatus.CREATED));

        new ProposalController(proposalRepository).analysis(formToAnalysis, restTemplate);
        assertEquals(retrievedProposal.getStatus(), NAO_ELEGIVEL);
    }

    @Test
    @Transactional
    public void analysis__should_save_status_ELEGIVEL_if_service_returns_SEM_RESTRICAO() throws Exception {
        URI proposalCreateUri = new URI("/proposals/create");

        String jsonToCreateProposal = new Gson().toJson(createForm);
        MvcResult result = postAndExpected(proposalCreateUri, jsonToCreateProposal, 201);

        Integer proposalId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Proposal retrievedProposal = proposalRepository.getOne(proposalId.longValue());

        assertEquals(retrievedProposal.getStatus(), CRIADO);

        var formToAnalysis = new ProposalAnalysisForm(retrievedProposal);
        var serviceResponse = new AnalysisResponseDTO(retrievedProposal, "SEM_RESTRICAO");

        Mockito.when(restTemplate.postForEntity(POST_SOLICITATION, formToAnalysis, AnalysisResponseDTO.class))
                .thenReturn(new ResponseEntity(serviceResponse, HttpStatus.CREATED));

        new ProposalController(proposalRepository).analysis(formToAnalysis, restTemplate);
        assertEquals(retrievedProposal.getStatus(), ELEGIVEL);
    }

    private MvcResult post(URI uri, String json) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(uri)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
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
