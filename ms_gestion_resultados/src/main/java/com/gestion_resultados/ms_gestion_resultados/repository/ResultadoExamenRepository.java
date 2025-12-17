package com.gestion_resultados.ms_gestion_resultados.repository;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResultadoExamenRepository extends JpaRepository<ResultadoExamenModel, Long> {
    List<ResultadoExamenModel> findByPacienteId(Long pacienteId);
    List<ResultadoExamenModel> findByLabId(Long labId);
    List<ResultadoExamenModel> findByExamenId(Long examenId);
    Optional<ResultadoExamenModel> findByAgendaId(Long agendaId);
}
