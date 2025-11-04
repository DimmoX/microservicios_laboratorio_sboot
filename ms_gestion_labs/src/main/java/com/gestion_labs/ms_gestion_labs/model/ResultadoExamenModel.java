package com.gestion_labs.ms_gestion_labs.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "resultado_examen")
public class ResultadoExamenModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="agenda_id", nullable=false)
    private Long agendaId;  // 1:1 recomendado con agenda_examen

    @Column(name="paciente_id", nullable=false)
    private Long pacienteId;

    @Column(name="lab_id", nullable=false)
    private Long labId;

    @Column(name="examen_id", nullable=false)
    private Long examenId;

    @Column(name="empleado_id", nullable=false)
    private Long empleadoId; // quien emite

    @Column(name="fecha_muestra")
    private OffsetDateTime fechaMuestra;

    @Column(name="fecha_resultado")
    private OffsetDateTime fechaResultado;

    private String valor;       // texto/JSON
    private String unidad;
    private String observacion;

    @Column(nullable=false)
    private String estado = "PENDIENTE"; // PENDIENTE | EMITIDO | ANULADO

    // Getters/Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAgendaId() { return agendaId; }
    public void setAgendaId(Long agendaId) { this.agendaId = agendaId; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }
    public Long getExamenId() { return examenId; }
    public void setExamenId(Long examenId) { this.examenId = examenId; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public OffsetDateTime getFechaMuestra() { return fechaMuestra; }
    public void setFechaMuestra(OffsetDateTime fechaMuestra) { this.fechaMuestra = fechaMuestra; }
    public OffsetDateTime getFechaResultado() { return fechaResultado; }
    public void setFechaResultado(OffsetDateTime fechaResultado) { this.fechaResultado = fechaResultado; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}