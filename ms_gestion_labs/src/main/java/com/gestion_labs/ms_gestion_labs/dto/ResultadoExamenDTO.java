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
    // getters/setters …
}
