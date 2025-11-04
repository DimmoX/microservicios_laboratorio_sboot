package com.gestion_users.ms_gestion_users.dto;

import java.time.OffsetDateTime;

public class EmpleadoDTO {
    private Long id;
    private String pnombre;
    private String snombre;
    private String papellido;
    private String sapellido;
    private String rut;
    private String cargo;      // hasta 40
    private Long dirId;        // FK direcciones.id
    private Long contactoId;   // FK contactos.id
    private OffsetDateTime creadoEn; // solo lectura (TIMESTAMP)

    // getters/setters …
}