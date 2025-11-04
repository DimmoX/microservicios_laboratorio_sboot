package com.gestion_users.ms_gestion_users.service.user;

import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UsuarioRepository repo) { this.repo = repo; }

    @Override
    public List<UsuarioModel> findAll() { return repo.findAll(); }

    @Override
    public UsuarioModel findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public UsuarioModel create(UsuarioModel user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @Override
    public UsuarioModel update(Long id, UsuarioModel newUser) {
        UsuarioModel user = findById(id);
        user.setUsername(newUser.getUsername());
        user.setRole(newUser.getRole());
        user.setEstado(newUser.getEstado());
        if (newUser.getPassword() != null) {
            user.setPassword(encoder.encode(newUser.getPassword()));
        }
        return repo.save(user);
    }

    @Override
    public void delete(Long id) { repo.deleteById(id); }
}