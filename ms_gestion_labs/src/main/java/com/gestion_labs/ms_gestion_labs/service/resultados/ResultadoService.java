package com.gestion_labs.ms_gestion_labs.service.resultados;

import com.gestion_labs.ms_gestion_labs.model.ResultadoExamenModel;
import java.util.List;

public interface ResultadoService {
    List<ResultadoExamenModel> findAll();
    List<ResultadoExamenModel> findByPaciente(Long pacienteId);
    ResultadoExamenModel findById(Long id);
    ResultadoExamenModel create(ResultadoExamenModel r);
    ResultadoExamenModel update(Long id, ResultadoExamenModel r);
    void delete(Long id);
}