package com.gestion_users.ms_gestion_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByUsername(String username);
    Optional<UsuarioModel> findByEmpleadoId(Long empleadoId);
    Optional<UsuarioModel> findByPacienteId(Long pacienteId);
}