package com.gestion_users.ms_gestion_users.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contactos")
public class ContactoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fono1;
    private String fono2;
    private String email;

    // Constructores
    public ContactoModel() {}

    public ContactoModel(String fono1, String fono2, String email) {
        this.fono1 = fono1;
        this.fono2 = fono2;
        this.email = email;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFono1() {
        return fono1;
    }

    public void setFono1(String fono1) {
        this.fono1 = fono1;
    }

    public String getFono2() {
        return fono2;
    }

    public void setFono2(String fono2) {
        this.fono2 = fono2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
