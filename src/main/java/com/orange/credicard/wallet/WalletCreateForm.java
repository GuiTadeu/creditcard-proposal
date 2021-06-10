package com.orange.credicard.wallet;

import javax.validation.constraints.NotBlank;

class WalletCreateForm {

    @NotBlank private String email;
    @NotBlank private String walletName;

    public WalletCreateForm() {
    }

    public WalletCreateForm(@NotBlank String email, @NotBlank String walletName) {
        this.email = email;
        this.walletName = walletName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }
}
