package com.gestion_users.ms_gestion_users.service.empleado;

import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.ContactoRepository;
import com.gestion_users.ms_gestion_users.repository.DireccionRepository;
import com.gestion_users.ms_gestion_users.repository.EmpleadoRepository;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoServiceImpl.class);

    private final EmpleadoRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final ContactoRepository contactoRepo;
    private final DireccionRepository direccionRepo;

    public EmpleadoServiceImpl(EmpleadoRepository repo, UsuarioRepository usuarioRepo, 
                               ContactoRepository contactoRepo, DireccionRepository direccionRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.contactoRepo = contactoRepo;
        this.direccionRepo = direccionRepo;
    }

    @Override
    public List<EmpleadoModel> findAll() {
        return repo.findAll();
    }

    @Override
    public EmpleadoModel findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));
    }

    @Override
    public EmpleadoModel create(EmpleadoModel empleado) {
        return repo.save(empleado);
    }

    @Override
    public EmpleadoModel update(Long id, EmpleadoModel empleado) {
        EmpleadoModel existing = findById(id);
        
        // Solo actualizar los campos que no son nulos (actualización parcial)
        if (empleado.getPnombre() != null) {
            existing.setPnombre(empleado.getPnombre());
        }
        if (empleado.getSnombre() != null) {
            existing.setSnombre(empleado.getSnombre());
        }
        if (empleado.getPapellido() != null) {
            existing.setPapellido(empleado.getPapellido());
        }
        if (empleado.getSapellido() != null) {
            existing.setSapellido(empleado.getSapellido());
        }
        if (empleado.getRut() != null) {
            existing.setRut(empleado.getRut());
        }
        if (empleado.getCargo() != null) {
            existing.setCargo(empleado.getCargo());
        }
        if (empleado.getDirId() != null) {
            existing.setDirId(empleado.getDirId());
        }
        if (empleado.getContactoId() != null) {
            existing.setContactoId(empleado.getContactoId());
        }
        
        logger.info("Actualizando empleado con ID: {}", id);
        return repo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.info("Intentando eliminar empleado con ID: {} en cascada", id);
        
        // Verificar que el empleado existe
        EmpleadoModel empleado = findById(id);
        logger.info("Empleado encontrado: {} {}, contactoId: {}, dirId: {}", 
                    empleado.getPnombre(), empleado.getPapellido(), 
                    empleado.getContactoId(), empleado.getDirId());
        
        // 1. Buscar y eliminar el usuario asociado (si existe)
        Optional<UsuarioModel> usuario = usuarioRepo.findByEmpleadoId(id);
        if (usuario.isPresent()) {
            logger.info("Eliminando usuario asociado con ID: {} (username: {})", 
                        usuario.get().getId(), usuario.get().getUsername());
            usuarioRepo.delete(usuario.get());
        } else {
            logger.info("No se encontró usuario asociado al empleado");
        }
        
        // 2. Eliminar el empleado
        logger.info("Eliminando empleado con ID: {}", id);
        repo.deleteById(id);
        
        // 3. Eliminar el contacto asociado (si existe)
        if (empleado.getContactoId() != null) {
            logger.info("Eliminando contacto con ID: {}", empleado.getContactoId());
            contactoRepo.deleteById(empleado.getContactoId());
        } else {
            logger.info("No hay contacto asociado para eliminar");
        }
        
        // 4. Eliminar la dirección asociada (si existe)
        if (empleado.getDirId() != null) {
            logger.info("Eliminando dirección con ID: {}", empleado.getDirId());
            direccionRepo.deleteById(empleado.getDirId());
        } else {
            logger.info("No hay dirección asociada para eliminar");
        }
        
        logger.info("Empleado con ID: {} y todos sus registros relacionados eliminados exitosamente", id);
    }
}