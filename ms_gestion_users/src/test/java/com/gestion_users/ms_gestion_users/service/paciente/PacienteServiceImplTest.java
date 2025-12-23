package com.gestion_users.ms_gestion_users.service.paciente;

import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.ContactoRepository;
import com.gestion_users.ms_gestion_users.repository.DireccionRepository;
import com.gestion_users.ms_gestion_users.repository.PacienteRepository;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContactoRepository contactoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private PacienteModel pacienteModel;

    @BeforeEach
    void setUp() {
        pacienteModel = new PacienteModel();
        pacienteModel.setId(1L);
        pacienteModel.setPnombre("María");
        pacienteModel.setSnombre("José");
        pacienteModel.setPapellido("González");
        pacienteModel.setSapellido("López");
        pacienteModel.setRut("12345678-9");
        pacienteModel.setContactoId(10L);
        pacienteModel.setDirId(20L);
    }

    // Tests para findAll()
    @Test
    void testFindAll_Success() {
        // Arrange
        PacienteModel paciente2 = new PacienteModel();
        paciente2.setId(2L);
        paciente2.setPnombre("Pedro");
        paciente2.setPapellido("Martínez");

        List<PacienteModel> pacientes = Arrays.asList(pacienteModel, paciente2);
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        // Act
        List<PacienteModel> result = pacienteService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("María", result.get(0).getPnombre());
        assertEquals("Pedro", result.get(1).getPnombre());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<PacienteModel> result = pacienteService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(pacienteRepository, times(1)).findAll();
    }

    // Tests para findById()
    @Test
    void testFindById_Success() {
        // Arrange
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));

        // Act
        PacienteModel result = pacienteService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("María", result.getPnombre());
        assertEquals("González", result.getPapellido());
        verify(pacienteRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pacienteService.findById(999L);
        });

        assertEquals("Paciente no encontrado con ID: 999", exception.getMessage());
        verify(pacienteRepository, times(1)).findById(999L);
    }

    // Tests para create()
    @Test
    void testCreate_Success() {
        // Arrange
        PacienteModel newPaciente = new PacienteModel();
        newPaciente.setPnombre("Ana");
        newPaciente.setPapellido("Silva");
        newPaciente.setRut("98765432-1");

        PacienteModel savedPaciente = new PacienteModel();
        savedPaciente.setId(3L);
        savedPaciente.setPnombre("Ana");
        savedPaciente.setPapellido("Silva");
        savedPaciente.setRut("98765432-1");

        when(pacienteRepository.save(newPaciente)).thenReturn(savedPaciente);

        // Act
        PacienteModel result = pacienteService.create(newPaciente);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Ana", result.getPnombre());
        assertEquals("Silva", result.getPapellido());
        verify(pacienteRepository, times(1)).save(newPaciente);
    }

    // Tests para update()
    @Test
    void testUpdate_Success_AllFields() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        updateData.setPnombre("María Carmen");
        updateData.setSnombre("Teresa");
        updateData.setPapellido("González");
        updateData.setSapellido("Ramírez");
        updateData.setRut("11111111-1");
        updateData.setContactoId(15L);
        updateData.setDirId(25L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(any(PacienteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PacienteModel result = pacienteService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("María Carmen", result.getPnombre());
        assertEquals("Teresa", result.getSnombre());
        assertEquals("González", result.getPapellido());
        assertEquals("Ramírez", result.getSapellido());
        assertEquals("11111111-1", result.getRut());
        assertEquals(15L, result.getContactoId());
        assertEquals(25L, result.getDirId());
        verify(pacienteRepository, times(1)).findById(1L);
        verify(pacienteRepository, times(1)).save(any(PacienteModel.class));
    }

    @Test
    void testUpdate_PartialUpdate_OnlyPnombre() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        updateData.setPnombre("Carmen");

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(any(PacienteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PacienteModel result = pacienteService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("Carmen", result.getPnombre());
        assertEquals("José", result.getSnombre()); // No cambió
        assertEquals("González", result.getPapellido()); // No cambió
        assertEquals("López", result.getSapellido()); // No cambió
        assertEquals("12345678-9", result.getRut()); // No cambió
        verify(pacienteRepository, times(1)).save(any(PacienteModel.class));
    }

    @Test
    void testUpdate_PartialUpdate_OnlyRut() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        updateData.setRut("22222222-2");

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(any(PacienteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PacienteModel result = pacienteService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("22222222-2", result.getRut());
        assertEquals("María", result.getPnombre()); // No cambió
        assertEquals("González", result.getPapellido()); // No cambió
        verify(pacienteRepository, times(1)).save(any(PacienteModel.class));
    }

    @Test
    void testUpdate_PartialUpdate_OnlyContactoId() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        updateData.setContactoId(99L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(any(PacienteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PacienteModel result = pacienteService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals(99L, result.getContactoId());
        assertEquals(20L, result.getDirId()); // No cambió
        verify(pacienteRepository, times(1)).save(any(PacienteModel.class));
    }

    @Test
    void testUpdate_PartialUpdate_OnlyDirId() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        updateData.setDirId(88L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(any(PacienteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PacienteModel result = pacienteService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals(88L, result.getDirId());
        assertEquals(10L, result.getContactoId()); // No cambió
        verify(pacienteRepository, times(1)).save(any(PacienteModel.class));
    }

    @Test
    void testUpdate_PacienteNotFound() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        updateData.setPnombre("Nuevo Nombre");

        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pacienteService.update(999L, updateData);
        });

        assertEquals("Paciente no encontrado con ID: 999", exception.getMessage());
        verify(pacienteRepository, times(1)).findById(999L);
        verify(pacienteRepository, never()).save(any(PacienteModel.class));
    }

    @Test
    void testUpdate_NullFields_NoChanges() {
        // Arrange
        PacienteModel updateData = new PacienteModel();
        // No se establece ningún campo (todos null)

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(pacienteRepository.save(any(PacienteModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PacienteModel result = pacienteService.update(1L, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("María", result.getPnombre()); // No cambió
        assertEquals("José", result.getSnombre()); // No cambió
        assertEquals("González", result.getPapellido()); // No cambió
        assertEquals("López", result.getSapellido()); // No cambió
        assertEquals("12345678-9", result.getRut()); // No cambió
        verify(pacienteRepository, times(1)).save(any(PacienteModel.class));
    }

    // Tests para delete() - Cascada completa
    @Test
    void testDelete_Success_WithUsuarioContactoAndDireccion() {
        // Arrange
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(5L);
        usuario.setUsername("maria.gonzalez@example.com");
        usuario.setPacienteId(1L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(usuarioRepository.findByPacienteId(1L)).thenReturn(Optional.of(usuario));

        // Act
        pacienteService.delete(1L);

        // Assert
        verify(pacienteRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findByPacienteId(1L);
        verify(usuarioRepository, times(1)).delete(usuario);
        verify(pacienteRepository, times(1)).deleteById(1L);
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, times(1)).deleteById(20L);
    }

    @Test
    void testDelete_Success_WithoutUsuario() {
        // Arrange
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(usuarioRepository.findByPacienteId(1L)).thenReturn(Optional.empty());

        // Act
        pacienteService.delete(1L);

        // Assert
        verify(pacienteRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findByPacienteId(1L);
        verify(usuarioRepository, never()).delete(any(UsuarioModel.class));
        verify(pacienteRepository, times(1)).deleteById(1L);
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, times(1)).deleteById(20L);
    }

    @Test
    void testDelete_Success_WithoutContacto() {
        // Arrange
        pacienteModel.setContactoId(null);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(usuarioRepository.findByPacienteId(1L)).thenReturn(Optional.empty());

        // Act
        pacienteService.delete(1L);

        // Assert
        verify(pacienteRepository, times(1)).deleteById(1L);
        verify(contactoRepository, never()).deleteById(any());
        verify(direccionRepository, times(1)).deleteById(20L);
    }

    @Test
    void testDelete_Success_WithoutDireccion() {
        // Arrange
        pacienteModel.setDirId(null);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(usuarioRepository.findByPacienteId(1L)).thenReturn(Optional.empty());

        // Act
        pacienteService.delete(1L);

        // Assert
        verify(pacienteRepository, times(1)).deleteById(1L);
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, never()).deleteById(any());
    }

    @Test
    void testDelete_Success_OnlyPaciente() {
        // Arrange
        pacienteModel.setContactoId(null);
        pacienteModel.setDirId(null);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(usuarioRepository.findByPacienteId(1L)).thenReturn(Optional.empty());

        // Act
        pacienteService.delete(1L);

        // Assert
        verify(pacienteRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findByPacienteId(1L);
        verify(usuarioRepository, never()).delete(any(UsuarioModel.class));
        verify(pacienteRepository, times(1)).deleteById(1L);
        verify(contactoRepository, never()).deleteById(any());
        verify(direccionRepository, never()).deleteById(any());
    }

    @Test
    void testDelete_PacienteNotFound() {
        // Arrange
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pacienteService.delete(999L);
        });

        assertEquals("Paciente no encontrado con ID: 999", exception.getMessage());
        verify(pacienteRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).findByPacienteId(any());
        verify(pacienteRepository, never()).deleteById(any());
    }

    @Test
    void testDelete_WithUsuario_VerifyDeletionOrder() {
        // Arrange
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(5L);
        usuario.setUsername("paciente@test.com");
        usuario.setPacienteId(1L);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteModel));
        when(usuarioRepository.findByPacienteId(1L)).thenReturn(Optional.of(usuario));

        // Act
        pacienteService.delete(1L);

        // Assert - Verificar el orden de eliminación: usuario -> paciente -> contacto -> dirección
        var inOrder = inOrder(usuarioRepository, pacienteRepository, contactoRepository, direccionRepository);
        inOrder.verify(usuarioRepository).delete(usuario);
        inOrder.verify(pacienteRepository).deleteById(1L);
        inOrder.verify(contactoRepository).deleteById(10L);
        inOrder.verify(direccionRepository).deleteById(20L);
    }
}
