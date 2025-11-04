package com.gestion_users.ms_gestion_users.dto;

/**
 * DTO de respuesta para el registro exitoso de paciente o empleado.
 * Incluye los IDs generados y la información del usuario creado.
 */
public class RegistroResponse {
    private Long pacienteId;
    private Long empleadoId;
    private Long usuarioId;
    private String username;
    private String role;
    private String mensaje;

    // Constructores
    public RegistroResponse() {}

    public RegistroResponse(Long pacienteId, Long empleadoId, Long usuarioId, String username, String role, String mensaje) {
        this.pacienteId = pacienteId;
        this.empleadoId = empleadoId;
        this.usuarioId = usuarioId;
        this.username = username;
        this.role = role;
        this.mensaje = mensaje;
    }

    // Constructor para paciente
    public static RegistroResponse forPaciente(Long pacienteId, Long usuarioId, String username) {
        return new RegistroResponse(
            pacienteId, 
            null, 
            usuarioId, 
            username, 
            "PATIENT", 
            "Paciente registrado exitosamente"
        );
    }

    // Constructor para empleado
    public static RegistroResponse forEmpleado(Long empleadoId, Long usuarioId, String username) {
        return new RegistroResponse(
            null, 
            empleadoId, 
            usuarioId, 
            username, 
            "LAB_EMPLOYEE", 
            "Empleado registrado exitosamente"
        );
    }

    // Getters y Setters
    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
