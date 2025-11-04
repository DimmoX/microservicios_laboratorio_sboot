package com.gestion_users.ms_gestion_users.dto;

public class ResetPasswordRequest {
    private String username;
    private String newPassword;

    public ResetPasswordRequest() {}
    
    public ResetPasswordRequest(String username, String newPassword) {
        this.username = username;
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
