package com.gestion_users.ms_gestion_users.service.empleado;

import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.ContactoRepository;
import com.gestion_users.ms_gestion_users.repository.DireccionRepository;
import com.gestion_users.ms_gestion_users.repository.EmpleadoRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceImplTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContactoRepository contactoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    private EmpleadoModel empleado;

    @BeforeEach
    void setUp() {
        empleado = new EmpleadoModel();
        empleado.setId(1L);
        empleado.setPnombre("Juan");
        empleado.setSnombre("Carlos");
        empleado.setPapellido("Pérez");
        empleado.setSapellido("González");
        empleado.setRut("12345678-9");
        empleado.setCargo("Desarrollador");
        empleado.setContactoId(10L);
        empleado.setDirId(20L);
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        EmpleadoModel empleado2 = new EmpleadoModel();
        empleado2.setId(2L);
        empleado2.setPnombre("María");
        empleado2.setPapellido("López");
        
        List<EmpleadoModel> empleados = Arrays.asList(empleado, empleado2);
        when(empleadoRepository.findAll()).thenReturn(empleados);

        // Act
        List<EmpleadoModel> result = empleadoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getPnombre());
        assertEquals("María", result.get(1).getPnombre());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<EmpleadoModel> result = empleadoService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));

        // Act
        EmpleadoModel result = empleadoService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getPnombre());
        assertEquals("Pérez", result.getPapellido());
        verify(empleadoRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> empleadoService.findById(999L));
        
        assertTrue(exception.getMessage().contains("Empleado no encontrado con ID: 999"));
        verify(empleadoRepository, times(1)).findById(999L);
    }

    @Test
    void testCreate_Success() {
        // Arrange
        EmpleadoModel newEmpleado = new EmpleadoModel();
        newEmpleado.setPnombre("Pedro");
        newEmpleado.setPapellido("Ramírez");
        newEmpleado.setRut("98765432-1");
        newEmpleado.setCargo("Gerente");

        EmpleadoModel savedEmpleado = new EmpleadoModel();
        savedEmpleado.setId(3L);
        savedEmpleado.setPnombre("Pedro");
        savedEmpleado.setPapellido("Ramírez");
        savedEmpleado.setRut("98765432-1");
        savedEmpleado.setCargo("Gerente");

        when(empleadoRepository.save(newEmpleado)).thenReturn(savedEmpleado);

        // Act
        EmpleadoModel result = empleadoService.create(newEmpleado);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Pedro", result.getPnombre());
        assertEquals("Ramírez", result.getPapellido());
        verify(empleadoRepository, times(1)).save(newEmpleado);
    }

    @Test
    void testUpdate_AllFieldsNotNull() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setPnombre("Carlos");
        updates.setSnombre("Alberto");
        updates.setPapellido("Martínez");
        updates.setSapellido("Silva");
        updates.setRut("11111111-1");
        updates.setCargo("Manager");
        updates.setContactoId(15L);
        updates.setDirId(25L);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertNotNull(result);
        assertEquals("Carlos", result.getPnombre());
        assertEquals("Alberto", result.getSnombre());
        assertEquals("Martínez", result.getPapellido());
        assertEquals("Silva", result.getSapellido());
        assertEquals("11111111-1", result.getRut());
        assertEquals("Manager", result.getCargo());
        assertEquals(15L, result.getContactoId());
        assertEquals(25L, result.getDirId());
        verify(empleadoRepository, times(1)).findById(1L);
        verify(empleadoRepository, times(1)).save(any(EmpleadoModel.class));
    }

    @Test
    void testUpdate_PartialUpdate_OnlyPnombre() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setPnombre("NuevoNombre");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertNotNull(result);
        assertEquals("NuevoNombre", result.getPnombre());
        assertEquals("Carlos", result.getSnombre()); // No cambió
        assertEquals("Pérez", result.getPapellido()); // No cambió
        assertEquals("González", result.getSapellido()); // No cambió
        verify(empleadoRepository, times(1)).save(any(EmpleadoModel.class));
    }

    @Test
    void testUpdate_PartialUpdate_OnlySnombre() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setSnombre("NuevoSegundoNombre");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals("NuevoSegundoNombre", result.getSnombre());
        assertEquals("Juan", result.getPnombre()); // No cambió
    }

    @Test
    void testUpdate_PartialUpdate_OnlyPapellido() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setPapellido("NuevoApellido");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals("NuevoApellido", result.getPapellido());
        assertEquals("González", result.getSapellido()); // No cambió
    }

    @Test
    void testUpdate_PartialUpdate_OnlySapellido() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setSapellido("NuevoSegundoApellido");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals("NuevoSegundoApellido", result.getSapellido());
        assertEquals("Pérez", result.getPapellido()); // No cambió
    }

    @Test
    void testUpdate_PartialUpdate_OnlyRut() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setRut("99999999-9");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals("99999999-9", result.getRut());
        assertEquals("Desarrollador", result.getCargo()); // Otros campos no cambian
    }

    @Test
    void testUpdate_PartialUpdate_OnlyCargo() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setCargo("Director");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals("Director", result.getCargo());
        assertEquals("12345678-9", result.getRut()); // Otros campos no cambian
    }

    @Test
    void testUpdate_PartialUpdate_OnlyContactoId() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setContactoId(99L);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals(99L, result.getContactoId());
    }

    @Test
    void testUpdate_PartialUpdate_OnlyDirId() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setDirId(88L);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals(88L, result.getDirId());
    }

    @Test
    void testUpdate_EmptyUpdate() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpleadoModel result = empleadoService.update(1L, updates);

        // Assert
        assertEquals("Juan", result.getPnombre());
        assertEquals("Carlos", result.getSnombre());
        assertEquals("Pérez", result.getPapellido());
        assertEquals("González", result.getSapellido());
        verify(empleadoRepository, times(1)).save(any(EmpleadoModel.class));
    }

    @Test
    void testUpdate_NotFound() {
        // Arrange
        EmpleadoModel updates = new EmpleadoModel();
        updates.setPnombre("Nuevo");

        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> empleadoService.update(999L, updates));
        
        assertTrue(exception.getMessage().contains("Empleado no encontrado con ID: 999"));
        verify(empleadoRepository, never()).save(any(EmpleadoModel.class));
    }

    @Test
    void testDelete_WithUsuarioAndContactoAndDireccion() {
        // Arrange
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(5L);
        usuario.setUsername("juan.perez@example.com");
        usuario.setEmpleadoId(1L);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(usuarioRepository.findByEmpleadoId(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(usuario);
        doNothing().when(empleadoRepository).deleteById(1L);
        doNothing().when(contactoRepository).deleteById(10L);
        doNothing().when(direccionRepository).deleteById(20L);

        // Act
        empleadoService.delete(1L);

        // Assert
        verify(empleadoRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).findByEmpleadoId(1L);
        verify(usuarioRepository, times(1)).delete(usuario);
        verify(empleadoRepository, times(1)).deleteById(1L);
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, times(1)).deleteById(20L);
    }

    @Test
    void testDelete_WithoutUsuario() {
        // Arrange
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(usuarioRepository.findByEmpleadoId(1L)).thenReturn(Optional.empty());
        doNothing().when(empleadoRepository).deleteById(1L);
        doNothing().when(contactoRepository).deleteById(10L);
        doNothing().when(direccionRepository).deleteById(20L);

        // Act
        empleadoService.delete(1L);

        // Assert
        verify(usuarioRepository, times(1)).findByEmpleadoId(1L);
        verify(usuarioRepository, never()).delete(any());
        verify(empleadoRepository, times(1)).deleteById(1L);
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, times(1)).deleteById(20L);
    }

    @Test
    void testDelete_WithoutContacto() {
        // Arrange
        empleado.setContactoId(null);
        
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(usuarioRepository.findByEmpleadoId(1L)).thenReturn(Optional.empty());
        doNothing().when(empleadoRepository).deleteById(1L);
        doNothing().when(direccionRepository).deleteById(20L);

        // Act
        empleadoService.delete(1L);

        // Assert
        verify(contactoRepository, never()).deleteById(anyLong());
        verify(direccionRepository, times(1)).deleteById(20L);
    }

    @Test
    void testDelete_WithoutDireccion() {
        // Arrange
        empleado.setDirId(null);
        
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(usuarioRepository.findByEmpleadoId(1L)).thenReturn(Optional.empty());
        doNothing().when(empleadoRepository).deleteById(1L);
        doNothing().when(contactoRepository).deleteById(10L);

        // Act
        empleadoService.delete(1L);

        // Assert
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDelete_WithoutContactoAndDireccion() {
        // Arrange
        empleado.setContactoId(null);
        empleado.setDirId(null);
        
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(usuarioRepository.findByEmpleadoId(1L)).thenReturn(Optional.empty());
        doNothing().when(empleadoRepository).deleteById(1L);

        // Act
        empleadoService.delete(1L);

        // Assert
        verify(empleadoRepository, times(1)).deleteById(1L);
        verify(contactoRepository, never()).deleteById(anyLong());
        verify(direccionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDelete_EmpleadoNotFound() {
        // Arrange
        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> empleadoService.delete(999L));
        
        assertTrue(exception.getMessage().contains("Empleado no encontrado con ID: 999"));
        verify(empleadoRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).findByEmpleadoId(anyLong());
        verify(empleadoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDelete_AllEntitiesPresent() {
        // Arrange
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(5L);
        usuario.setUsername("empleado@test.com");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(usuarioRepository.findByEmpleadoId(1L)).thenReturn(Optional.of(usuario));

        // Act
        empleadoService.delete(1L);

        // Assert - verifica el orden de eliminación
        verify(usuarioRepository, times(1)).delete(usuario);
        verify(empleadoRepository, times(1)).deleteById(1L);
        verify(contactoRepository, times(1)).deleteById(10L);
        verify(direccionRepository, times(1)).deleteById(20L);
    }
}
