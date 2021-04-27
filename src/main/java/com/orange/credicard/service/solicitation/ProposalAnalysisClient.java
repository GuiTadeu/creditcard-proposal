package com.orange.credicard.service.solicitation;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(name = "proposalAnalysis", url = "http://localhost:9999")
public interface ProposalAnalysisClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/solicitacao", consumes = "application/json")
    AnalysisServiceResponse analysis(@RequestBody @Valid ProposalAnalysisForm form);
}
