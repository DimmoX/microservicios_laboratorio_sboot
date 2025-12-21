package com.gestion_users.ms_gestion_users.dto;

public class ChangePasswordResponse {
    private String username;
    private String message;
    private boolean success;

    public ChangePasswordResponse() {}

    public ChangePasswordResponse(String username, String message, boolean success) {
        this.username = username;
        this.message = message;
        this.success = success;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
