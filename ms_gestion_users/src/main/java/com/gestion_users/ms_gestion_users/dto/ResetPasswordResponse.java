package com.gestion_users.ms_gestion_users.dto;

public class ResetPasswordResponse {
    private String username;
    private String message;
    private String newHash;

    public ResetPasswordResponse() {}
    
    public ResetPasswordResponse(String username, String message, String newHash) {
        this.username = username;
        this.message = message;
        this.newHash = newHash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNewHash() {
        return newHash;
    }

    public void setNewHash(String newHash) {
        this.newHash = newHash;
    }
}
