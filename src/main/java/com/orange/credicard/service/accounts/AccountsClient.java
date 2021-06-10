package com.orange.credicard.service.accounts;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@FeignClient(name = "accounts", url = "http://localhost:8888")
public interface AccountsClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/cartoes", consumes = "application/json")
    AccountsResponse cardSituation(@RequestParam Long idProposta);

    @RequestMapping(method = RequestMethod.POST, value = "/api/cartoes/{cardNumber}/bloqueios", consumes = "application/json")
    ServiceCardStatus blockCard(@PathVariable String cardNumber, @RequestBody ServiceNameRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/api/cartoes/{cardNumber}/avisos", consumes = "application/json")
    ServiceTravelNoticeStatus travelNotice(@PathVariable @NotBlank String cardNumber, @Valid @RequestBody ServiceTravelNoticeRequest request);

    @RequestMapping(method = RequestMethod.POST, value = "/api/cartoes/{cardNumber}/carteiras", consumes = "application/json")
    ServiceCreateWalletResponse createWallet(@PathVariable @NotBlank String cardNumber, @Valid @RequestBody ServiceCreateWalletRequest request);

}
