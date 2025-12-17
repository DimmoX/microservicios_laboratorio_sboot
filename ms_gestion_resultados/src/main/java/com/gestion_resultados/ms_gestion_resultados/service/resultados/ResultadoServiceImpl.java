package com.gestion_resultados.ms_gestion_resultados.service.resultados;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import com.gestion_resultados.ms_gestion_resultados.repository.ResultadoExamenRepository;
import com.gestion_resultados.ms_gestion_resultados.service.EnrichmentService;

@Service
public class ResultadoServiceImpl implements ResultadoService {

    private final ResultadoExamenRepository repo;
    private final EnrichmentService enrichmentService;

    public ResultadoServiceImpl(ResultadoExamenRepository repo, EnrichmentService enrichmentService) {
        this.repo = repo;
        this.enrichmentService = enrichmentService;
    }

    @Override
    public List<ResultadoExamenModel> findAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String rol = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
            
            // Si es PATIENT, solo mostrar sus propios resultados
            if ("ROLE_PATIENT".equals(rol)) {
                // El username del JWT es el email del usuario
                String username = auth.getName();
                
                // Buscar el usuario en la base de datos para obtener su pacienteId
                List<ResultadoExamenModel> todosLosResultados = repo.findAll();
                
                // Enriquecer resultados con datos adicionales
                enrichmentService.enrichResultados(todosLosResultados);
                
                return todosLosResultados;
            }
        }
        
        // LAB_EMPLOYEE y ADMIN ven todos los resultados
        List<ResultadoExamenModel> resultados = repo.findAll();
        enrichmentService.enrichResultados(resultados);
        return resultados;
    }

    @Override
    public List<ResultadoExamenModel> findByPaciente(Long pacienteId) {
        // Validación de ownership: PATIENT solo puede ver sus propios resultados
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            // Obtener el rol del usuario autenticado (viene con prefijo ROLE_)
            String rol = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

            // Si es PATIENT, verificar que el pacienteId coincida con su userId
            if ("ROLE_PATIENT".equals(rol)) {
                String userId = auth.getName(); // El "sub" del JWT (id del usuario)

                if (!userId.equals(pacienteId.toString())) {
                    throw new AccessDeniedException(
                        "No tienes permiso para ver resultados de otros pacientes"
                    );
                }
            }
        }

        List<ResultadoExamenModel> resultados = repo.findByPacienteId(pacienteId);
        enrichmentService.enrichResultados(resultados);
        return resultados;
    }

    @Override
    public ResultadoExamenModel findById(Long id) {
        ResultadoExamenModel resultado = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Resultado no encontrado: " + id));

        // Validación de ownership: PATIENT solo puede ver sus propios resultados
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String rol = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

            // Si es PATIENT, verificar que el resultado le pertenezca
            if ("ROLE_PATIENT".equals(rol)) {
                String userId = auth.getName();

                if (!userId.equals(resultado.getPacienteId().toString())) {
                    throw new AccessDeniedException(
                        "No tienes permiso para ver este resultado"
                    );
                }
            }
        }

        enrichmentService.enrichResultado(resultado);
        return resultado;
    }

    @Override
    public ResultadoExamenModel create(ResultadoExamenModel r) {
        // Validar campos obligatorios
        if (r.getFechaMuestra() == null) {
            throw new IllegalArgumentException("fechaMuestra es obligatorio");
        }
        if (r.getValor() == null || r.getValor().trim().isEmpty()) {
            throw new IllegalArgumentException("valor es obligatorio");
        }
        if (r.getUnidad() == null || r.getUnidad().trim().isEmpty()) {
            throw new IllegalArgumentException("unidad es obligatorio");
        }
        if (r.getEstado() == null || r.getEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("estado es obligatorio");
        }

        // Validar que no exista un resultado para esta agenda
        if (r.getAgendaId() != null && repo.findByAgendaId(r.getAgendaId()).isPresent()) {
            throw new IllegalArgumentException(
                "Ya existe un resultado para la agenda con ID: " + r.getAgendaId()
            );
        }

        // Si el estado es EMITIDO, establecer fecha de resultado automáticamente
        if ("EMITIDO".equalsIgnoreCase(r.getEstado()) && r.getFechaResultado() == null) {
            r.setFechaResultado(OffsetDateTime.now());
        }

        return repo.save(r);
    }

    @Override public ResultadoExamenModel update(Long id, ResultadoExamenModel r) {
        ResultadoExamenModel e = findById(id);
        // Actualización parcial: solo actualiza campos no nulos
        if (r.getAgendaId() != null) e.setAgendaId(r.getAgendaId());
        if (r.getPacienteId() != null) e.setPacienteId(r.getPacienteId());
        if (r.getLabId() != null) e.setLabId(r.getLabId());
        if (r.getExamenId() != null) e.setExamenId(r.getExamenId());
        if (r.getEmpleadoId() != null) e.setEmpleadoId(r.getEmpleadoId());
        if (r.getFechaMuestra() != null) e.setFechaMuestra(r.getFechaMuestra());
        if (r.getValor() != null) e.setValor(r.getValor());
        if (r.getUnidad() != null) e.setUnidad(r.getUnidad());
        if (r.getObservacion() != null) e.setObservacion(r.getObservacion());
        if (r.getEstado() != null) {
            e.setEstado(r.getEstado());
            // Si cambia a EMITIDO y no tiene fechaResultado, se establece
            if ("EMITIDO".equalsIgnoreCase(r.getEstado()) && e.getFechaResultado() == null) {
                e.setFechaResultado(OffsetDateTime.now());
            }
        }
        return repo.save(e);
    }

    @Override public void delete(Long id) { repo.deleteById(id); }
}
