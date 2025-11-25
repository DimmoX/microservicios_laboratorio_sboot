package com.gestion_labs.dto;

/**
 * DTO para la entidad Dirección
 */
public class DireccionDTO {
    private Long id;
    private String calle;
    private Integer numero;
    private String ciudad;
    private String comuna;
    private String region;

    public DireccionDTO() {}
    public DireccionDTO(Long id, String calle, Integer numero, String ciudad, String comuna, String region) {
        this.id = id;
        this.calle = calle;
        this.numero = numero;
        this.ciudad = ciudad;
        this.comuna = comuna;
        this.region = region;
    }
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
