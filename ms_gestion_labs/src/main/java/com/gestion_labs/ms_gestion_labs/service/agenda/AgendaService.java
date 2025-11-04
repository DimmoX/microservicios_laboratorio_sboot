package com.gestion_labs.ms_gestion_labs.service.agenda;

import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import java.util.List;

public interface AgendaService {
    List<AgendaExamenModel> findAll();
    List<AgendaExamenModel> findByPaciente(Long pacienteId);
    AgendaExamenModel findById(Long id);
    AgendaExamenModel create(AgendaExamenModel a);
    AgendaExamenModel update(Long id, AgendaExamenModel a);
    void delete(Long id);
}