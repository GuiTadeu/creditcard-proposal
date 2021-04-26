package com.orange.credicard.proposal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProposalAnalysisForm {

    @NotNull private Long idProposta;
    @NotBlank private String documento;
    @NotBlank private String nome;

    public ProposalAnalysisForm() {
    }

    public ProposalAnalysisForm(Proposal proposal) {
        this.idProposta = proposal.getId();
        this.documento = proposal.getDocument();
        this.nome = proposal.getName();
    }

    public ProposalAnalysisForm(@NotNull Long idProposta, @NotBlank String documento, @NotBlank String nome) {
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
