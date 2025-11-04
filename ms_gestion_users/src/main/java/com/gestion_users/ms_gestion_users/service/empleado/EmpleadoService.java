package com.gestion_users.ms_gestion_users.service.empleado;

import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import java.util.List;

public interface EmpleadoService {
    List<EmpleadoModel> findAll();
    EmpleadoModel findById(Long id);
    EmpleadoModel create(EmpleadoModel empleado);
    EmpleadoModel update(Long id, EmpleadoModel empleado);
    void delete(Long id);
}