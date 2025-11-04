package com.gestion_labs.ms_gestion_labs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LabExamDTO {
    private Long idLaboratorio;
    private Long idExamen;
    private BigDecimal precio;     // NUMBER(8,2)
    private LocalDate vigenteDesde;
    private LocalDate vigenteHasta; // CHECK: null o > vigenteDesde
    // getters/setters …
}
