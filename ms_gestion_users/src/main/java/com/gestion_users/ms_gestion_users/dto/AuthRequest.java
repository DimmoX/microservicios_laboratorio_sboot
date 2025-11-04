package com.gestion_users.ms_gestion_users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * AuthRequest
 * DTO utilizado para las solicitudes de inicio de sesión (login).
 */
public class AuthRequest {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Email(message = "El nombre de usuario debe ser un correo electrónico válido")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    // Getters y Setters
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

    public String getEmail() {
        return username;
    }

    public void setEmail(String email) {
        this.username = email;
    }
}