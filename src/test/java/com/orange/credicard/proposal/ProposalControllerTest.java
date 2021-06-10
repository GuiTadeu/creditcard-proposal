package com.orange.credicard.proposal;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.analysis.AnalysisClient;
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
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.proposal.ProposalStatus.ELEGIVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
public class ProposalControllerTest {

    @Mock private AnalysisClient analysisClient;
    @Mock private AccountsClient accountsClient;
    @Mock private CardRepository cardRepository;
    @Mock private AddressRepository addressRepository;

    @Autowired private MockMvc mockMvc;
    @Autowired private ProposalRepository proposalRepository;

    @PersistenceContext
    private EntityManager manager;

    private Address address;
    private ProposalCreateForm createForm;

    @BeforeEach
    void setup() {
        address = new Address("Rua dos Bobos", UUID.randomUUID().toString(), "04474123", "São Paulo", "SP");
        manager.persist(address);
        createForm = new ProposalCreateForm("88564395819", "Jubileu Irineu da Silva", "jubileu@gmail.com", address, new BigDecimal("40000"), PF);
    }

    @Test
    @Transactional
    public void create__should_save_if_form_has_valid_arguments_and_return_status_201() {
        ResponseEntity<?> response = new ProposalController(analysisClient, accountsClient, cardRepository, proposalRepository, addressRepository)
            .create(createForm, UriComponentsBuilder.newInstance());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(proposalRepository.findByDocument(createForm.getDocument())).isNotEmpty();
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

        ProposalRepository mockProposalRepository = mock(ProposalRepository.class);

        Mockito.when(mockProposalRepository.findByDocument(createForm.getDocument())).thenReturn(
            Optional.of(new Proposal("54799611011", "Jubileu Irineu da Silva",
                "jubileu@gmail.com", address, new BigDecimal("40000"), PF))
        );

        ResponseEntity<?> response = new ProposalController(analysisClient, accountsClient, cardRepository, mockProposalRepository, addressRepository)
            .create(createForm, UriComponentsBuilder.newInstance());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    @Transactional
    public void situation__should_return_if_existing_proposal_200_ok() throws Exception {

        manager.persist(address);
        Proposal proposal = proposalRepository.save(createForm.toModel());
        proposal.setStatus(ELEGIVEL);

        URI uri = new URI("/proposals/status/" + proposal.getId());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .get(uri)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(200))
            .andReturn();

        String proposalStatus = JsonPath.read(result.getResponse().getContentAsString(), "$.status");

        assertEquals(ELEGIVEL.toString(), proposalStatus);
    }

    @Test
    @Transactional
    public void situation__should_not_return_if_not_existing_proposal_404_notFound() throws Exception {
        URI uri = new URI("/proposals/status/" + 0);

        mockMvc.perform(MockMvcRequestBuilders
            .get(uri)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(404));
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
