// DTO para crear/actualizar laboratorios con datos embebidos de dirección y contacto
package com.gestion_labs.dto;

/**
 * Ejemplo de DTO para Laboratorio
 * Incluye atributos principales y referencias a entidades relacionadas
 */
public class LaboratorioDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private DireccionDTO direccion; // Referencia a DTO de dirección
    private ContactoDTO contacto;   // Referencia a DTO de contacto

    // Constructores
    public LaboratorioDTO() {}

    public LaboratorioDTO(Long id, String nombre, String tipo, DireccionDTO direccion, ContactoDTO contacto) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccion = direccion;
        this.contacto = contacto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public DireccionDTO getDireccion() { return direccion; }
    public void setDireccion(DireccionDTO direccion) { this.direccion = direccion; }
    public ContactoDTO getContacto() { return contacto; }
    public void setContacto(ContactoDTO contacto) { this.contacto = contacto; }
}

// Puedes crear DireccionDTO y ContactoDTO en el mismo paquete siguiendo la misma estructura.
