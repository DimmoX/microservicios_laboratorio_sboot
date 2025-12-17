package com.gestion_users.ms_gestion_users.dto;

/**
 * AuthResponse
 * DTO que representa la respuesta de autenticación exitosa,
 * devolviendo el token JWT y la información del usuario autenticado.
 */
public class AuthResponse {

    private String token;
    private UsuarioResponse usuario;

    public AuthResponse() {}

    public AuthResponse(String token, UsuarioResponse usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponse usuario) {
        this.usuario = usuario;
    }
}