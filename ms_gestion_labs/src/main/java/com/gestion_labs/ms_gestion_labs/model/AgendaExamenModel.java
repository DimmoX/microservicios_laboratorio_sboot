package com.gestion_labs.ms_gestion_labs.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "agenda_examen")
public class AgendaExamenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="paciente_id", nullable=false)
    private Long pacienteId;

    @Column(name="lab_id", nullable=false)
    private Long labId;

    @Column(name="examen_id", nullable=false)
    private Long examenId;

    @Column(name="empleado_id")
    private Long empleadoId; // opcional

    @Column(name="fecha_hora", nullable=false)
    private OffsetDateTime fechaHora;

    @Column(name="estado", nullable=false)
    private String estado = "PROGRAMADA"; // PROGRAMADA | CANCELADA | ATENDIDA

    @Column(name="creado_en", columnDefinition="TIMESTAMP DEFAULT SYSTIMESTAMP")
    private OffsetDateTime creadoEn;

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
    public OffsetDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(OffsetDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}