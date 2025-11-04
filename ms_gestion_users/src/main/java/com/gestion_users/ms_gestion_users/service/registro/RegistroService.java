package com.gestion_users.ms_gestion_users.service.registro;

import com.gestion_users.ms_gestion_users.dto.*;

public interface RegistroService {
    RegistroResponse registrarPaciente(RegistroPacienteRequest request);
    RegistroResponse registrarEmpleado(RegistroEmpleadoRequest request);
}
