package com.gestion_labs.ms_gestion_labs.service.agenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gestion_labs.ms_gestion_labs.client.UsersServiceClient;
import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;
import com.gestion_labs.ms_gestion_labs.dto.PacienteDTO;
import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.model.LaboratorioModel;
import com.gestion_labs.ms_gestion_labs.repository.AgendaExamenRepository;
import com.gestion_labs.ms_gestion_labs.repository.ExamenRepository;
import com.gestion_labs.ms_gestion_labs.repository.LabExamRepository;
import com.gestion_labs.ms_gestion_labs.repository.LaboratorioRepository;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {

    @Mock
    private AgendaExamenRepository agendaRepository;

    @Mock
    private ExamenRepository examenRepository;

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @Mock
    private LabExamRepository labExamRepository;

    @Mock
    private UsersServiceClient usersServiceClient;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private AgendaServiceImpl agendaService;
    private AgendaExamenModel agendaModel;
    private AgendaExamenDTO agendaDTO;

    @BeforeEach
    void setUp() {
        agendaService = new AgendaServiceImpl(
            agendaRepository, 
            examenRepository, 
            laboratorioRepository, 
            labExamRepository, 
            usersServiceClient
        );
        
        agendaModel = new AgendaExamenModel();
        agendaModel.setId(1L);
        agendaModel.setPacienteId(100L);
        agendaModel.setLabId(10L);
        agendaModel.setExamenId(50L);
        agendaModel.setEmpleadoId(5L);
        agendaModel.setFechaHora(LocalDateTime.of(2025, 12, 25, 10, 0));
        agendaModel.setEstado("PROGRAMADA");

        agendaDTO = new AgendaExamenDTO();
        agendaDTO.setPacienteId(100L);
        agendaDTO.setLabId(10L);
        agendaDTO.setExamenId(50L);
        agendaDTO.setEmpleadoId(5L);
        agendaDTO.setFechaHora(LocalDateTime.of(2025, 12, 25, 10, 0));
        
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindAll() {
        when(agendaRepository.findAll()).thenReturn(Arrays.asList(agendaModel));
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        List<AgendaExamenDTO> result = agendaService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getPacienteId());
        verify(agendaRepository, times(1)).findAll();
    }

    @Test
    void testFindAllConDatosEnriquecidos() {
        PacienteDTO paciente = new PacienteDTO();
        paciente.setPnombre("Juan");
        paciente.setPapellido("Perez");
        
        ExamenModel examen = new ExamenModel();
        examen.setNombre("Hemograma");
        
        LaboratorioModel lab = new LaboratorioModel();
        lab.setNombre("Lab Central");
        
        LabExamModel labExam = new LabExamModel();
        labExam.setPrecio(new BigDecimal("15000.00"));
        
        when(agendaRepository.findAll()).thenReturn(Arrays.asList(agendaModel));
        when(usersServiceClient.getPacienteById(100L)).thenReturn(paciente);
        when(examenRepository.findById(50L)).thenReturn(Optional.of(examen));
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.of(lab));
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.of(labExam));

        List<AgendaExamenDTO> result = agendaService.findAll();

        assertEquals("Juan Perez", result.get(0).getPacienteNombre());
        assertEquals("Hemograma", result.get(0).getExamenNombre());
        assertEquals("Lab Central", result.get(0).getLaboratorioNombre());
        assertNotNull(result.get(0).getPrecio());
    }

    @Test
    void testFindByPacienteAsEmployee() {
        when(agendaRepository.findByPacienteId(100L)).thenReturn(Arrays.asList(agendaModel));
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        List<AgendaExamenDTO> result = agendaService.findByPaciente(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(agendaRepository).findByPacienteId(100L);
    }

    @Test
    void testFindByPacienteAsPatientMismoPaciente() {
        when(agendaRepository.findByPacienteId(100L)).thenReturn(Arrays.asList(agendaModel));
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        List<AgendaExamenDTO> result = agendaService.findByPaciente(100L);

        assertEquals(1, result.size());
    }

    @Test
    void testFindByPacienteAsPatientOtroPaciente() {
        when(agendaRepository.findByPacienteId(999L)).thenReturn(Collections.emptyList());

        List<AgendaExamenDTO> result = agendaService.findByPaciente(999L);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.findById(1L);

        assertNotNull(result);
        assertEquals(100L, result.getPacienteId());
        verify(agendaRepository).findById(1L);
    }

    @Test
    void testFindByIdNoEncontrada() {
        when(agendaRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Agenda no encontrada"));
    }

    @Test
    void testCreate() {
        when(examenRepository.existsById(50L)).thenReturn(true);
        when(laboratorioRepository.existsById(10L)).thenReturn(true);
        when(agendaRepository.save(any(AgendaExamenModel.class))).thenReturn(agendaModel);
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.create(agendaDTO);

        assertNotNull(result);
        assertEquals(100L, result.getPacienteId());
        verify(agendaRepository).save(any(AgendaExamenModel.class));
    }

    @Test
    void testCreateSinPacienteId() {
        agendaDTO.setPacienteId(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.create(agendaDTO);
        });

        assertTrue(exception.getMessage().contains("ID del paciente es requerido"));
    }

    @Test
    void testCreateExamenNoExiste() {
        when(examenRepository.existsById(50L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.create(agendaDTO);
        });

        assertEquals("Examen no encontrado: 50", exception.getMessage());
    }

    @Test
    void testCreateLaboratorioNoExiste() {
        when(examenRepository.existsById(50L)).thenReturn(true);
        when(laboratorioRepository.existsById(10L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.create(agendaDTO);
        });

        assertEquals("Laboratorio no encontrado: 10", exception.getMessage());
    }

    @Test
    void testUpdate() {
        AgendaExamenDTO updateDTO = new AgendaExamenDTO();
        updateDTO.setEstado("ATENDIDA");
        
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        when(agendaRepository.save(any(AgendaExamenModel.class))).thenReturn(agendaModel);
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.update(1L, updateDTO);

        assertNotNull(result);
        verify(agendaRepository).save(any(AgendaExamenModel.class));
    }

    @Test
    void testUpdateNoEncontrada() {
        when(agendaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            agendaService.update(999L, agendaDTO);
        });
    }

    @Test
    void testUpdateFechaHora() {
        LocalDateTime nuevaFecha = LocalDateTime.of(2025, 12, 30, 15, 0);
        
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        when(agendaRepository.save(any(AgendaExamenModel.class))).thenReturn(agendaModel);
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.updateFechaHora(1L, nuevaFecha);

        assertNotNull(result);
        verify(agendaRepository).save(any(AgendaExamenModel.class));
    }

    @Test
    void testUpdateFechaHoraAgendaAtendida() {
        agendaModel.setEstado("ATENDIDA");
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        LocalDateTime nuevaFecha = LocalDateTime.now();

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> agendaService.updateFechaHora(1L, nuevaFecha));

        assertTrue(exception.getMessage().contains("No se puede actualizar una agenda ya atendida"));
    }

    @Test
    void testUpdateFechaHoraAgendaCancelada() {
        agendaModel.setEstado("CANCELADA");
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        LocalDateTime nuevaFecha = LocalDateTime.now();

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> agendaService.updateFechaHora(1L, nuevaFecha));

        assertTrue(exception.getMessage().contains("No se puede actualizar una agenda cancelada"));
    }

    @Test
    void testUpdateFechaHoraNula() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.updateFechaHora(1L, null);
        });

        assertTrue(exception.getMessage().contains("La nueva fecha/hora es requerida"));
    }

    @Test
    void testCancelar() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        when(agendaRepository.save(any(AgendaExamenModel.class))).thenReturn(agendaModel);
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.cancelar(1L);

        assertNotNull(result);
        verify(agendaRepository).save(any(AgendaExamenModel.class));
    }

    @Test
    void testCancelarAgendaAtendida() {
        agendaModel.setEstado("ATENDIDA");
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.cancelar(1L);
        });

        assertTrue(exception.getMessage().contains("No se puede cancelar una agenda ya atendida"));
    }

    @Test
    void testCancelarAgendaYaCancelada() {
        agendaModel.setEstado("CANCELADA");
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendaService.cancelar(1L);
        });

        assertTrue(exception.getMessage().contains("La agenda ya está cancelada"));
    }

    @Test
    void testDelete() {
        doNothing().when(agendaRepository).deleteById(1L);

        agendaService.delete(1L);

        verify(agendaRepository).deleteById(1L);
    }

    @Test
    void testConvertToDTOConUsersServiceException() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        when(usersServiceClient.getPacienteById(100L)).thenThrow(new RuntimeException("Service unavailable"));
        when(examenRepository.findById(50L)).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(10L)).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(10L, 50L)).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.findById(1L);

        assertNotNull(result);
        assertNull(result.getPacienteNombre());
    }

    @Test
    void testFindAllVacia() {
        when(agendaRepository.findAll()).thenReturn(Collections.emptyList());

        List<AgendaExamenDTO> result = agendaService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateTodosCampos() {
        AgendaExamenDTO updateDTO = new AgendaExamenDTO();
        updateDTO.setPacienteId(200L);
        updateDTO.setLabId(20L);
        updateDTO.setExamenId(60L);
        updateDTO.setEmpleadoId(10L);
        updateDTO.setFechaHora(LocalDateTime.of(2025, 12, 28, 14, 30));
        updateDTO.setEstado("ATENDIDA");
        
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agendaModel));
        when(agendaRepository.save(any(AgendaExamenModel.class))).thenReturn(agendaModel);
        when(examenRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(laboratorioRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(labExamRepository.findById_IdLaboratorioAndId_IdExamen(anyLong(), anyLong())).thenReturn(Optional.empty());

        AgendaExamenDTO result = agendaService.update(1L, updateDTO);

        assertNotNull(result);
        verify(agendaRepository).save(any(AgendaExamenModel.class));
    }
}
