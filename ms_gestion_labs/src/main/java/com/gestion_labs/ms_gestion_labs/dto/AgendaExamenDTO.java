package com.gestion_labs.ms_gestion_labs.dto;

import java.time.OffsetDateTime;

public class AgendaExamenDTO {
    private Long id;
    private Long pacienteId;
    private Long labId;
    private Long examenId;
    private Long empleadoId;        // opcional: quien agenda/atiende
    private OffsetDateTime fechaHora;
    private String estado;          // PROGRAMADA | CANCELADA | ATENDIDA
    private OffsetDateTime creadoEn; // solo lectura
    // getters/setters …
}
