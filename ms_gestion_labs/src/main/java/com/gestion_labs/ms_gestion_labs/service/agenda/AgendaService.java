package com.gestion_labs.ms_gestion_labs.service.agenda;

import java.util.List;

import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;

public interface AgendaService {
    List<AgendaExamenDTO> findAll();
    List<AgendaExamenDTO> findByPaciente(Long pacienteId);
    AgendaExamenDTO findById(Long id);
    AgendaExamenDTO create(AgendaExamenDTO dto);
    AgendaExamenDTO update(Long id, AgendaExamenDTO dto);
    AgendaExamenDTO cancelar(Long id);
    void delete(Long id);
}