package com.gestion_users.ms_gestion_users.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.PacienteRepository;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import com.gestion_users.ms_gestion_users.service.user.UserServiceImpl;
import com.gestion_users.ms_gestion_users.service.user.UsuarioProfileService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para UserService")
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioProfileService usuarioProfileService;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UsuarioModel usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setUsername("usuario@test.com");
        usuario.setPassword("hashedPassword");
        usuario.setRole("PATIENT");
        usuario.setEstado("ACTIVO");
    }

    @Test
    @DisplayName("Debe retornar todos los usuarios")
    void testFindAll() {
        // Arrange
        UsuarioModel usuario2 = new UsuarioModel();
        usuario2.setId(2L);
        usuario2.setUsername("admin@test.com");
        usuario2.setRole("ADMIN");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario, usuario2));

        // Act
        List<UsuarioModel> resultado = userService.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar un usuario por ID")
    void testFindById() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioModel resultado = userService.findById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("usuario@test.com", resultado.getUsername());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void testFindByIdNotFound() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    @DisplayName("Debe crear un nuevo usuario con contraseña encriptada")
    void testCreate() {
        // Arrange
        UsuarioModel nuevoUsuario = new UsuarioModel();
        nuevoUsuario.setUsername("nuevo@test.com");
        nuevoUsuario.setPassword("password123");
        nuevoUsuario.setRole("ADMIN");

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenAnswer(invocation -> {
            UsuarioModel u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });

        // Act
        UsuarioModel resultado = userService.create(nuevoUsuario);

        // Assert
        assertNotNull(resultado);
        assertEquals("encodedPassword", resultado.getPassword());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(any(UsuarioModel.class));
    }

    @Test
    @DisplayName("Debe cambiar la contraseña correctamente")
    void testChangePassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuario);

        // Act & Assert
        assertDoesNotThrow(() -> userService.changePassword(1L, request));
        verify(passwordEncoder, times(1)).matches("oldPassword", "hashedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(usuarioRepository, times(1)).save(any(UsuarioModel.class));
    }
}
