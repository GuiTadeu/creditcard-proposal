package com.orange.credicard.proposal;

public class ProposalCreateDTO {

    private Long id;
    private String clientEmail;

    public ProposalCreateDTO(Proposal proposal) {
        this.id = proposal.getId();
        this.clientEmail = proposal.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getClientEmail() {
        return clientEmail;
    }
}
