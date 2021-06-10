package com.orange.credicard.service.analysis;

import com.orange.credicard.card.CardRepository;
import com.orange.credicard.proposal.*;
import com.orange.credicard.service.accounts.AccountsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.proposal.ProposalStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnalysisClientTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProposalRepository proposalRepository;

    @Mock private CardRepository cardRepository;
    @Mock private AnalysisClient analysisClient;
    @Mock private AccountsClient accountsClient;
    @Mock private AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager manager;

    private Proposal proposal;

    @BeforeEach
    void setup() throws Exception {
        var address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        proposal = proposalRepository.save(new Proposal("54799611011", "Jubileu Irineu da Silva",
                "jubileu@gmail.com", address, new BigDecimal("40000"), PF));
    }

    @Test
    @Transactional
    public void analysis__should_save_status_NAO_ELEGIVEL_if_service_returns_COM_RESTRICAO() throws Exception {
        assertEquals(proposal.getStatus(), CRIADO);

        var formToAnalysis = new AnalysisRequest(proposal);
        Mockito.when(analysisClient.analysis(formToAnalysis))
                .thenReturn(new AnalysisResponse(proposal, "COM_RESTRICAO"));

        new ProposalController(analysisClient, accountsClient, cardRepository, proposalRepository, addressRepository).analysis(formToAnalysis);

        assertEquals(proposal.getStatus(), NAO_ELEGIVEL);
    }

    @Test
    @Transactional
    public void analysis__should_save_status_ELEGIVEL_if_service_returns_SEM_RESTRICAO() throws Exception {
        assertEquals(proposal.getStatus(), CRIADO);

        var formToAnalysis = new AnalysisRequest(proposal);

        Mockito.when(analysisClient.analysis(formToAnalysis))
                .thenReturn(new AnalysisResponse(proposal, "SEM_RESTRICAO"));

        new ProposalController(analysisClient, accountsClient, cardRepository, proposalRepository, addressRepository).analysis(formToAnalysis);

        assertEquals(proposal.getStatus(), ELEGIVEL);
    }
}