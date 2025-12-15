package com.gestion_resultados.ms_gestion_resultados.service.resultados;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import com.gestion_resultados.ms_gestion_resultados.repository.ResultadoExamenRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ResultadoServiceImpl implements ResultadoService {

    private final ResultadoExamenRepository repo;
    public ResultadoServiceImpl(ResultadoExamenRepository repo) { this.repo = repo; }

    @Override
    public List<ResultadoExamenModel> findAll() {
        // Solo LAB_EMPLOYEE y ADMIN pueden ver todos los resultados
        // PATIENT no debería llegar aquí por @PreAuthorize, pero validamos por seguridad
        return repo.findAll();
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
            // Si es ROLE_LAB_EMPLOYEE o ROLE_ADMIN, permite acceso a cualquier paciente
        }

        return repo.findByPacienteId(pacienteId);
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
            // Si cambia a EMITIDO y no tiene fechaResultado, establecerla
            if ("EMITIDO".equalsIgnoreCase(r.getEstado()) && e.getFechaResultado() == null) {
                e.setFechaResultado(OffsetDateTime.now());
            }
        }
        return repo.save(e);
    }

    @Override public void delete(Long id) { repo.deleteById(id); }
}
