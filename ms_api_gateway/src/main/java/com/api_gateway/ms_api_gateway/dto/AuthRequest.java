package com.api_gateway.ms_api_gateway.dto;

/**
 * DTO para peticiones de autenticación (login).
 * Debe coincidir exactamente con el AuthRequest de ms_gestion_users.
 */
public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Alias para compatibilidad (getEmail/setEmail usan username internamente)
    public String getEmail() {
        return username;
    }

    public void setEmail(String email) {
        this.username = email;
    }
}
