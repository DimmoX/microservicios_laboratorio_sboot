package com.gestion_users.ms_gestion_users.dto;

public class ContactoDTO {
    private String fono1;
    private String fono2;
    private String email;

    // Constructores
    public ContactoDTO() {}

    public ContactoDTO(String fono1, String fono2, String email) {
        this.fono1 = fono1;
        this.fono2 = fono2;
        this.email = email;
    }

    // Getters y Setters
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
