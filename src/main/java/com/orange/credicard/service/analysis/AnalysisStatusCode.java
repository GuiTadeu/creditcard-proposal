package com.orange.credicard.service.analysis;

import com.orange.credicard.proposal.ProposalStatus;

import static com.orange.credicard.proposal.ProposalStatus.ELEGIVEL;
import static com.orange.credicard.proposal.ProposalStatus.NAO_ELEGIVEL;

public enum AnalysisStatusCode {

    COM_RESTRICAO(NAO_ELEGIVEL, 422),
    SEM_RESTRICAO(ELEGIVEL, 201);

    private ProposalStatus convertedStatus;
    private Integer httpStatusCode;

    AnalysisStatusCode(ProposalStatus convertedStatus, int httpStatusCode) {
        this.convertedStatus = convertedStatus;
        this.httpStatusCode = httpStatusCode;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public ProposalStatus getConvertedStatus() {
        return convertedStatus;
    }
}
