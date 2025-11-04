package com.gestion_users.ms_gestion_users.dto;

public class UsuarioDTO {
    private Long id;
    private String username;   // email
    private String password;   // en POST/PUT; en GET puedes omitirlo
    private String role;       // ADMIN | LAB_EMPLOYEE | PATIENT
    private String estado;     // ACTIVO | INACTIVO
    private Long pacienteId;   // opcional: vínculo lógico al paciente
    private Long empleadoId;   // opcional: vínculo lógico al empleado
    // getters/setters …
}
