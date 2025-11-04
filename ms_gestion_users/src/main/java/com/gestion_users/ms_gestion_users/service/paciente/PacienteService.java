package com.gestion_users.ms_gestion_users.service.paciente;

import com.gestion_users.ms_gestion_users.model.PacienteModel;
import java.util.List;

public interface PacienteService {
    List<PacienteModel> findAll();
    PacienteModel findById(Long id);
    PacienteModel create(PacienteModel paciente);
    PacienteModel update(Long id, PacienteModel paciente);
    void delete(Long id);
}