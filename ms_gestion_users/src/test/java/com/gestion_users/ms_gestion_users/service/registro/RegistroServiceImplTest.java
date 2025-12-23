package com.gestion_users.ms_gestion_users.service.registro;

import com.gestion_users.ms_gestion_users.dto.ContactoDTO;
import com.gestion_users.ms_gestion_users.dto.DireccionDTO;
import com.gestion_users.ms_gestion_users.dto.RegistroEmpleadoRequest;
import com.gestion_users.ms_gestion_users.dto.RegistroPacienteRequest;
import com.gestion_users.ms_gestion_users.dto.RegistroResponse;
import com.gestion_users.ms_gestion_users.model.ContactoModel;
import com.gestion_users.ms_gestion_users.model.DireccionModel;
import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.ContactoRepository;
import com.gestion_users.ms_gestion_users.repository.DireccionRepository;
import com.gestion_users.ms_gestion_users.repository.EmpleadoRepository;
import com.gestion_users.ms_gestion_users.repository.PacienteRepository;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroServiceImplTest {

    @Mock
    private ContactoRepository contactoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistroServiceImpl registroService;

    private ContactoDTO contactoDTO;
    private DireccionDTO direccionDTO;
    private ContactoModel contactoModel;
    private DireccionModel direccionModel;

    @BeforeEach
    void setUp() {
        // Setup ContactoDTO
        contactoDTO = new ContactoDTO();
        contactoDTO.setFono1("123456789");
        contactoDTO.setFono2("987654321");
        contactoDTO.setEmail("test@example.com");

        // Setup DireccionDTO
        direccionDTO = new DireccionDTO();
        direccionDTO.setCalle("Calle Principal");
        direccionDTO.setNumero(123);
        direccionDTO.setCiudad("Santiago");
        direccionDTO.setComuna("Providencia");
        direccionDTO.setRegion("Metropolitana");

        // Setup ContactoModel
        contactoModel = new ContactoModel();
        contactoModel.setId(1L);
        contactoModel.setFono1("123456789");
        contactoModel.setFono2("987654321");
        contactoModel.setEmail("test@example.com");

        // Setup DireccionModel
        direccionModel = new DireccionModel();
        direccionModel.setId(1L);
        direccionModel.setCalle("Calle Principal");
        direccionModel.setNumero(123);
        direccionModel.setCiudad("Santiago");
        direccionModel.setComuna("Providencia");
        direccionModel.setRegion("Metropolitana");
    }

    // ==================== Tests para registrarPaciente ====================

    @Test
    void testRegistrarPaciente_Success() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Juan");
        request.setSnombre("Carlos");
        request.setPapellido("Pérez");
        request.setSapellido("González");
        request.setRut("12345678-9");
        request.setPassword("password123");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        PacienteModel pacienteModel = new PacienteModel();
        pacienteModel.setId(1L);
        pacienteModel.setPnombre("Juan");
        pacienteModel.setSnombre("Carlos");
        pacienteModel.setPapellido("Pérez");
        pacienteModel.setSapellido("González");
        pacienteModel.setRut("12345678-9");
        pacienteModel.setContactoId(1L);
        pacienteModel.setDirId(1L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setUsername("test@example.com");
        usuarioModel.setPassword("encodedPassword");
        usuarioModel.setRole("PATIENT");
        usuarioModel.setPacienteId(1L);

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(pacienteRepository.save(any(PacienteModel.class))).thenReturn(pacienteModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarPaciente(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getPacienteId());
        assertEquals(1L, response.getUsuarioId());
        assertEquals("test@example.com", response.getUsername());
        assertNull(response.getEmpleadoId());

        verify(contactoRepository).findByEmail("test@example.com");
        verify(usuarioRepository).findByUsername("test@example.com");
        verify(contactoRepository).save(any(ContactoModel.class));
        verify(direccionRepository).save(any(DireccionModel.class));
        verify(pacienteRepository).save(any(PacienteModel.class));
        verify(usuarioRepository).save(any(UsuarioModel.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void testRegistrarPaciente_EmailYaRegistradoEnContacto() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setContacto(contactoDTO);

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.of(contactoModel));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> registroService.registrarPaciente(request));

        assertTrue(exception.getMessage().contains("El email ya está registrado"));
        verify(contactoRepository).findByEmail("test@example.com");
        verify(contactoRepository, never()).save(any());
    }

    @Test
    void testRegistrarPaciente_EmailYaRegistradoEnUsuario() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setContacto(contactoDTO);

        UsuarioModel existingUsuario = new UsuarioModel();
        existingUsuario.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUsuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> registroService.registrarPaciente(request));

        assertTrue(exception.getMessage().contains("Ya existe un usuario con este email"));
        verify(usuarioRepository).findByUsername("test@example.com");
        verify(contactoRepository, never()).save(any());
    }

    @Test
    void testRegistrarPaciente_ConDireccionVacia() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Juan");
        request.setPapellido("Pérez");
        request.setRut("12345678-9");
        request.setPassword("password123");
        request.setContacto(contactoDTO);

        DireccionDTO direccionVacia = new DireccionDTO();
        direccionVacia.setCalle("");
        direccionVacia.setNumero(null);
        direccionVacia.setCiudad("");
        direccionVacia.setComuna("");
        direccionVacia.setRegion("");
        request.setDireccion(direccionVacia);

        PacienteModel pacienteModel = new PacienteModel();
        pacienteModel.setId(1L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(pacienteRepository.save(any(PacienteModel.class))).thenReturn(pacienteModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarPaciente(request);

        // Assert
        assertNotNull(response);
        verify(direccionRepository).save(argThat(dir ->
            "No especificada".equals(dir.getCalle()) &&
            dir.getNumero() == 0 &&
            "No especificada".equals(dir.getCiudad()) &&
            "No especificada".equals(dir.getComuna()) &&
            "No especificada".equals(dir.getRegion())
        ));
    }

    @Test
    void testRegistrarPaciente_ConDireccionNull() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Juan");
        request.setPapellido("Pérez");
        request.setRut("12345678-9");
        request.setPassword("password123");
        request.setContacto(contactoDTO);

        DireccionDTO direccionNull = new DireccionDTO();
        direccionNull.setCalle(null);
        direccionNull.setNumero(null);
        direccionNull.setCiudad(null);
        direccionNull.setComuna(null);
        direccionNull.setRegion(null);
        request.setDireccion(direccionNull);

        PacienteModel pacienteModel = new PacienteModel();
        pacienteModel.setId(1L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(pacienteRepository.save(any(PacienteModel.class))).thenReturn(pacienteModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarPaciente(request);

        // Assert
        assertNotNull(response);
        verify(direccionRepository).save(argThat(dir ->
            "No especificada".equals(dir.getCalle()) &&
            dir.getNumero() == 0 &&
            "No especificada".equals(dir.getCiudad()) &&
            "No especificada".equals(dir.getComuna()) &&
            "No especificada".equals(dir.getRegion())
        ));
    }

    @Test
    void testRegistrarPaciente_ErrorAlGuardarContacto() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Juan");
        request.setPapellido("Pérez");
        request.setRut("12345678-9");
        request.setPassword("password123");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> registroService.registrarPaciente(request));

        assertTrue(exception.getMessage().contains("Error al registrar paciente"));
        verify(direccionRepository, never()).save(any());
        verify(pacienteRepository, never()).save(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testRegistrarPaciente_ConNombresCompletos() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Juan");
        request.setSnombre("Carlos");
        request.setPapellido("Pérez");
        request.setSapellido("González");
        request.setRut("12345678-9");
        request.setPassword("password123");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        PacienteModel pacienteModel = new PacienteModel();
        pacienteModel.setId(1L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(pacienteRepository.save(any(PacienteModel.class))).thenReturn(pacienteModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarPaciente(request);

        // Assert
        assertNotNull(response);
        verify(pacienteRepository).save(argThat(pac ->
            "Juan".equals(pac.getPnombre()) &&
            "Carlos".equals(pac.getSnombre()) &&
            "Pérez".equals(pac.getPapellido()) &&
            "González".equals(pac.getSapellido()) &&
            "12345678-9".equals(pac.getRut())
        ));
    }

    // ==================== Tests para registrarEmpleado ====================

    @Test
    void testRegistrarEmpleado_Success() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("María");
        request.setSnombre("Isabel");
        request.setPapellido("López");
        request.setSapellido("Martínez");
        request.setRut("98765432-1");
        request.setCargo("Enfermera");
        request.setPassword("password456");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        EmpleadoModel empleadoModel = new EmpleadoModel();
        empleadoModel.setId(2L);
        empleadoModel.setPnombre("María");
        empleadoModel.setSnombre("Isabel");
        empleadoModel.setPapellido("López");
        empleadoModel.setSapellido("Martínez");
        empleadoModel.setRut("98765432-1");
        empleadoModel.setCargo("Enfermera");
        empleadoModel.setContactoId(1L);
        empleadoModel.setDirId(1L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);
        usuarioModel.setUsername("test@example.com");
        usuarioModel.setPassword("encodedPassword");
        usuarioModel.setRole("EMPLOYEE");
        usuarioModel.setEmpleadoId(2L);

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(empleadoModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarEmpleado(request);

        // Assert
        assertNotNull(response);
        assertEquals(2L, response.getEmpleadoId());
        assertEquals(2L, response.getUsuarioId());
        assertEquals("test@example.com", response.getUsername());
        assertNull(response.getPacienteId());

        verify(contactoRepository).findByEmail("test@example.com");
        verify(usuarioRepository).findByUsername("test@example.com");
        verify(contactoRepository).save(any(ContactoModel.class));
        verify(direccionRepository).save(any(DireccionModel.class));
        verify(empleadoRepository).save(any(EmpleadoModel.class));
        verify(usuarioRepository).save(any(UsuarioModel.class));
        verify(passwordEncoder).encode("password456");
    }

    @Test
    void testRegistrarEmpleado_EmailYaRegistradoEnContacto() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setContacto(contactoDTO);

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.of(contactoModel));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> registroService.registrarEmpleado(request));

        assertTrue(exception.getMessage().contains("El email ya está registrado"));
        verify(contactoRepository).findByEmail("test@example.com");
        verify(contactoRepository, never()).save(any());
    }

    @Test
    void testRegistrarEmpleado_EmailYaRegistradoEnUsuario() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setContacto(contactoDTO);

        UsuarioModel existingUsuario = new UsuarioModel();
        existingUsuario.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUsuario));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> registroService.registrarEmpleado(request));

        assertTrue(exception.getMessage().contains("Ya existe un usuario con este email"));
        verify(usuarioRepository).findByUsername("test@example.com");
        verify(contactoRepository, never()).save(any());
    }

    @Test
    void testRegistrarEmpleado_ConDireccionVacia() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("María");
        request.setPapellido("López");
        request.setRut("98765432-1");
        request.setCargo("Médico");
        request.setPassword("password456");
        request.setContacto(contactoDTO);

        DireccionDTO direccionVacia = new DireccionDTO();
        direccionVacia.setCalle("");
        direccionVacia.setNumero(null);
        direccionVacia.setCiudad("");
        direccionVacia.setComuna("");
        direccionVacia.setRegion("");
        request.setDireccion(direccionVacia);

        EmpleadoModel empleadoModel = new EmpleadoModel();
        empleadoModel.setId(2L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);
        usuarioModel.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(empleadoModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarEmpleado(request);

        // Assert
        assertNotNull(response);
        verify(direccionRepository).save(argThat(dir ->
            "No especificada".equals(dir.getCalle()) &&
            dir.getNumero() == 0 &&
            "No especificada".equals(dir.getCiudad()) &&
            "No especificada".equals(dir.getComuna()) &&
            "No especificada".equals(dir.getRegion())
        ));
    }

    @Test
    void testRegistrarEmpleado_ConDireccionNull() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("María");
        request.setPapellido("López");
        request.setRut("98765432-1");
        request.setCargo("Médico");
        request.setPassword("password456");
        request.setContacto(contactoDTO);

        DireccionDTO direccionNull = new DireccionDTO();
        direccionNull.setCalle(null);
        direccionNull.setNumero(null);
        direccionNull.setCiudad(null);
        direccionNull.setComuna(null);
        direccionNull.setRegion(null);
        request.setDireccion(direccionNull);

        EmpleadoModel empleadoModel = new EmpleadoModel();
        empleadoModel.setId(2L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);
        usuarioModel.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(empleadoModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarEmpleado(request);

        // Assert
        assertNotNull(response);
        verify(direccionRepository).save(argThat(dir ->
            "No especificada".equals(dir.getCalle()) &&
            dir.getNumero() == 0 &&
            "No especificada".equals(dir.getCiudad()) &&
            "No especificada".equals(dir.getComuna()) &&
            "No especificada".equals(dir.getRegion())
        ));
    }

    @Test
    void testRegistrarEmpleado_ErrorAlGuardarContacto() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("María");
        request.setPapellido("López");
        request.setRut("98765432-1");
        request.setCargo("Médico");
        request.setPassword("password456");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> registroService.registrarEmpleado(request));

        assertTrue(exception.getMessage().contains("Error al registrar empleado"));
        verify(direccionRepository, never()).save(any());
        verify(empleadoRepository, never()).save(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void testRegistrarEmpleado_ConNombresCompletos() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("María");
        request.setSnombre("Isabel");
        request.setPapellido("López");
        request.setSapellido("Martínez");
        request.setRut("98765432-1");
        request.setCargo("Enfermera");
        request.setPassword("password456");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        EmpleadoModel empleadoModel = new EmpleadoModel();
        empleadoModel.setId(2L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);
        usuarioModel.setUsername("test@example.com");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(empleadoModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarEmpleado(request);

        // Assert
        assertNotNull(response);
        verify(empleadoRepository).save(argThat(emp ->
            "María".equals(emp.getPnombre()) &&
            "Isabel".equals(emp.getSnombre()) &&
            "López".equals(emp.getPapellido()) &&
            "Martínez".equals(emp.getSapellido()) &&
            "98765432-1".equals(emp.getRut()) &&
            "Enfermera".equals(emp.getCargo())
        ));
    }

    @Test
    void testRegistrarEmpleado_UsuarioConRolEmployee() {
        // Arrange
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("María");
        request.setPapellido("López");
        request.setRut("98765432-1");
        request.setCargo("Médico");
        request.setPassword("password456");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        EmpleadoModel empleadoModel = new EmpleadoModel();
        empleadoModel.setId(2L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(2L);
        usuarioModel.setUsername("test@example.com");
        usuarioModel.setRole("EMPLOYEE");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(empleadoModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarEmpleado(request);

        // Assert
        assertNotNull(response);
        verify(usuarioRepository).save(argThat(usr ->
            "EMPLOYEE".equals(usr.getRole()) &&
            usr.getEmpleadoId() != null &&
            usr.getPacienteId() == null
        ));
    }

    @Test
    void testRegistrarPaciente_UsuarioConRolPatient() {
        // Arrange
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Juan");
        request.setPapellido("Pérez");
        request.setRut("12345678-9");
        request.setPassword("password123");
        request.setContacto(contactoDTO);
        request.setDireccion(direccionDTO);

        PacienteModel pacienteModel = new PacienteModel();
        pacienteModel.setId(1L);

        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setId(1L);
        usuarioModel.setUsername("test@example.com");
        usuarioModel.setRole("PATIENT");

        when(contactoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contactoModel);
        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccionModel);
        when(pacienteRepository.save(any(PacienteModel.class))).thenReturn(pacienteModel);
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        RegistroResponse response = registroService.registrarPaciente(request);

        // Assert
        assertNotNull(response);
        verify(usuarioRepository).save(argThat(usr ->
            "PATIENT".equals(usr.getRole()) &&
            usr.getPacienteId() != null &&
            usr.getEmpleadoId() == null
        ));
    }
}
