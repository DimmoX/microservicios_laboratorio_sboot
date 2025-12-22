package com.gestion_labs.ms_gestion_labs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgendaExamenDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long labId;
    private String laboratorioNombre;
    private Long examenId;
    private String examenNombre;
    private Long empleadoId;
    private LocalDateTime fechaHora;
    private String estado;
    private LocalDateTime creadoEn;
    private BigDecimal precio;

    // Constructor vacío
    public AgendaExamenDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public Long getLabId() { return labId; }
    public void setLabId(Long labId) { this.labId = labId; }

    public String getLaboratorioNombre() { return laboratorioNombre; }
    public void setLaboratorioNombre(String laboratorioNombre) { this.laboratorioNombre = laboratorioNombre; }

    public Long getExamenId() { return examenId; }
    public void setExamenId(Long examenId) { this.examenId = examenId; }

    public String getExamenNombre() { return examenNombre; }
    public void setExamenNombre(String examenNombre) { this.examenNombre = examenNombre; }

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public Integer getPrecio() { 
        return precio != null ? precio.intValue() : null; 
    }
    
    public void setPrecio(BigDecimal precio) { 
        this.precio = precio; 
    }
}
