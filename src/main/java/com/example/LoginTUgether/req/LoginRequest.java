package com.example.LoginTUgether.req;

public class LoginRequest {
    private String email;
    private String citizenId; 
    public LoginRequest() {
    }

    
    public LoginRequest(String email, String citizenId) {
        this.email = email;
        this.citizenId = citizenId;
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }


}