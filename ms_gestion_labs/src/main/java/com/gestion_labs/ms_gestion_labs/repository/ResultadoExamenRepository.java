package com.gestion_labs.ms_gestion_labs.repository;

import com.gestion_labs.ms_gestion_labs.model.ResultadoExamenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultadoExamenRepository extends JpaRepository<ResultadoExamenModel, Long> {
    List<ResultadoExamenModel> findByPacienteId(Long pacienteId);
    List<ResultadoExamenModel> findByLabId(Long labId);
    List<ResultadoExamenModel> findByExamenId(Long examenId);
}