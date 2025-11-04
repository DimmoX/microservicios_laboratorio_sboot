package com.gestion_labs.ms_gestion_labs.repository;

import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LabExamRepository extends JpaRepository<LabExamModel, LabExamKey> {
    List<LabExamModel> findById_IdLaboratorio(Long idLaboratorio);
    List<LabExamModel> findById_IdExamen(Long idExamen);
}