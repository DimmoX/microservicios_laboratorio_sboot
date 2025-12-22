package com.gestion_users.ms_gestion_users.service;

import java.util.Optional;

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

import com.gestion_users.ms_gestion_users.config.JwtProperties;
import com.gestion_users.ms_gestion_users.dto.AuthRequest;
import com.gestion_users.ms_gestion_users.dto.AuthResponse;
import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ChangePasswordResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import com.gestion_users.ms_gestion_users.service.auth.AuthServiceImpl;
import com.gestion_users.ms_gestion_users.service.email.EmailService;
import com.gestion_users.ms_gestion_users.service.user.UsuarioProfileService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioProfileService usuarioProfileService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private UsuarioModel usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setUsername("usuario@test.com");
        usuario.setPassword("hashedPassword");
        usuario.setRole("PATIENT");
        usuario.setEstado("ACTIVO");
        usuario.setPasswordTemporal("N");
    }

    @Test
    @DisplayName("Debe realizar login exitoso y retornar token JWT")
    void testLoginSuccess() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("usuario@test.com");
        request.setPassword("password123");

        UsuarioResponse usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(1L);
        usuarioResponse.setUsername("usuario@test.com");

        when(usuarioRepository.findByUsername("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtProperties.getSecret()).thenReturn("miClaveSecretaDe32CaracteresMin12345678");
        when(jwtProperties.getExpMin()).thenReturn(60);
        when(usuarioProfileService.buildProfile(any(UsuarioModel.class))).thenReturn(usuarioResponse);

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(response.getUsuario());
        verify(usuarioRepository, times(1)).findByUsername("usuario@test.com");
        verify(passwordEncoder, times(1)).matches("password123", "hashedPassword");
    }

    @Test
    @DisplayName("Debe lanzar excepción con usuario no encontrado")
    void testLoginUserNotFound() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("noexiste@test.com");
        request.setPassword("password123");

        when(usuarioRepository.findByUsername("noexiste@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    @DisplayName("Debe lanzar excepción con contraseña incorrecta")
    void testLoginWrongPassword() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setUsername("usuario@test.com");
        request.setPassword("wrongPassword");

        when(usuarioRepository.findByUsername("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertTrue(exception.getMessage().contains("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("Debe cambiar la contraseña exitosamente")
    void testChangePasswordSuccess() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword123");

        when(usuarioRepository.findByUsername("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("oldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "hashedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuario);

        // Act
        ChangePasswordResponse response = authService.changePassword("usuario@test.com", request);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        verify(usuarioRepository, times(1)).save(any(UsuarioModel.class));
    }

    @Test
    @DisplayName("Debe rechazar cambio de contraseña con contraseña actual incorrecta")
    void testChangePasswordWrongCurrentPassword() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongOldPassword");
        request.setNewPassword("newPassword123");

        when(usuarioRepository.findByUsername("usuario@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongOldPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.changePassword("usuario@test.com", request);
        });

        assertTrue(exception.getMessage().contains("contraseña actual es incorrecta"));
    }
}
