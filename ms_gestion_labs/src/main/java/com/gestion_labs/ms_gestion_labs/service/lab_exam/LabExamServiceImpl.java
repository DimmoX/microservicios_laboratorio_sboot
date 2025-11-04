package com.gestion_labs.ms_gestion_labs.service.lab_exam;

import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.repository.LabExamRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LabExamServiceImpl implements LabExamService {

    private final LabExamRepository repo;
    public LabExamServiceImpl(LabExamRepository repo) { this.repo = repo; }

    @Override public List<LabExamModel> listAll() { return repo.findAll(); }

    @Override public List<LabExamModel> listByLaboratorio(Long idLaboratorio) {
        return repo.findById_IdLaboratorio(idLaboratorio);
    }

    @Override public List<LabExamModel> listByExamen(Long idExamen) {
        return repo.findById_IdExamen(idExamen);
    }

    @Override public LabExamModel get(Long idLaboratorio, Long idExamen) {
        LabExamKey key = new LabExamKey(idLaboratorio, idExamen);
        return repo.findById(key).orElseThrow(() -> new RuntimeException("Precio no encontrado lab=" + idLaboratorio + " ex=" + idExamen));
    }

    @Override public LabExamModel upsert(LabExamModel model) {
        // Verificar si ya existe para hacer actualización parcial
        return repo.findById(model.getId()).map(existing -> {
            // Actualización parcial: solo actualiza campos no nulos
            if (model.getPrecio() != null) existing.setPrecio(model.getPrecio());
            if (model.getVigenteDesde() != null) existing.setVigenteDesde(model.getVigenteDesde());
            if (model.getVigenteHasta() != null) existing.setVigenteHasta(model.getVigenteHasta());
            return repo.save(existing);
        }).orElseGet(() -> {
            // Si no existe, crear nuevo
            return repo.save(model);
        });
    }

    @Override public void delete(Long idLaboratorio, Long idExamen) {
        repo.deleteById(new LabExamKey(idLaboratorio, idExamen));
    }
}