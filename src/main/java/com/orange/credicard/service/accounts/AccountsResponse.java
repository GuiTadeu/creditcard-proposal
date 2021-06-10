package com.orange.credicard.service.accounts;

import com.orange.credicard.card.Card;
import com.orange.credicard.proposal.Proposal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountsResponse {

    private String id;
    private LocalDateTime emitidoEm;
    private String titular;
    private BigDecimal limite;
    private ExpirationResponse vencimento;
    private Long idProposta;

    public Card toCard(Proposal proposal) {
        return new Card(id, vencimento.getDia(), limite, proposal);
    }

    public AccountsResponse() {
    }

    public AccountsResponse(String id, LocalDateTime emitidoEm, String titular, BigDecimal limite, ExpirationResponse vencimento, Long idProposta) {
        this.id = id;
        this.emitidoEm = emitidoEm;
        this.titular = titular;
        this.limite = limite;
        this.vencimento = vencimento;
        this.idProposta = idProposta;
    }

    public AccountsResponse(String id, LocalDateTime emitidoEm, BigDecimal limite, ExpirationResponse vencimento, Proposal proposal) {
        this(id, emitidoEm, proposal.getName(), limite, vencimento, proposal.getId());
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }

    public String getTitular() {
        return titular;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public ExpirationResponse getVencimento() {
        return vencimento;
    }

    public Long getIdProposta() {
        return idProposta;
    }

    public static class ExpirationResponse {

        private String id;
        private Integer dia;
        private LocalDateTime dataDeCriacao;

        public ExpirationResponse() {}

        public ExpirationResponse(String id, Integer dia, LocalDateTime dataDeCriacao) {
            this.id = id;
            this.dia = dia;
            this.dataDeCriacao = dataDeCriacao;
        }

        public String getId() {
            return id;
        }

        public Integer getDia() {
            return dia;
        }

        public LocalDateTime getDataDeCriacao() {
            return dataDeCriacao;
        }
    }
}
