package com.orange.credicard.biometry;

import com.orange.credicard.validation.Base64;

import javax.validation.constraints.NotBlank;

public class BiometryCreateForm {

    @Base64
    @NotBlank
    private String value;

    public BiometryCreateForm() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
