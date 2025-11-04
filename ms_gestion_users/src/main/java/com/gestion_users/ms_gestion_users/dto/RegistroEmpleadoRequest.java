package com.gestion_users.ms_gestion_users.dto;

/**
 * DTO para el registro completo de un empleado.
 * Incluye toda la información necesaria para crear:
 * - Registro en tabla contactos (con email para login)
 * - Registro en tabla direcciones
 * - Registro en tabla empleados
 * - Registro en tabla users (credenciales de acceso)
 */
public class RegistroEmpleadoRequest {
    
    // Datos personales del empleado
    private String pnombre;
    private String snombre;
    private String papellido;
    private String sapellido;
    private String rut;
    private String cargo;
    
    // Datos de contacto (anidado)
    private ContactoDTO contacto;
    
    // Datos de dirección (anidado)
    private DireccionDTO direccion;
    
    // Contraseña para crear el usuario de login
    private String password;

    // Constructores
    public RegistroEmpleadoRequest() {}

    // Getters y Setters
    public String getPnombre() {
        return pnombre;
    }

    public void setPnombre(String pnombre) {
        this.pnombre = pnombre;
    }

    public String getSnombre() {
        return snombre;
    }

    public void setSnombre(String snombre) {
        this.snombre = snombre;
    }

    public String getPapellido() {
        return papellido;
    }

    public void setPapellido(String papellido) {
        this.papellido = papellido;
    }

    public String getSapellido() {
        return sapellido;
    }

    public void setSapellido(String sapellido) {
        this.sapellido = sapellido;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public ContactoDTO getContacto() {
        return contacto;
    }

    public void setContacto(ContactoDTO contacto) {
        this.contacto = contacto;
    }

    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
