package com.gestion_resultados.ms_gestion_resultados.model;

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

    @Column(name="fecha_muestra", nullable=false)
    private OffsetDateTime fechaMuestra;

    @Column(name="fecha_resultado")
    private OffsetDateTime fechaResultado;

    @Column(nullable=false)
    private String valor;       // texto/JSON con los resultados del examen

    @Column(nullable=false)
    private String unidad;      // unidad de medida (mg/dL, g/L, etc.)

    private String observacion;

    @Column(nullable=false)
    private String estado = "PENDIENTE"; // PENDIENTE | EMITIDO | ANULADO

    // Campos transientes para mostrar nombres (no se guardan en BD)
    @Transient
    private String pacienteNombre;

    @Transient
    private String examenNombre;

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
    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }
    public String getExamenNombre() { return examenNombre; }
    public void setExamenNombre(String examenNombre) { this.examenNombre = examenNombre; }
}
