package com.gestion_labs.ms_gestion_labs.dto;

import com.gestion_labs.ms_gestion_labs.model.ContactoModel;
import com.gestion_labs.ms_gestion_labs.model.DireccionModel;

/**
 * DTO para crear/actualizar laboratorios con datos embebidos de dirección y contacto
 */
public class LaboratorioDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private DireccionModel direccion;
    private ContactoModel contacto;

    // Constructores
    public LaboratorioDTO() {}

    public LaboratorioDTO(Long id, String nombre, String tipo, DireccionModel direccion, ContactoModel contacto) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccion = direccion;
        this.contacto = contacto;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public DireccionModel getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionModel direccion) {
        this.direccion = direccion;
    }

    public ContactoModel getContacto() {
        return contacto;
    }

    public void setContacto(ContactoModel contacto) {
        this.contacto = contacto;
    }
}

