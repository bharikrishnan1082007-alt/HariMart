package com.harimart.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;

    @NotBlank(message = "Password is required")
    private String password;

    public AuthRequest() {
    }

    public AuthRequest(String emailOrPhone, String password) {
        this.emailOrPhone = emailOrPhone;
        this.password = password;
    }

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
