package com.orange.credicard.service.accounts;

public class ServiceCreateWalletResponse {

    private WalletStatus resultado;
    private String id;

    public ServiceCreateWalletResponse() {
    }

    public ServiceCreateWalletResponse(WalletStatus resultado, String id) {
        this.resultado = resultado;
        this.id = id;
    }

    public WalletStatus getResultado() {
        return resultado;
    }

    public String getId() {
        return id;
    }

    public enum WalletStatus {
        ASSOCIADA, FALHA
    }
}
