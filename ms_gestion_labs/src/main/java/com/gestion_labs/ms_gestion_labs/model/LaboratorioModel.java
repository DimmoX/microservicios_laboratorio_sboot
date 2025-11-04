package com.gestion_labs.ms_gestion_labs.model;

import jakarta.persistence.*;

@Entity
@Table(name = "laboratorios")
public class LaboratorioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;     // VARCHAR2(50)
    private String tipo;       // VARCHAR2(20)

    @Column(name = "dir_id", nullable = false)
    private Long dirId;

    @Column(name = "contacto_id", nullable = false)
    private Long contactoId;

    // Getters/Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getDirId() { return dirId; }
    public void setDirId(Long dirId) { this.dirId = dirId; }
    public Long getContactoId() { return contactoId; }
    public void setContactoId(Long contactoId) { this.contactoId = contactoId; }

    @Override
    public String toString() {
        return "LaboratorioModel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", dirId=" + dirId +
                ", contactoId=" + contactoId +
                '}';
    }
}