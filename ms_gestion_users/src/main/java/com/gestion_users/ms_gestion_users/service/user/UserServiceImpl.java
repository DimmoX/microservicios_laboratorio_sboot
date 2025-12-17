package com.gestion_users.ms_gestion_users.service.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.dto.UsuarioUpdateRequest;
import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.PacienteRepository;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioProfileService usuarioProfileService;
    private final PacienteRepository pacienteRepo;

    public UserServiceImpl(UsuarioRepository repo, PasswordEncoder passwordEncoder, 
                          UsuarioProfileService usuarioProfileService,
                          PacienteRepository pacienteRepo) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.usuarioProfileService = usuarioProfileService;
        this.pacienteRepo = pacienteRepo;
    }

    @Override
    public List<UsuarioModel> findAll() { return repo.findAll(); }

    @Override
    public UsuarioModel findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public UsuarioModel create(UsuarioModel user) {
        // Asegurar que el ID sea null para que Hibernate lo genere automáticamente
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Si es un usuario PATIENT y no tiene pacienteId, crear el registro de paciente automáticamente
        if ("PATIENT".equalsIgnoreCase(user.getRole()) && user.getPacienteId() == null) {
            logger.info("Creando registro de paciente para usuario: {}", user.getUsername());
            
            // Crear paciente básico (los datos adicionales se pueden actualizar después)
            PacienteModel paciente = new PacienteModel();
            paciente.setPnombre("Paciente"); // Valor temporal, se puede actualizar después
            paciente.setPapellido("Nuevo");  // Valor temporal, se puede actualizar después
            
            // Guardar paciente
            PacienteModel pacienteGuardado = pacienteRepo.save(paciente);
            logger.info("Paciente creado con ID: {}", pacienteGuardado.getId());
            
            // Asociar el ID del paciente al usuario
            user.setPacienteId(pacienteGuardado.getId());
        }
        
        UsuarioModel savedUser = repo.save(user);
        logger.info("Usuario creado con ID: {}, rol: {}, pacienteId: {}", 
            savedUser.getId(), savedUser.getRole(), savedUser.getPacienteId());
        
        return savedUser;
    }

    @Override
    public UsuarioModel update(Long id, UsuarioUpdateRequest updates) {
        UsuarioModel user = findById(id);
        usuarioProfileService.applyUpdates(user, updates);
        return repo.save(user);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        if (request == null || request.getOldPassword() == null || request.getNewPassword() == null) {
            throw new RuntimeException("Solicitud inválida");
        }
        UsuarioModel user = findById(id);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repo.save(user);
    }

    @Override
    public void delete(Long id) { repo.deleteById(id); }
}