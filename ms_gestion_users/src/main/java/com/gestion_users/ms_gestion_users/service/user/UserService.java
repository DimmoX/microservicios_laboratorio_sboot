package com.gestion_users.ms_gestion_users.service.user;

import java.util.List;

import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.dto.UsuarioUpdateRequest;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;

public interface UserService {
    List<UsuarioModel> findAll();
    UsuarioModel findById(Long id);
    UsuarioModel create(UsuarioModel user);
    UsuarioModel update(Long id, UsuarioUpdateRequest updates);
    void changePassword(Long id, ChangePasswordRequest request);
    void delete(Long id);
}