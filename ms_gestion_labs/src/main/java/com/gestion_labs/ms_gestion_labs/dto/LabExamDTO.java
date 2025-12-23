package com.gestion_labs.ms_gestion_labs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LabExamDTO {
    private Long idLaboratorio;
    private Long idExamen;
    private BigDecimal precio;
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta;
    private String nombreLab;
    private String nombreExamen;

    public LabExamDTO() {
        // Constructor vacío necesario para deserialización JSON y creación de instancias JPA
    }

    // Getters and Setters
    public Long getIdLaboratorio() { return idLaboratorio; }
    public void setIdLaboratorio(Long idLaboratorio) { this.idLaboratorio = idLaboratorio; }

    public Long getIdExamen() { return idExamen; }
    public void setIdExamen(Long idExamen) { this.idExamen = idExamen; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }

    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }

    public String getNombreLab() { return nombreLab; }
    public void setNombreLab(String nombreLab) { this.nombreLab = nombreLab; }

    public String getNombreExamen() { return nombreExamen; }
    public void setNombreExamen(String nombreExamen) { this.nombreExamen = nombreExamen; }
}
