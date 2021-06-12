package com.orange.credicard.service.accounts;

import com.orange.credicard.proposal.Proposal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AccountsRequest {

    @NotNull private Long idProposta;
    @NotBlank private String documento;
    @NotBlank private String nome;

    public AccountsRequest() {
    }

    public AccountsRequest(Proposal proposal) {
        this.idProposta = proposal.getId();
        this.documento = proposal.getDecryptDocument();
        this.nome = proposal.getName();
    }

    public AccountsRequest(@NotNull Long idProposta, @NotBlank String documento, @NotBlank String nome) {
        this.idProposta = idProposta;
        this.documento = documento;
        this.nome = nome;
    }

    public Long getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(Long idProposta) {
        this.idProposta = idProposta;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
