package com.orange.credicard.service.accounts;

import com.orange.credicard.card.CardStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(name = "accounts", url = "http://localhost:8888")
public interface AccountsClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/cartoes", consumes = "application/json")
    AccountsResponse cardSituation(@RequestBody @Valid AccountsRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/api/cartoes/{cardId}/bloqueios", consumes = "application/json")
    ServiceCardStatus blockCard(@PathVariable Long cardId);

    enum ServiceCardStatus {
        BLOQUEADO, FALHA
    }
}
