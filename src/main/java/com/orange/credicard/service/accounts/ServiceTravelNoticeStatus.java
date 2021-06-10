package com.orange.credicard.service.accounts;

public class ServiceTravelNoticeStatus {

    private NoticeStatus resultado;

    public ServiceTravelNoticeStatus() {
    }

    public ServiceTravelNoticeStatus(NoticeStatus resultado) {
        this.resultado = resultado;
    }

    public NoticeStatus getResultado() {
        return resultado;
    }

    public enum NoticeStatus {
        CRIADO, FALHA
    }
}
