package com.gestion_users.ms_gestion_users.dto;

public class UsuarioDTO {
    private Long id;
    private String username;   // email
    private String password;   // en POST/PUT; en GET puedes omitirlo
    private String role;       // ADMIN | LAB_EMPLOYEE | PATIENT
    private String estado;     // ACTIVO | INACTIVO
    private Long pacienteId;   // opcional: vínculo lógico al paciente
    private Long empleadoId;   // opcional: vínculo lógico al empleado

    public UsuarioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
}
