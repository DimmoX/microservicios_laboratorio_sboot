package com.gestion_labs.ms_gestion_labs.service.examen;

import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.repository.ExamenRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExamenServiceImpl implements ExamenService {

    private final ExamenRepository repo;
    public ExamenServiceImpl(ExamenRepository repo) { this.repo = repo; }

    @Override public List<ExamenModel> findAll() { return repo.findAll(); }

    @Override public ExamenModel findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Examen no encontrado: " + id));
    }

    @Override public ExamenModel create(ExamenModel ex) { return repo.save(ex); }

    @Override public ExamenModel update(Long id, ExamenModel ex) {
        ExamenModel e = findById(id);
        // Actualización parcial: solo actualiza campos no nulos
        if (ex.getCodigo() != null) e.setCodigo(ex.getCodigo());
        if (ex.getNombre() != null) e.setNombre(ex.getNombre());
        if (ex.getTipo() != null) e.setTipo(ex.getTipo());
        return repo.save(e);
    }

    @Override public void delete(Long id) { repo.deleteById(id); }
}