package com.gestion_users.ms_gestion_users.service.user;

import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import java.util.List;

public interface UserService {
    List<UsuarioModel> findAll();
    UsuarioModel findById(Long id);
    UsuarioModel create(UsuarioModel user);
    UsuarioModel update(Long id, UsuarioModel user);
    void delete(Long id);
}