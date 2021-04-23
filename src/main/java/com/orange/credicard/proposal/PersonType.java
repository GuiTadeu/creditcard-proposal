package com.orange.credicard.proposal;

public enum PersonType {

    PF("Pessoa Física"),
    PJ("Pessoa Jurídica");

    private final String name;

    PersonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
