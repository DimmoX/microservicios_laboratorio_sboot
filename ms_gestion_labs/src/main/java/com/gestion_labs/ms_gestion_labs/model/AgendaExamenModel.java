package com.gestion_labs.ms_gestion_labs.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "agenda_examen")
public class AgendaExamenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now(ZoneId.of("America/Santiago"));
        }
    }

    @Column(name="paciente_id", nullable=false)
    private Long pacienteId;

    @Column(name="lab_id", nullable=false)
    private Long labId;

    @Column(name="examen_id", nullable=false)
    private Long examenId;

    @Column(name="empleado_id")
    private Long empleadoId; // opcional

    @Column(name="fecha_hora", nullable=false, columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaHora;

    @Column(name="estado", nullable=false)
    private String estado = "PROGRAMADA"; // PROGRAMADA | CANCELADA | ATENDIDA

    @Column(name="creado_en", columnDefinition = "TIMESTAMP")
    private LocalDateTime creadoEn;

    // Getters/Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }
    public Long getExamenId() { return examenId; }
    public void setExamenId(Long examenId) { this.examenId = examenId; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}