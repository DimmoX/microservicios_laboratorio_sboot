// Interfaz de servicio para Laboratorio
package com.gestion_labs.service;

import java.util.List;

import com.gestion_labs.dto.LaboratorioDTO;

/**
 * Métodos CRUD para Laboratorio
 */
public interface LaboratorioService {
    List<LaboratorioDTO> findAll();
    LaboratorioDTO findById(Long id);
    LaboratorioDTO create(LaboratorioDTO dto);
    LaboratorioDTO update(Long id, LaboratorioDTO dto);
    void delete(Long id);
}
