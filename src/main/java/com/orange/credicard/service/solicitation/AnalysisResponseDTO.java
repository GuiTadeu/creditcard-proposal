package com.orange.credicard.service.solicitation;

import com.orange.credicard.proposal.Proposal;

public class AnalysisResponseDTO {

    private Long idProposta;
    private String documento;
    private String nome;
    private String resultadoSolicitacao;

    public AnalysisResponseDTO() {
    }

    public AnalysisResponseDTO(Proposal proposal, String resultadoSolicitacao) {
        this.idProposta = proposal.getId();
        this.documento = proposal.getDocument();
        this.nome = proposal.getName();
        this.resultadoSolicitacao = resultadoSolicitacao;
    }

    public AnalysisResponseDTO(Long idProposta, String documento, String nome, String resultadoSolicitacao) {
        this.idProposta = idProposta;
        this.documento = documento;
        this.nome = nome;
        this.resultadoSolicitacao = resultadoSolicitacao;
    }

    public Long getIdProposta() {
        return idProposta;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNome() {
        return nome;
    }

    public String getResultadoSolicitacao() {
        return resultadoSolicitacao;
    }
}
