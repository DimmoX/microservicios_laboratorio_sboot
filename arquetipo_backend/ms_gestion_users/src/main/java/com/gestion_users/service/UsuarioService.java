// Interfaz de servicio para Usuario
package com.gestion_users.service;

import com.gestion_users.dto.UsuarioDTO;
import java.util.List;

/**
 * Métodos CRUD para Usuario
 */
public interface UsuarioService {
    List<UsuarioDTO> findAll();
    UsuarioDTO findById(Long id);
    UsuarioDTO create(UsuarioDTO dto);
    UsuarioDTO update(Long id, UsuarioDTO dto);
    void delete(Long id);
}
