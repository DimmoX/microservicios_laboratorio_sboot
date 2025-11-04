package com.gestion_labs.ms_gestion_labs.service.lab_exam;

import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import java.util.List;

public interface LabExamService {
    List<LabExamModel> listAll();
    List<LabExamModel> listByLaboratorio(Long idLaboratorio);
    List<LabExamModel> listByExamen(Long idExamen);

    LabExamModel get(Long idLaboratorio, Long idExamen);
    LabExamModel upsert(LabExamModel model); // crea o actualiza precio/vigencias
    void delete(Long idLaboratorio, Long idExamen);
}