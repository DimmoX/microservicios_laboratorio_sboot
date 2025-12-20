package com.gestion_labs.ms_gestion_labs.service.agenda;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;
import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import com.gestion_labs.ms_gestion_labs.repository.AgendaExamenRepository;
import com.gestion_labs.ms_gestion_labs.repository.ExamenRepository;
import com.gestion_labs.ms_gestion_labs.repository.LabExamRepository;
import com.gestion_labs.ms_gestion_labs.repository.LaboratorioRepository;

@Service
public class AgendaServiceImpl implements AgendaService {

    private final AgendaExamenRepository repo;
    
    @Autowired
    private ExamenRepository examenRepository;
    
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    
    @Autowired
    private LabExamRepository labExamRepository;
    
    public AgendaServiceImpl(AgendaExamenRepository repo) { this.repo = repo; }

    @Override 
    public List<AgendaExamenDTO> findAll() { 
        return repo.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override 
    public List<AgendaExamenDTO> findByPaciente(Long pacienteId) {
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
        
        return repo.findByPacienteId(pacienteId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override 
    public AgendaExamenDTO findById(Long id) {
        AgendaExamenModel model = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Agenda no encontrada: " + id));
        return convertToDTO(model);
    }

    @Override 
    public AgendaExamenDTO create(AgendaExamenDTO dto) {
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
                if (dto.getPacienteId() != null && !userId.equals(dto.getPacienteId().toString())) {
                    throw new AccessDeniedException(
                        "No puedes crear agendas para otros pacientes"
                    );
                }
                
                // Si no viene pacienteId, asignarlo automáticamente
                if (dto.getPacienteId() == null) {
                    dto.setPacienteId(Long.parseLong(userId));
                }
            }
        }

        // Validar que existan el examen y laboratorio
        if (!examenRepository.existsById(dto.getExamenId())) {
            throw new RuntimeException("Examen no encontrado: " + dto.getExamenId());
        }
        
        if (!laboratorioRepository.existsById(dto.getLabId())) {
            throw new RuntimeException("Laboratorio no encontrado: " + dto.getLabId());
        }
        
        AgendaExamenModel model = new AgendaExamenModel();
        model.setPacienteId(dto.getPacienteId());
        model.setLabId(dto.getLabId());
        model.setExamenId(dto.getExamenId());
        model.setEmpleadoId(dto.getEmpleadoId());
        model.setFechaHora(dto.getFechaHora());
        model.setEstado("PROGRAMADA"); // Estado inicial
        
        AgendaExamenModel saved = repo.save(model);
        return convertToDTO(saved);
    }

    @Override 
    public AgendaExamenDTO update(Long id, AgendaExamenDTO dto) {
        AgendaExamenModel e = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Agenda no encontrada: " + id));
            
        // Actualización parcial: solo actualiza campos no nulos
        if (dto.getPacienteId() != null) e.setPacienteId(dto.getPacienteId());
        if (dto.getLabId() != null) e.setLabId(dto.getLabId());
        if (dto.getExamenId() != null) e.setExamenId(dto.getExamenId());
        if (dto.getEmpleadoId() != null) e.setEmpleadoId(dto.getEmpleadoId());
        if (dto.getFechaHora() != null) e.setFechaHora(dto.getFechaHora());
        if (dto.getEstado() != null) e.setEstado(dto.getEstado());
        
        AgendaExamenModel saved = repo.save(e);
        return convertToDTO(saved);
    }
    
    @Override
    public AgendaExamenDTO cancelar(Long id) {
        AgendaExamenModel agenda = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Agenda no encontrada: " + id));
            
        if ("ATENDIDA".equals(agenda.getEstado())) {
            throw new RuntimeException("No se puede cancelar una agenda ya atendida");
        }
        
        if ("CANCELADA".equals(agenda.getEstado())) {
            throw new RuntimeException("La agenda ya está cancelada");
        }
        
        agenda.setEstado("CANCELADA");
        AgendaExamenModel saved = repo.save(agenda);
        return convertToDTO(saved);
    }

    @Override public void delete(Long id) { repo.deleteById(id); }
    
    private AgendaExamenDTO convertToDTO(AgendaExamenModel model) {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        dto.setId(model.getId());
        dto.setPacienteId(model.getPacienteId());
        dto.setLabId(model.getLabId());
        dto.setExamenId(model.getExamenId());
        dto.setEmpleadoId(model.getEmpleadoId());
        dto.setFechaHora(model.getFechaHora());
        dto.setEstado(model.getEstado());
        dto.setCreadoEn(model.getCreadoEn());
        
        // Enriquecer con nombres
        examenRepository.findById(model.getExamenId()).ifPresent(examen -> {
            dto.setExamenNombre(examen.getNombre());
        });
        
        laboratorioRepository.findById(model.getLabId()).ifPresent(lab -> {
            dto.setLaboratorioNombre(lab.getNombre());
        });
        
        // Obtener precio del LAB_EXAM
        labExamRepository.findById_IdLaboratorioAndId_IdExamen(model.getLabId(), model.getExamenId())
            .ifPresent(labExam -> {
                dto.setPrecio(labExam.getPrecio());
            });
        
        return dto;
    }
}