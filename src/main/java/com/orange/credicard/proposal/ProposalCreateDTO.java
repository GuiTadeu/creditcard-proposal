package com.orange.credicard.proposal;

public class ProposalCreateDTO {

    private Long id;
    private String name;
    private String email;

    public ProposalCreateDTO(Proposal proposal) {
        this.id = proposal.getId();
        this.name = proposal.getName();
        this.email = proposal.getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
