package com.orange.credicard.service.accounts;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ServiceTravelNoticeRequest {

    @NotBlank private String destino;
    @NotBlank private LocalDate validoAte;

    public ServiceTravelNoticeRequest() {
    }

    public ServiceTravelNoticeRequest(String destino, LocalDate validoAte) {
        this.destino = destino;
        this.validoAte = validoAte;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDate getValidoAte() {
        return validoAte;
    }

    public void setValidoAte(LocalDate validoAte) {
        this.validoAte = validoAte;
    }
}
