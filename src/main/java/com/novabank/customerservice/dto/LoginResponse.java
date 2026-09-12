package com.novabank.customerservice.dto;

public class LoginResponse {

    private String token;
    private long expiresInSeconds;

    public LoginResponse(String token, long expiresInSeconds) {
        this.token = token;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(long expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }
}
