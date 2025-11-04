package com.gestion_users.ms_gestion_users.service.paciente;

import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.ContactoRepository;
import com.gestion_users.ms_gestion_users.repository.DireccionRepository;
import com.gestion_users.ms_gestion_users.repository.PacienteRepository;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    private static final Logger logger = LoggerFactory.getLogger(PacienteServiceImpl.class);

    private final PacienteRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final ContactoRepository contactoRepo;
    private final DireccionRepository direccionRepo;

    public PacienteServiceImpl(PacienteRepository repo, UsuarioRepository usuarioRepo,
                               ContactoRepository contactoRepo, DireccionRepository direccionRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.contactoRepo = contactoRepo;
        this.direccionRepo = direccionRepo;
    }

    @Override
    public List<PacienteModel> findAll() {
        return repo.findAll();
    }

    @Override
    public PacienteModel findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    @Override
    public PacienteModel create(PacienteModel paciente) {
        return repo.save(paciente);
    }

    @Override
    public PacienteModel update(Long id, PacienteModel paciente) {
        PacienteModel existing = findById(id);
        
        // Solo actualizar los campos que no son nulos (actualización parcial)
        if (paciente.getPnombre() != null) {
            existing.setPnombre(paciente.getPnombre());
        }
        if (paciente.getSnombre() != null) {
            existing.setSnombre(paciente.getSnombre());
        }
        if (paciente.getPapellido() != null) {
            existing.setPapellido(paciente.getPapellido());
        }
        if (paciente.getSapellido() != null) {
            existing.setSapellido(paciente.getSapellido());
        }
        if (paciente.getRut() != null) {
            existing.setRut(paciente.getRut());
        }
        if (paciente.getDirId() != null) {
            existing.setDirId(paciente.getDirId());
        }
        if (paciente.getContactoId() != null) {
            existing.setContactoId(paciente.getContactoId());
        }
        
        logger.info("Actualizando paciente con ID: {}", id);
        return repo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Intentando eliminar paciente con ID: {} en cascada", id);
        
        // Verificar que el paciente existe
        PacienteModel paciente = findById(id);
        logger.info("Paciente encontrado: {} {}, contactoId: {}, dirId: {}", 
                    paciente.getPnombre(), paciente.getPapellido(), 
                    paciente.getContactoId(), paciente.getDirId());
        
        // 1. Buscar y eliminar el usuario asociado (si existe)
        Optional<UsuarioModel> usuario = usuarioRepo.findByPacienteId(id);
        if (usuario.isPresent()) {
            logger.info("Eliminando usuario asociado con ID: {} (username: {})", 
                        usuario.get().getId(), usuario.get().getUsername());
            usuarioRepo.delete(usuario.get());
        } else {
            logger.info("No se encontró usuario asociado al paciente");
        }
        
        // 2. Eliminar el paciente
        logger.info("Eliminando paciente con ID: {}", id);
        repo.deleteById(id);
        
        // 3. Eliminar el contacto asociado (si existe)
        if (paciente.getContactoId() != null) {
            logger.info("Eliminando contacto con ID: {}", paciente.getContactoId());
            contactoRepo.deleteById(paciente.getContactoId());
        } else {
            logger.info("No hay contacto asociado para eliminar");
        }
        
        // 4. Eliminar la dirección asociada (si existe)
        if (paciente.getDirId() != null) {
            logger.info("Eliminando dirección con ID: {}", paciente.getDirId());
            direccionRepo.deleteById(paciente.getDirId());
        } else {
            logger.info("No hay dirección asociada para eliminar");
        }
        
        logger.info("Paciente con ID: {} y todos sus registros relacionados eliminados exitosamente", id);
    }
}