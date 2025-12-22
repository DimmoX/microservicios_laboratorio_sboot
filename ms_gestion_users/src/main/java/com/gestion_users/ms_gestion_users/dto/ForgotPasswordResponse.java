package com.gestion_users.ms_gestion_users.dto;

public class ForgotPasswordResponse {
    private String email;
    private String message;
    private String temporaryPassword; // Contraseña temporal en texto plano (solo para esta respuesta)

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(String email, String message, String temporaryPassword) {
        this.email = email;
        this.message = message;
        this.temporaryPassword = temporaryPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }
}
