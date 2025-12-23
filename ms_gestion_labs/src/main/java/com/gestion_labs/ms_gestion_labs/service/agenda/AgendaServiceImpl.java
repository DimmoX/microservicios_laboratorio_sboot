package com.gestion_labs.ms_gestion_labs.service.agenda;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gestion_labs.ms_gestion_labs.client.UsersServiceClient;
import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;
import com.gestion_labs.ms_gestion_labs.dto.PacienteDTO;
import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import com.gestion_labs.ms_gestion_labs.repository.AgendaExamenRepository;
import com.gestion_labs.ms_gestion_labs.repository.ExamenRepository;
import com.gestion_labs.ms_gestion_labs.repository.LabExamRepository;
import com.gestion_labs.ms_gestion_labs.repository.LaboratorioRepository;

@Service
public class AgendaServiceImpl implements AgendaService {

    private static final Logger logger = LoggerFactory.getLogger(AgendaServiceImpl.class);
    private static final String AGENDA_NOT_FOUND_PREFIX = "Agenda no encontrada: ";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String ESTADO_ATENDIDA = "ATENDIDA";
    
    private final AgendaExamenRepository repo;
    private final ExamenRepository examenRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final LabExamRepository labExamRepository;
    private final UsersServiceClient usersServiceClient;

    public AgendaServiceImpl(AgendaExamenRepository repo, 
                              ExamenRepository examenRepository,
                              LaboratorioRepository laboratorioRepository,
                              LabExamRepository labExamRepository,
                              UsersServiceClient usersServiceClient) { 
        this.repo = repo;
        this.examenRepository = examenRepository;
        this.laboratorioRepository = laboratorioRepository;
        this.labExamRepository = labExamRepository;
        this.usersServiceClient = usersServiceClient;
    }

    @Override 
    public List<AgendaExamenDTO> findAll() { 
        return repo.findAll().stream()
            .map(this::convertToDTO)
            .toList();
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
            // Si es EMPLOYEE o ADMIN, permite acceso a cualquier paciente
        }
        
        return repo.findByPacienteId(pacienteId).stream()
            .map(this::convertToDTO)
            .toList();
    }

    @Override 
    public AgendaExamenDTO findById(Long id) {
        AgendaExamenModel model = repo.findById(id)
            .orElseThrow(() -> new RuntimeException(AGENDA_NOT_FOUND_PREFIX + id));
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
            
            // Solo aplicar restricción si el usuario es PATIENT
            if ("PATIENT".equals(rol)) {
                if (dto.getPacienteId() == null) {
                    throw new RuntimeException("El ID del paciente es requerido");
                }
            }
        }

        // Validar que el pacienteId esté presente
        if (dto.getPacienteId() == null) {
            throw new RuntimeException("El ID del paciente es requerido");
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
            .orElseThrow(() -> new RuntimeException(AGENDA_NOT_FOUND_PREFIX + id));
            
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
    public AgendaExamenDTO updateFechaHora(Long id, LocalDateTime nuevaFechaHora) {
        AgendaExamenModel agenda = repo.findById(id)
            .orElseThrow(() -> new RuntimeException(AGENDA_NOT_FOUND_PREFIX + id));
            
        if (ESTADO_ATENDIDA.equals(agenda.getEstado())) {
            throw new RuntimeException("No se puede actualizar una agenda ya atendida");
        }
        
        if (ESTADO_CANCELADA.equals(agenda.getEstado())) {
            throw new RuntimeException("No se puede actualizar una agenda cancelada");
        }
        
        if (nuevaFechaHora == null) {
            throw new RuntimeException("La nueva fecha/hora es requerida");
        }
        
        agenda.setFechaHora(nuevaFechaHora);
        AgendaExamenModel saved = repo.save(agenda);
        logger.info("Fecha/hora actualizada para agenda ID: {} a {}", id, nuevaFechaHora);
        return convertToDTO(saved);
    }

    @Override
    public AgendaExamenDTO cancelar(Long id) {
        AgendaExamenModel agenda = repo.findById(id)
            .orElseThrow(() -> new RuntimeException(AGENDA_NOT_FOUND_PREFIX + id));
            
        if (ESTADO_ATENDIDA.equals(agenda.getEstado())) {
            throw new RuntimeException("No se puede cancelar una agenda ya atendida");
        }
        
        if ("CANCELADA".equals(agenda.getEstado())) {
            throw new RuntimeException("La agenda ya está cancelada");
        }
        
        agenda.setEstado("CANCELADA");
        AgendaExamenModel saved = repo.save(agenda);
        logger.info("Agenda cancelada (soft delete) ID: {}", id);
        return convertToDTO(saved);
    }

    @Override 
    public void delete(Long id) { 
        logger.info("DELETE: /agendas/{} -> Eliminar físicamente agenda", id);
        repo.deleteById(id); 
    }
    
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
        
        // Obtener nombre del paciente desde users-service
        try {
            PacienteDTO paciente = usersServiceClient.getPacienteById(model.getPacienteId());
            if (paciente != null) {
                dto.setPacienteNombre(paciente.getNombreCompleto());
            }
        } catch (Exception e) {
            logger.warn("No se pudo obtener nombre del paciente ID {}: {}", model.getPacienteId(), e.getMessage());
        }
        
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