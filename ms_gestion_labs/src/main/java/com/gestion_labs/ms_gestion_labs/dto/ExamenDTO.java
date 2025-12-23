package com.gestion_labs.ms_gestion_labs.dto;

public class ExamenDTO {
    private Long id;
    private String codigo;   // max 4
    private String nombre;   // max 50
    private String tipo;     // max 20
    
    /**
     * Constructor vacío requerido por frameworks como Jackson para deserialización JSON
     * y por JPA para la creación de instancias de entidades.
     */
    public ExamenDTO() {
        // Constructor vacío necesario para deserialización JSON y creación de instancias JPA
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
