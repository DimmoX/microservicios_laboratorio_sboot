package com.gestion_labs.ms_gestion_labs.service.laboratorio;

import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import java.util.List;

public interface LaboratorioService {
    List<LaboratorioDTO> findAll();
    LaboratorioDTO findById(Long id);
    LaboratorioDTO create(LaboratorioDTO dto);
    LaboratorioDTO update(Long id, LaboratorioDTO dto);
    void delete(Long id);
}