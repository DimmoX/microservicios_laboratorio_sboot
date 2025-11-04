package com.gestion_users.ms_gestion_users.dto;

public class HashRequest {
    private String password;

    public HashRequest() {}
    
    public HashRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
