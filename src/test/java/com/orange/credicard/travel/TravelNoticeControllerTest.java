package com.orange.credicard.travel;

import com.google.gson.Gson;
import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.proposal.Address;
import com.orange.credicard.proposal.Proposal;
import com.orange.credicard.proposal.ProposalRepository;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.ServiceTravelNoticeStatus;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.service.accounts.ServiceTravelNoticeStatus.NoticeStatus.CRIADO;
import static com.orange.credicard.service.accounts.ServiceTravelNoticeStatus.NoticeStatus.FALHA;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc
class TravelNoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock private AccountsClient accountsClient;
    @Mock private HttpServletRequest mockServletRequest;

    @PersistenceContext
    private EntityManager manager;

    @Autowired private CardRepository cardRepository;
    @Autowired private ProposalRepository proposalRepository;
    @Autowired private TravelNoticeRepository travelNoticeRepository;

    private Long cardId;

    @BeforeEach
    void setup() {

        Address address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");
        manager.persist(address);

        Proposal proposal = proposalRepository.save(new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", address, new BigDecimal("40000"), PF));

        cardId = cardRepository.save(
            new Card("5352-7465-5791-9495",
                20, new BigDecimal("20000"), proposal)).getId();

    }

    @Test
    @Transactional
    public void createTravelNotice__should_return_400_badRequest_if_request_body_is_invalid() throws Exception {
        TravelNoticeCreateForm invalidForm = new TravelNoticeCreateForm("", null);

        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/travel", (String) null))
            .content(new Gson().toJson(invalidForm))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(400));
    }

    @Test
    @Transactional
    public void createTravelNotice__should_return_400_badRequest_if_endOfTrip_is_on_past() throws Exception {

        String json = "{"
            + "\"destination\": \"Londres\","
            + " \"endOfTrip\": \"" + LocalDate.now().minusDays(1).toString() + "\""
            + "}";

        mockMvc.perform(MockMvcRequestBuilders
            .post("/cards/" + cardId + "/travel")
            .header("User-Agent", "Mozilla/5.0")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(400));
    }

    @Test
    @Transactional
    public void createTravelNotice__should_return_404_notFound_if_card_not_exists() throws Exception {

        String json = "{"
            + "\"destination\": \"Londres\","
            + " \"endOfTrip\": \"" + LocalDate.now().plusDays(42).toString() + "\""
            + "}";

        mockMvc.perform(MockMvcRequestBuilders
            .post("/cards/0/travel")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(404));
    }

    @Test
    @Transactional
    public void createTravelNotice__should_return_200_and_create_travel_notice() {

        Mockito.when(mockServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(mockServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        Mockito.when(accountsClient.travelNotice(any(), any())).thenReturn(new ServiceTravelNoticeStatus(CRIADO));

        new TravelNoticeController(cardRepository, accountsClient, travelNoticeRepository)
            .createTravelNotice(cardId, mockServletRequest,
                new TravelNoticeCreateForm("Londres", LocalDate.now().plusDays(10))
            );

        TravelNotice travelNotice = travelNoticeRepository.findByCardId(cardId).get();

        assertThat(travelNotice).isNotNull();
        assertThat(travelNotice.getCreatedAt()).isBefore(LocalDateTime.now());
        assertEquals(travelNotice.getCard().getId(), cardId);
        assertEquals(travelNotice.getEndOfTrip(), LocalDate.now().plusDays(10));
        assertEquals(travelNotice.getDestination(), "Londres");
        assertEquals(travelNotice.getUserAgent(), "Mozilla/5.0");
        assertEquals(travelNotice.getRequesterIp(), "127.0.0.1");
    }

    @Test
    @Transactional
    public void createTravelNotice__should_return_422_if_service_return_fail_status() {

        Mockito.when(mockServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(mockServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        Mockito.when(accountsClient.travelNotice(any(), any())).thenReturn(new ServiceTravelNoticeStatus(FALHA));

        ResponseEntity<?> response = new TravelNoticeController(cardRepository, accountsClient, travelNoticeRepository)
            .createTravelNotice(cardId, mockServletRequest,
                new TravelNoticeCreateForm("Londres", LocalDate.now().plusDays(10))
            );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());

        Optional<TravelNotice> travelNotice = travelNoticeRepository.findByCardId(cardId);
        assertThat(travelNotice).isEmpty();
    }

    @Test
    @Transactional
    public void createTravelNotice__should_return_500_if_service_throws_exception() {

        Mockito.when(mockServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(mockServletRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        Mockito.when(accountsClient.travelNotice(any(), any())).thenThrow(new RuntimeException("Erro inesperado"));

        ResponseEntity<?> response = new TravelNoticeController(cardRepository, accountsClient, travelNoticeRepository)
            .createTravelNotice(cardId, mockServletRequest,
                new TravelNoticeCreateForm("Londres", LocalDate.now().plusDays(10))
            );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        Optional<TravelNotice> travelNotice = travelNoticeRepository.findByCardId(cardId);
        assertThat(travelNotice).isEmpty();
    }
}