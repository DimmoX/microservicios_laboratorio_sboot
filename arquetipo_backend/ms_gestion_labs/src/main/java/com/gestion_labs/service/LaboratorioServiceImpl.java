package com.gestion_labs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.model.LaboratorioModel;
import com.gestion_labs.repository.LaboratorioRepository;

/**
 * Implementación de LaboratorioService
 */
@Service
public class LaboratorioServiceImpl implements LaboratorioService {
    private final LaboratorioRepository repo;
    public LaboratorioServiceImpl(LaboratorioRepository repo) { this.repo = repo; }

    @Override
    public List<LaboratorioDTO> findAll() {
        // Ejemplo: convertir entidades a DTO
        return repo.findAll().stream()
            .map(lab -> new LaboratorioDTO(lab.getId(), lab.getNombre(), lab.getTipo(), null, null))
            .collect(Collectors.toList());
    }
    @Override
    public LaboratorioDTO findById(Long id) {
        LaboratorioModel lab = repo.findById(id).orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
        return new LaboratorioDTO(lab.getId(), lab.getNombre(), lab.getTipo(), null, null);
    }
    @Override
    public LaboratorioDTO create(LaboratorioDTO dto) {
        LaboratorioModel lab = new LaboratorioModel();
        lab.setNombre(dto.getNombre());
        lab.setTipo(dto.getTipo());
        // dirId/contactoId pueden venir de otros servicios
        return new LaboratorioDTO(repo.save(lab).getId(), lab.getNombre(), lab.getTipo(), null, null);
    }
    @Override
    public LaboratorioDTO update(Long id, LaboratorioDTO dto) {
        LaboratorioModel lab = repo.findById(id).orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
        lab.setNombre(dto.getNombre());
        lab.setTipo(dto.getTipo());
        return new LaboratorioDTO(repo.save(lab).getId(), lab.getNombre(), lab.getTipo(), null, null);
    }
    @Override
    public void delete(Long id) { repo.deleteById(id); }
}
