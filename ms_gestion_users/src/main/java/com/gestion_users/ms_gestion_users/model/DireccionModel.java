package com.gestion_users.ms_gestion_users.model;

import jakarta.persistence.*;

@Entity
@Table(name = "direcciones")
public class DireccionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String calle;
    private Integer numero;
    private String ciudad;
    private String comuna;
    private String region;

    // Constructores
    public DireccionModel() {}

    public DireccionModel(String calle, Integer numero, String ciudad, String comuna, String region) {
        this.calle = calle;
        this.numero = numero;
        this.ciudad = ciudad;
        this.comuna = comuna;
        this.region = region;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
