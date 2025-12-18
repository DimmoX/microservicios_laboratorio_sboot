package com.gestion_resultados.ms_gestion_resultados.service.resultados;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import java.util.List;

public interface ResultadoService {
    List<ResultadoExamenModel> findAll();
    List<ResultadoExamenModel> findByPaciente(Long pacienteId);
    ResultadoExamenModel findById(Long id);
    ResultadoExamenModel create(ResultadoExamenModel r);
    ResultadoExamenModel update(Long id, ResultadoExamenModel r);
    void delete(Long id);
}
