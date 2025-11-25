// DTO para la entidad Usuario
package com.gestion_users.dto;

/**
 * Ejemplo de DTO para Usuario
 * Incluye atributos principales y referencias a entidades relacionadas
 */
public class UsuarioDTO {
    private Long id;
    private String username;
    private String password;
    private String role;
    private String estado;
    private Long pacienteId;
    private Long empleadoId;

    public UsuarioDTO() {}
    public UsuarioDTO(Long id, String username, String password, String role, String estado, Long pacienteId, Long empleadoId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.estado = estado;
        this.pacienteId = pacienteId;
        this.empleadoId = empleadoId;
    }
    // Getters y Setters
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
