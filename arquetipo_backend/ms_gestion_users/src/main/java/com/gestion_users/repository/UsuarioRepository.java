// Repositorio JPA para Usuario
package com.gestion_users.repository;

import com.gestion_users.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByUsername(String username);
    Optional<UsuarioModel> findByEmpleadoId(Long empleadoId);
    Optional<UsuarioModel> findByPacienteId(Long pacienteId);
    // Puedes agregar métodos personalizados aquí
}
