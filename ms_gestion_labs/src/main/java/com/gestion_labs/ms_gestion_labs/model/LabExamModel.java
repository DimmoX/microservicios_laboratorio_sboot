package com.gestion_labs.ms_gestion_labs.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lab_exam")
public class LabExamModel {

    @EmbeddedId
    private LabExamKey id;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal precio;

    @Column(name = "vigente_desde")
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    public LabExamModel() {}
    public LabExamModel(LabExamKey id) { this.id = id; }

    // Getters/Setters
    
    public LabExamKey getId() { return id; }
    public void setId(LabExamKey id) { this.id = id; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }
    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }
    

    @Override
    public String toString() {
        return "LabExamModel{" +
                "id=" + id +
                ", precio=" + precio +
                ", vigenteDesde=" + vigenteDesde +
                ", vigenteHasta=" + vigenteHasta +
                '}';
    }
}