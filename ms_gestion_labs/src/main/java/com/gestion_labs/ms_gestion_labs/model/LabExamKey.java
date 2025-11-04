package com.gestion_labs.ms_gestion_labs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LabExamKey implements Serializable {
    @Column(name = "id_laboratorio")
    private Long idLaboratorio;
    
    @Column(name = "id_examen")
    private Long idExamen;

    public LabExamKey() {}
    public LabExamKey(Long idLaboratorio, Long idExamen) {
        this.idLaboratorio = idLaboratorio;
        this.idExamen = idExamen;
    }

    public Long getIdLaboratorio() { return idLaboratorio; }
    public void setIdLaboratorio(Long idLaboratorio) { this.idLaboratorio = idLaboratorio; }

    public Long getIdExamen() { return idExamen; }
    public void setIdExamen(Long idExamen) { this.idExamen = idExamen; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabExamKey)) return false;
        LabExamKey that = (LabExamKey) o;
        return Objects.equals(idLaboratorio, that.idLaboratorio) &&
               Objects.equals(idExamen, that.idExamen);
    }
    @Override public int hashCode() {
        return Objects.hash(idLaboratorio, idExamen);
    }

    @Override public String toString() {
        return "LabExamKey{" +
               "idLaboratorio=" + idLaboratorio +
               ", idExamen=" + idExamen +
               '}';
    }
}