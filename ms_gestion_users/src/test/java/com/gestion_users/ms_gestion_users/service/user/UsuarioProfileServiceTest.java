package com.gestion_users.ms_gestion_users.service.user;

import com.gestion_users.ms_gestion_users.config.JwtProperties;
import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioUpdateRequest;
import com.gestion_users.ms_gestion_users.model.*;
import com.gestion_users.ms_gestion_users.repository.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioProfileServiceTest {

    @Mock
    private PacienteRepository pacienteRepo;

    @Mock
    private EmpleadoRepository empleadoRepo;

    @Mock
    private ContactoRepository contactoRepo;

    @Mock
    private DireccionRepository direccionRepo;

    private JwtProperties jwtProperties;

    private UsuarioProfileService service;

    private UsuarioModel testUser;
    private PacienteModel testPaciente;
    private EmpleadoModel testEmpleado;
    private ContactoModel testContacto;
    private DireccionModel testDireccion;

    @BeforeEach
    void setUp() {
        // Setup JwtProperties manualmente
        jwtProperties = mock(JwtProperties.class);
        
        // Crear el servicio con los repositories mockeados y jwtProperties
        service = new UsuarioProfileService(pacienteRepo, empleadoRepo, contactoRepo, direccionRepo);
        // Inyectar manualmente jwtProperties
        try {
            var field = UsuarioProfileService.class.getDeclaredField("jwtProperties");
            field.setAccessible(true);
            field.set(service, jwtProperties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Setup Usuario
        testUser = new UsuarioModel();
        testUser.setId(1L);
        testUser.setUsername("test@test.cl");
        testUser.setRole("PATIENT");
        testUser.setEstado("ACTIVO");
        testUser.setPacienteId(10L);

        // Setup Paciente
        testPaciente = new PacienteModel();
        testPaciente.setId(10L);
        testPaciente.setPnombre("Juan");
        testPaciente.setSnombre("Carlos");
        testPaciente.setPapellido("Pérez");
        testPaciente.setSapellido("González");
        testPaciente.setRut("12345678-9");
        testPaciente.setContactoId(100L);
        testPaciente.setDirId(200L);

        // Setup Empleado
        testEmpleado = new EmpleadoModel();
        testEmpleado.setId(20L);
        testEmpleado.setPnombre("María");
        testEmpleado.setSnombre("Teresa");
        testEmpleado.setPapellido("López");
        testEmpleado.setSapellido("Martínez");
        testEmpleado.setRut("98765432-1");
        testEmpleado.setCargo("Técnico");
        testEmpleado.setContactoId(101L);
        testEmpleado.setDirId(201L);

        // Setup Contacto
        testContacto = new ContactoModel();
        testContacto.setId(100L);
        testContacto.setFono1("+56912345678");
        testContacto.setEmail("test@test.cl");

        // Setup Direccion
        testDireccion = new DireccionModel();
        testDireccion.setId(200L);
        testDireccion.setCalle("Av. Principal");
        testDireccion.setNumero(123);
        testDireccion.setCiudad("Santiago");
    }

    @Test
    void testExtractUserIdFromToken_ValidToken() {
        // Arrange - Crear un JWT válido
        String secret = "mi-secreto-super-largo-para-jwt-con-minimo-256-bits-de-seguridad";
        when(jwtProperties.getSecret()).thenReturn(secret);
        
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String validToken = Jwts.builder()
            .claim("userId", 123)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
        
        // Act
        Long userId = service.extractUserIdFromToken(validToken);
        
        // Assert
        assertNotNull(userId);
        assertEquals(123L, userId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"invalid-token", ""})
    void testExtractUserIdFromToken_InvalidTokens(String token) {
        // Act
        Long userId = service.extractUserIdFromToken(token);
        
        // Assert
        assertNull(userId);
    }

    @Test
    void testExtractUserIdFromToken_TokenWithoutUserId() {
        // Arrange - JWT sin claim "userId"
        String secret = "mi-secreto-super-largo-para-jwt-con-minimo-256-bits-de-seguridad";
        when(jwtProperties.getSecret()).thenReturn(secret);
        
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String tokenWithoutUserId = Jwts.builder()
            .claim("otherClaim", "value")
            .issuedAt(new Date())
            .signWith(key)
            .compact();
        
        // Act
        Long userId = service.extractUserIdFromToken(tokenWithoutUserId);
        
        // Assert
        assertNull(userId); // userId no existe en el token
    }

    @Test
    void testBuildProfile_WithPaciente() {
        // Arrange
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));
        when(contactoRepo.findById(100L)).thenReturn(Optional.of(testContacto));
        when(direccionRepo.findById(200L)).thenReturn(Optional.of(testDireccion));

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@test.cl", response.getUsername());
        assertEquals("PATIENT", response.getRol());
        assertTrue(response.getActivo());
        assertEquals("Juan Carlos Pérez González", response.getNombre());
        assertEquals("12345678-9", response.getRut());
        assertEquals("+56912345678", response.getTelefono());
        assertEquals("Av. Principal", response.getDireccion());
        assertEquals(10L, response.getPacienteId());
        assertEquals(100L, response.getContactoId());
        assertEquals(200L, response.getDirId());

        verify(pacienteRepo).findById(10L);
        verify(contactoRepo).findById(100L);
        verify(direccionRepo).findById(200L);
    }

    @Test
    void testBuildProfile_WithEmpleado() {
        // Arrange
        testUser.setPacienteId(null);
        testUser.setEmpleadoId(20L);
        when(empleadoRepo.findById(20L)).thenReturn(Optional.of(testEmpleado));
        when(contactoRepo.findById(101L)).thenReturn(Optional.of(testContacto));
        when(direccionRepo.findById(201L)).thenReturn(Optional.of(testDireccion));

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNotNull(response);
        assertEquals("María Teresa López Martínez", response.getNombre());
        assertEquals("98765432-1", response.getRut());
        assertEquals("Técnico", response.getCargo());
        assertEquals(20L, response.getEmpleadoId());

        verify(empleadoRepo).findById(20L);
    }

    @Test
    void testBuildProfile_PacienteNotFound_UsesFallbackName() {
        // Arrange
        when(pacienteRepo.findById(10L)).thenReturn(Optional.empty());

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNotNull(response);
        assertEquals("test", response.getNombre()); // Fallback from username
    }

    @Test
    void testBuildProfile_WithoutPacienteOrEmpleado() {
        // Arrange
        testUser.setPacienteId(null);
        testUser.setEmpleadoId(null);

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNotNull(response);
        assertEquals("test", response.getNombre()); // Fallback from username
    }

    @Test
    void testBuildProfile_EstadoInactivo() {
        // Arrange
        testUser.setEstado("INACTIVO");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertFalse(response.getActivo());
    }

    @Test
    void testBuildProfile_WithPartialName() {
        // Arrange - Paciente solo con primer nombre y apellido
        testPaciente.setSnombre(null);
        testPaciente.setSapellido(null);
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertEquals("Juan Pérez", response.getNombre());
    }

    @Test
    void testApplyUpdates_UpdateActivo() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setActivo(false);

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("INACTIVO", testUser.getEstado());
    }

    @Test
    void testApplyUpdates_UpdateRol() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setRol("ADMIN");

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("ADMIN", testUser.getRole());
    }

    @Test
    void testApplyUpdates_UpdatePacienteNombre() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("Pedro Luis Ramírez Silva");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - Con 4 palabras: parts[0]=pnombre, parts[1]=snombre, parts[2]=sapellido, parts[length-1]=papellido
        assertEquals("Pedro", testPaciente.getPnombre());
        assertEquals("Luis", testPaciente.getSnombre());
        assertEquals("Silva", testPaciente.getPapellido()); // último elemento
        assertEquals("Ramírez", testPaciente.getSapellido()); // parts[2]
        verify(pacienteRepo).save(testPaciente);
    }

    @Test
    void testApplyUpdates_UpdatePacienteTelefono() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setTelefono("+56987654321");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));
        when(contactoRepo.findById(100L)).thenReturn(Optional.of(testContacto));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("+56987654321", testContacto.getFono1());
        verify(contactoRepo).save(testContacto);
    }

    @Test
    void testApplyUpdates_UpdatePacienteDireccion() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setDireccion("Nueva Calle 456");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));
        when(direccionRepo.findById(200L)).thenReturn(Optional.of(testDireccion));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("Nueva Calle 456", testDireccion.getCalle());
        verify(direccionRepo).save(testDireccion);
    }

    @Test
    void testApplyUpdates_UpdateEmpleadoNombre() {
        // Arrange
        testUser.setPacienteId(null);
        testUser.setEmpleadoId(20L);
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("Ana María Fernández Torres");
        when(empleadoRepo.findById(20L)).thenReturn(Optional.of(testEmpleado));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - Con 4 palabras: parts[0]=pnombre, parts[1]=snombre, parts[2]=sapellido, parts[length-1]=papellido
        assertEquals("Ana", testEmpleado.getPnombre());
        assertEquals("María", testEmpleado.getSnombre());
        assertEquals("Torres", testEmpleado.getPapellido()); // último elemento
        assertEquals("Fernández", testEmpleado.getSapellido()); // parts[2]
        verify(empleadoRepo).save(testEmpleado);
    }

    @Test
    void testApplyUpdates_UpdateEmpleadoTelefono() {
        // Arrange
        testUser.setPacienteId(null);
        testUser.setEmpleadoId(20L);
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setTelefono("+56911111111");
        when(empleadoRepo.findById(20L)).thenReturn(Optional.of(testEmpleado));
        when(contactoRepo.findById(101L)).thenReturn(Optional.of(testContacto));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("+56911111111", testContacto.getFono1());
        verify(contactoRepo).save(testContacto);
    }

    @Test
    void testApplyUpdates_UpdateEmpleadoDireccion() {
        // Arrange
        testUser.setPacienteId(null);
        testUser.setEmpleadoId(20L);
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setDireccion("Calle Nueva 789");
        when(empleadoRepo.findById(20L)).thenReturn(Optional.of(testEmpleado));
        when(direccionRepo.findById(201L)).thenReturn(Optional.of(testDireccion));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("Calle Nueva 789", testDireccion.getCalle());
        verify(direccionRepo).save(testDireccion);
    }

    @Test
    void testApplyUpdates_NullUpdates() {
        // Act
        service.applyUpdates(testUser, null);

        // Assert - No debe lanzar excepción
        verifyNoInteractions(pacienteRepo, empleadoRepo, contactoRepo, direccionRepo);
    }

    @Test
    void testApplyUpdates_PacienteNotFound() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("Nuevo Nombre");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.empty());

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - No debe lanzar excepción
        verify(pacienteRepo).findById(10L);
        verify(pacienteRepo, never()).save(any());
    }

    @Test
    void testApplyUpdates_UpdateContactoNotFound() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setTelefono("+56999999999");
        testPaciente.setContactoId(null); // Sin contacto
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - No debe intentar actualizar contacto
        verify(contactoRepo, never()).findById(anyLong());
    }

    @Test
    void testApplyUpdates_UpdateDireccionNotFound() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setDireccion("Nueva Dirección");
        testPaciente.setDirId(null); // Sin dirección
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - No debe intentar actualizar dirección
        verify(direccionRepo, never()).findById(anyLong());
    }

    @Test
    void testApplyUpdates_NombreWithOneWord() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("Pedro");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("Pedro", testPaciente.getPnombre());
        verify(pacienteRepo).save(testPaciente);
    }

    @Test
    void testApplyUpdates_NombreWithTwoWords() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("Pedro González");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("Pedro", testPaciente.getPnombre());
        assertEquals("González", testPaciente.getPapellido());
        verify(pacienteRepo).save(testPaciente);
    }

    @Test
    void testApplyUpdates_NombreWithThreeWords() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("Pedro Luis González");
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert
        assertEquals("Pedro", testPaciente.getPnombre());
        assertEquals("Luis", testPaciente.getSnombre());
        assertEquals("González", testPaciente.getPapellido());
        verify(pacienteRepo).save(testPaciente);
    }

    @Test
    void testApplyUpdates_BlankNombre() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setNombre("   "); // Blank
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - No debería actualizar si el nombre está en blanco
        verify(pacienteRepo).findById(10L);
    }

    @Test
    void testApplyUpdates_BlankRol() {
        // Arrange
        UsuarioUpdateRequest updates = new UsuarioUpdateRequest();
        updates.setRol("   "); // Blank

        // Act
        service.applyUpdates(testUser, updates);

        // Assert - No debería actualizar si el rol está en blanco
        assertEquals("PATIENT", testUser.getRole());
    }

    @ParameterizedTest
    @MethodSource("provideUsernamesForFallbackTest")
    void testFallbackNombreFromUsername(String username, String expectedNombre) {
        // Arrange
        testUser.setUsername(username);
        testUser.setPacienteId(null);
        testUser.setEmpleadoId(null);

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertEquals(expectedNombre, response.getNombre());
    }

    private static Stream<Arguments> provideUsernamesForFallbackTest() {
        return Stream.of(
            Arguments.of("juan.perez@test.cl", "juan.perez"),
            Arguments.of("juanperez", "juanperez"),
            Arguments.of(null, "Usuario"),
            Arguments.of("   ", "Usuario")
        );
    }

    @Test
    void testBuildProfile_ContactoNotFound() {
        // Arrange
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));
        when(contactoRepo.findById(100L)).thenReturn(Optional.empty());
        when(direccionRepo.findById(200L)).thenReturn(Optional.of(testDireccion));

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNull(response.getTelefono()); // No se asigna teléfono
        assertNotNull(response.getDireccion()); // Dirección sí se asigna
    }

    @Test
    void testBuildProfile_DireccionNotFound() {
        // Arrange
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));
        when(contactoRepo.findById(100L)).thenReturn(Optional.of(testContacto));
        when(direccionRepo.findById(200L)).thenReturn(Optional.empty());

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNotNull(response.getTelefono()); // Teléfono sí se asigna
        assertNull(response.getDireccion()); // Dirección no se asigna
    }

    @Test
    void testBuildProfile_PacienteWithNullContactoIdAndDirId() {
        // Arrange
        testPaciente.setContactoId(null);
        testPaciente.setDirId(null);
        when(pacienteRepo.findById(10L)).thenReturn(Optional.of(testPaciente));

        // Act
        UsuarioResponse response = service.buildProfile(testUser);

        // Assert
        assertNull(response.getTelefono());
        assertNull(response.getDireccion());
        verify(contactoRepo, never()).findById(anyLong());
        verify(direccionRepo, never()).findById(anyLong());
    }
}
