package com.orange.credicard.service.accounts;

public class ServiceCardStatus {

    private BlockCardStatus resultado;

    public ServiceCardStatus() {
    }

    public ServiceCardStatus(BlockCardStatus resultado) {
        this.resultado = resultado;
    }

    public BlockCardStatus getResultado() {
        return resultado;
    }

    public enum BlockCardStatus {
        BLOQUEADO, FALHA
    }
}
