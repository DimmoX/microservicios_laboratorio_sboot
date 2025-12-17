package com.gestion_users.ms_gestion_users.dto;

/**
 * Request parcial para actualizar un usuario desde el Frontend.
 * Campos opcionales.
 */
public class UsuarioUpdateRequest {
    private String nombre;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private String rol;

    public UsuarioUpdateRequest() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
