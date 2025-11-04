package com.gestion_users.ms_gestion_users.dto;

import java.time.OffsetDateTime;

public class PacienteDTO {
    private Long id;
    private String pnombre;
    private String snombre;
    private String papellido;
    private String sapellido;
    private String rut;
    private Long dirId;
    private Long contactoId;
    private OffsetDateTime creadoEn; // solo lectura

    // getters/setters …
}