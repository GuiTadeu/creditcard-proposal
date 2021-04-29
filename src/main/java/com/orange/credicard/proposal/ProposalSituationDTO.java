package com.orange.credicard.proposal;

public class ProposalSituationDTO {

    private ProposalStatus status;

    public ProposalSituationDTO(Proposal proposal) {
        this.status = proposal.getStatus();
    }

    public ProposalStatus getStatus() {
        return status;
    }
}
