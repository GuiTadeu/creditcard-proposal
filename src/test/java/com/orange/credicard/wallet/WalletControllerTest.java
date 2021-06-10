package com.orange.credicard.wallet;

import com.google.gson.Gson;
import com.orange.credicard.card.Card;
import com.orange.credicard.card.CardRepository;
import com.orange.credicard.exception.NotFoundException;
import com.orange.credicard.exception.UnprocessableEntityException;
import com.orange.credicard.proposal.Address;
import com.orange.credicard.proposal.Proposal;
import com.orange.credicard.proposal.ProposalRepository;
import com.orange.credicard.service.accounts.AccountsClient;
import com.orange.credicard.service.accounts.ServiceCreateWalletResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

import static com.orange.credicard.proposal.PersonType.PF;
import static com.orange.credicard.service.accounts.ServiceCreateWalletResponse.WalletStatus.ASSOCIADA;
import static com.orange.credicard.service.accounts.ServiceCreateWalletResponse.WalletStatus.FALHA;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock private CardRepository cardRepository;
    @Mock private AccountsClient accountsClient;

    @Autowired private WalletRepository walletRepository;
    @Autowired private ProposalRepository proposalRepository;

    @PersistenceContext
    private EntityManager manager;

    @Test
    void createWallet__should_return_400_badRequest_if_cardId_is_null() throws Exception {
        WalletCreateForm form = new WalletCreateForm("jubileu@gmail.com", "PAYPAL");

        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/wallets", (String) null))
            .content(new Gson().toJson(form))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(400));
    }

    @Test
    void createWallet__should_return_400_badRequest_if_form_is_blank() throws Exception {
        WalletCreateForm form = new WalletCreateForm("", "");

        mockMvc.perform(MockMvcRequestBuilders
            .post(String.format("/cards/%s/wallets", 1))
            .content(new Gson().toJson(form))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(400));
    }

    @Test
    void createWallet__should_return_404_notFound_if_card_not_exists() {
        Mockito.when(cardRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            new WalletController(cardRepository, accountsClient, walletRepository).createWallet(
                42L, new WalletCreateForm("jubileu@gmail.com", "PAYPAL"), UriComponentsBuilder.newInstance()
            )
        );
    }

    @Test
    void createWallet__should_return_422_unprocessableEntity_if_card_is_already_associated_with_wallet() {
        WalletRepository mockWalletRepository = mock(WalletRepository.class);

        Mockito.when(cardRepository.findById(42L)).thenReturn(Optional.of(mock(Card.class)));
        Mockito.when(mockWalletRepository.findByNameAndCardId(any(), any())).thenReturn(Optional.of(mock(Wallet.class)));

        assertThrows(UnprocessableEntityException.class, () ->
            new WalletController(cardRepository, accountsClient, mockWalletRepository).createWallet(
                42L, new WalletCreateForm("jubileu@gmail.com", "PAYPAL"), UriComponentsBuilder.newInstance()
            )
        );
    }

    @Test
    void createWallet__should_return_500_internalServerError_if_service_throws_exception_and_not_save_wallet() {

        Mockito.when(cardRepository.findById(42L)).thenReturn(Optional.of(mock(Card.class)));
        Mockito.when(accountsClient.createWallet(any(), any())).thenThrow(new RuntimeException("Deu ruim"));

        ResponseEntity<?> response = new WalletController(cardRepository, accountsClient, walletRepository).createWallet(
            42L, new WalletCreateForm("jubileu@gmail.com", "PAYPAL"), UriComponentsBuilder.newInstance()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertThat(walletRepository.findByNameAndCardId("PAYPAL", 42L)).isEmpty();
    }

    @Test
    void createWallet__should_return_422_unprocessableEntity_if_service_return_fail_and_not_save_wallet() {
        String email = "jubileu@gmail.com";
        String walletName = "PAYPAL";

        Proposal proposal = new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", mock(Address.class), new BigDecimal("40000"), PF);

        Card card = new Card("5352-7465-5791-9495", 20, new BigDecimal("20000"), proposal);

        Mockito.when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        Mockito.when(accountsClient.createWallet(any(), any()))
            .thenReturn(new ServiceCreateWalletResponse(FALHA, ""));

        ResponseEntity<?> response = new WalletController(cardRepository, accountsClient, walletRepository)
            .createWallet(card.getId(), new WalletCreateForm(email, walletName), UriComponentsBuilder.newInstance());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertThat(walletRepository.findByNameAndCardId(walletName, card.getId())).isEmpty();
    }

    @Test
    @Transactional
    void createWallet__should_return_201_create_and_save_wallet() {
        String email = "jubileu@gmail.com";
        String walletName = "PAYPAL";

        Address address = new Address("Rua dos Bobos", "0", "04474123", "São Paulo", "SP");

        Proposal proposal = new Proposal("54799611011", "Jubileu Irineu da Silva",
            "jubileu@gmail.com", address, new BigDecimal("40000"), PF);

        Card card = new Card("5352-7465-5791-9495", 20, new BigDecimal("20000"), proposal);

        manager.persist(address);
        manager.persist(proposal);
        manager.persist(card);

        Mockito.when(cardRepository.findById(any())).thenReturn(Optional.of(card));
        Mockito.when(accountsClient.createWallet(any(), any()))
            .thenReturn(new ServiceCreateWalletResponse(ASSOCIADA, ""));

        ResponseEntity<?> response = new WalletController(cardRepository, accountsClient, walletRepository)
            .createWallet(card.getId(), new WalletCreateForm(email, walletName), UriComponentsBuilder.newInstance());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(walletRepository.findByNameAndCardId(walletName, card.getId())).isNotEmpty();
    }
}