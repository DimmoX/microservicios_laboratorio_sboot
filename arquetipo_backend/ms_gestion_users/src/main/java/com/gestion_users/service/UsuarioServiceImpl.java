// Implementación de UsuarioService
package com.gestion_users.service;

import com.gestion_users.dto.UsuarioDTO;
import com.gestion_users.model.UsuarioModel;
import com.gestion_users.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de UsuarioService
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository repo;
    public UsuarioServiceImpl(UsuarioRepository repo) { this.repo = repo; }

    /**
     * Obtiene todos los usuarios
     * @return lista de usuarios
     */
    @Override
    public List<UsuarioDTO> findAll() {
        return repo.findAll().stream()
            .map(user -> new UsuarioDTO(user.getId(), user.getUsername(), null, user.getRole(), user.getEstado(), user.getPacienteId(), user.getEmpleadoId()))
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return usuario encontrado
     */
    @Override
    public UsuarioDTO findById(Long id) {
        UsuarioModel user = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UsuarioDTO(user.getId(), user.getUsername(), null, user.getRole(), user.getEstado(), user.getPacienteId(), user.getEmpleadoId());
    }

    /**
     * Crea un nuevo usuario
     * @param dto datos del usuario a crear
     * @return usuario creado
     */
    @Override
    public UsuarioDTO create(UsuarioDTO dto) {
        UsuarioModel user = new UsuarioModel();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setEstado(dto.getEstado());
        user.setPacienteId(dto.getPacienteId());
        user.setEmpleadoId(dto.getEmpleadoId());
        return new UsuarioDTO(repo.save(user).getId(), user.getUsername(), null, user.getRole(), user.getEstado(), user.getPacienteId(), user.getEmpleadoId());
    }

    /**
     * Actualiza un usuario existente
     * @param id ID del usuario a actualizar
     * @param dto nuevos datos del usuario
     * @return usuario actualizado
     */
    @Override
    public UsuarioDTO update(Long id, UsuarioDTO dto) {
        UsuarioModel user = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        user.setEstado(dto.getEstado());
        user.setPacienteId(dto.getPacienteId());
        user.setEmpleadoId(dto.getEmpleadoId());
        return new UsuarioDTO(repo.save(user).getId(), user.getUsername(), null, user.getRole(), user.getEstado(), user.getPacienteId(), user.getEmpleadoId());
    }

    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     */
    @Override
    public void delete(Long id) { repo.deleteById(id); }
}
