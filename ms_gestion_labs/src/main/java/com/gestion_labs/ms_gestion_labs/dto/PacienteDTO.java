package com.gestion_labs.ms_gestion_labs.dto;

public class PacienteDTO {
    private Long id;
    private String pnombre;
    private String snombre;
    private String papellido;
    private String sapellido;
    
    public PacienteDTO() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPnombre() { return pnombre; }
    public void setPnombre(String pnombre) { this.pnombre = pnombre; }
    
    public String getSnombre() { return snombre; }
    public void setSnombre(String snombre) { this.snombre = snombre; }
    
    public String getPapellido() { return papellido; }
    public void setPapellido(String papellido) { this.papellido = papellido; }
    
    public String getSapellido() { return sapellido; }
    public void setSapellido(String sapellido) { this.sapellido = sapellido; }
    
    /**
     * Construye el nombre completo del paciente
     */
    public String getNombreCompleto() {
        StringBuilder nombre = new StringBuilder();
        if (pnombre != null && !pnombre.isEmpty()) nombre.append(pnombre).append(" ");
        if (snombre != null && !snombre.isEmpty()) nombre.append(snombre).append(" ");
        if (papellido != null && !papellido.isEmpty()) nombre.append(papellido).append(" ");
        if (sapellido != null && !sapellido.isEmpty()) nombre.append(sapellido);
        return nombre.toString().trim();
    }
}
