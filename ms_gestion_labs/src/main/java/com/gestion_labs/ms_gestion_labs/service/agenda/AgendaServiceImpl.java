package com.gestion_labs.ms_gestion_labs.service.agenda;

import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import com.gestion_labs.ms_gestion_labs.repository.AgendaExamenRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AgendaServiceImpl implements AgendaService {

    private final AgendaExamenRepository repo;
    public AgendaServiceImpl(AgendaExamenRepository repo) { this.repo = repo; }

    @Override public List<AgendaExamenModel> findAll() { return repo.findAll(); }

    @Override 
    public List<AgendaExamenModel> findByPaciente(Long pacienteId) {
        // Validación de ownership: PATIENT solo puede ver sus propias agendas
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            // Obtener el rol del usuario autenticado
            String rol = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
            
            // Si es PATIENT, verificar que el pacienteId coincida con su userId
            if ("PATIENT".equals(rol)) {
                String userId = auth.getName(); // El "sub" del JWT (id del usuario)
                
                if (!userId.equals(pacienteId.toString())) {
                    throw new AccessDeniedException(
                        "No tienes permiso para ver agendas de otros pacientes"
                    );
                }
            }
            // Si es LAB_EMPLOYEE o ADMIN, permite acceso a cualquier paciente
        }
        
        return repo.findByPacienteId(pacienteId);
    }

    @Override public AgendaExamenModel findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Agenda no encontrada: " + id));
    }

    @Override 
    public AgendaExamenModel create(AgendaExamenModel a) {
        // Validación: PATIENT solo puede crear agendas para sí mismo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String rol = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
            
            if ("PATIENT".equals(rol)) {
                String userId = auth.getName();
                
                // Verificar que el pacienteId en el body coincida con el userId
                if (a.getPacienteId() != null && !userId.equals(a.getPacienteId().toString())) {
                    throw new AccessDeniedException(
                        "No puedes crear agendas para otros pacientes"
                    );
                }
                
                // Si no viene pacienteId, asignarlo automáticamente
                if (a.getPacienteId() == null) {
                    a.setPacienteId(Long.parseLong(userId));
                }
            }
        }
        
        return repo.save(a); 
    }

    @Override public AgendaExamenModel update(Long id, AgendaExamenModel a) {
        AgendaExamenModel e = findById(id);
        // Actualización parcial: solo actualiza campos no nulos
        if (a.getPacienteId() != null) e.setPacienteId(a.getPacienteId());
        if (a.getLabId() != null) e.setLabId(a.getLabId());
        if (a.getExamenId() != null) e.setExamenId(a.getExamenId());
        if (a.getEmpleadoId() != null) e.setEmpleadoId(a.getEmpleadoId());
        if (a.getFechaHora() != null) e.setFechaHora(a.getFechaHora());
        if (a.getEstado() != null) e.setEstado(a.getEstado());
        return repo.save(e);
    }

    @Override public void delete(Long id) { repo.deleteById(id); }
}