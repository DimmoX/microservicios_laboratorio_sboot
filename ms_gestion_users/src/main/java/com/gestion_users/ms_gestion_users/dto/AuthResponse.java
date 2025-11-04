package com.gestion_users.ms_gestion_users.dto;

/**
 * AuthResponse
 * DTO que representa la respuesta de autenticación exitosa,
 * devolviendo el token JWT generado por el servidor.
 */
public class AuthResponse {

    private String token;

    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}