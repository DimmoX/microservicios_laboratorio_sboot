package com.gestion_labs.ms_gestion_labs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;

public interface LabExamRepository extends JpaRepository<LabExamModel, LabExamKey> {
    List<LabExamModel> findById_IdLaboratorio(Long idLaboratorio);
    List<LabExamModel> findById_IdExamen(Long idExamen);
    Optional<LabExamModel> findById_IdLaboratorioAndId_IdExamen(Long idLaboratorio, Long idExamen);
}