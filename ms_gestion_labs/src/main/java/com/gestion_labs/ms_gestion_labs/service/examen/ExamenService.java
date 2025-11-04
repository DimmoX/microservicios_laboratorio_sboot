package com.gestion_labs.ms_gestion_labs.service.examen;

import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import java.util.List;

public interface ExamenService {
    List<ExamenModel> findAll();
    ExamenModel findById(Long id);
    ExamenModel create(ExamenModel ex);
    ExamenModel update(Long id, ExamenModel ex);
    void delete(Long id);
}