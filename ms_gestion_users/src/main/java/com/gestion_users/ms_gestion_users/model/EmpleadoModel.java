package com.gestion_users.ms_gestion_users.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "empleados")
public class EmpleadoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String pnombre;
    private String snombre;
    private String papellido;
    private String sapellido;
    private String rut;
    private String cargo;

    @Column(name = "dir_id")
    private Long dirId;
    
    @Column(name = "contacto_id")
    private Long contactoId;

    @Column(name = "creado_en", columnDefinition = "TIMESTAMP DEFAULT SYSTIMESTAMP")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime creadoEn;

    // Getters y setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getDirId() {
        return dirId;
    }

    public void setDirId(Long dirId) {
        this.dirId = dirId;
    }

    public Long getContactoId() {
        return contactoId;
    }

    public void setContactoId(Long contactoId) {
        this.contactoId = contactoId;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
