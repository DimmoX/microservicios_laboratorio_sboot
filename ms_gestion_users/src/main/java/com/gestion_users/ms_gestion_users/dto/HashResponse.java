package com.gestion_users.ms_gestion_users.dto;

public class HashResponse {
    private String password;
    private String hash;

    public HashResponse() {}
    
    public HashResponse(String password, String hash) {
        this.password = password;
        this.hash = hash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
