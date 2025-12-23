package com.gestion_labs.ms_gestion_labs.dto;

import java.time.OffsetDateTime;

public class ResultadoExamenDTO {
    private Long id;
    private Long agendaId;          // 1:1 con agenda_examen (simple)
    private Long pacienteId;        // redundante, útil para filtros
    private Long labId;
    private Long examenId;
    private Long empleadoId;        // quien emite
    private OffsetDateTime fechaMuestra;
    private OffsetDateTime fechaResultado;
    private String valor;           // texto/JSON
    private String unidad;
    private String observacion;
    private String estado;          // PENDIENTE | EMITIDO | ANULADO
    
    /**
     * Constructor vacío requerido por frameworks como Jackson para deserialización JSON
     * y por JPA para la creación de instancias de entidades.
     */
    public ResultadoExamenDTO() {
        // Constructor vacío necesario para deserialización JSON y creación de instancias JPA
    }
    
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
