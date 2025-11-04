package com.gestion_labs.ms_gestion_labs.service.laboratorio;

import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.ms_gestion_labs.model.ContactoModel;
import com.gestion_labs.ms_gestion_labs.model.DireccionModel;
import com.gestion_labs.ms_gestion_labs.model.LaboratorioModel;
import com.gestion_labs.ms_gestion_labs.repository.ContactoRepository;
import com.gestion_labs.ms_gestion_labs.repository.DireccionRepository;
import com.gestion_labs.ms_gestion_labs.repository.LaboratorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaboratorioServiceImpl implements LaboratorioService {

    private final LaboratorioRepository repo;
    private final DireccionRepository direccionRepo;
    private final ContactoRepository contactoRepo;
    
    public LaboratorioServiceImpl(LaboratorioRepository repo, DireccionRepository direccionRepo, ContactoRepository contactoRepo) { 
        this.repo = repo;
        this.direccionRepo = direccionRepo;
        this.contactoRepo = contactoRepo;
    }

    @Override 
    public List<LaboratorioDTO> findAll() { 
        return repo.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override 
    public LaboratorioDTO findById(Long id) {
        LaboratorioModel lab = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado: " + id));
        return convertToDTO(lab);
    }

    @Override
    @Transactional
    public LaboratorioDTO create(LaboratorioDTO dto) {
        // 1. Crear la dirección
        DireccionModel direccion = dto.getDireccion();
        if (direccion == null) {
            throw new RuntimeException("La dirección es obligatoria");
        }
        DireccionModel direccionGuardada = direccionRepo.save(direccion);
        
        // 2. Crear el contacto
        ContactoModel contacto = dto.getContacto();
        if (contacto == null) {
            throw new RuntimeException("El contacto es obligatorio");
        }
        ContactoModel contactoGuardado = contactoRepo.save(contacto);
        
        // 3. Crear el laboratorio
        LaboratorioModel lab = new LaboratorioModel();
        lab.setNombre(dto.getNombre());
        lab.setTipo(dto.getTipo());
        lab.setDirId(direccionGuardada.getId());
        lab.setContactoId(contactoGuardado.getId());
        
        LaboratorioModel labGuardado = repo.save(lab);
        
        return convertToDTO(labGuardado);
    }

    @Override
    @Transactional
    public LaboratorioDTO update(Long id, LaboratorioDTO dto) {
        LaboratorioModel lab = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado: " + id));
        
        // Actualización parcial del laboratorio
        if (dto.getNombre() != null) lab.setNombre(dto.getNombre());
        if (dto.getTipo() != null) lab.setTipo(dto.getTipo());
        
        // Actualizar dirección si viene en el DTO
        if (dto.getDireccion() != null) {
            DireccionModel direccion = direccionRepo.findById(lab.getDirId())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada: " + lab.getDirId()));
            
            if (dto.getDireccion().getCalle() != null) direccion.setCalle(dto.getDireccion().getCalle());
            if (dto.getDireccion().getNumero() != null) direccion.setNumero(dto.getDireccion().getNumero());
            if (dto.getDireccion().getCiudad() != null) direccion.setCiudad(dto.getDireccion().getCiudad());
            if (dto.getDireccion().getComuna() != null) direccion.setComuna(dto.getDireccion().getComuna());
            if (dto.getDireccion().getRegion() != null) direccion.setRegion(dto.getDireccion().getRegion());
            
            direccionRepo.save(direccion);
        }
        
        // Actualizar contacto si viene en el DTO
        if (dto.getContacto() != null) {
            ContactoModel contacto = contactoRepo.findById(lab.getContactoId())
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado: " + lab.getContactoId()));
            
            if (dto.getContacto().getFono1() != null) contacto.setFono1(dto.getContacto().getFono1());
            if (dto.getContacto().getFono2() != null) contacto.setFono2(dto.getContacto().getFono2());
            if (dto.getContacto().getEmail() != null) contacto.setEmail(dto.getContacto().getEmail());
            
            contactoRepo.save(contacto);
        }
        
        LaboratorioModel labActualizado = repo.save(lab);
        return convertToDTO(labActualizado);
    }

    @Override 
    public void delete(Long id) { 
        repo.deleteById(id); 
    }
    
    /**
     * Convierte LaboratorioModel a LaboratorioDTO con dirección y contacto embebidos
     */
    private LaboratorioDTO convertToDTO(LaboratorioModel lab) {
        DireccionModel direccion = direccionRepo.findById(lab.getDirId())
            .orElse(null);
        
        ContactoModel contacto = contactoRepo.findById(lab.getContactoId())
            .orElse(null);
        
        return new LaboratorioDTO(
            lab.getId(),
            lab.getNombre(),
            lab.getTipo(),
            direccion,
            contacto
        );
    }
}
