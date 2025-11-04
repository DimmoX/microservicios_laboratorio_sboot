package com.gestion_labs.ms_gestion_labs.repository;

import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendaExamenRepository extends JpaRepository<AgendaExamenModel, Long> {
    List<AgendaExamenModel> findByPacienteId(Long pacienteId);
    List<AgendaExamenModel> findByLabId(Long labId);
}