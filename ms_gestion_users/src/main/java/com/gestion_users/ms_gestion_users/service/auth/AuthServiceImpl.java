package com.gestion_users.ms_gestion_users.service.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion_users.ms_gestion_users.config.JwtProperties;
import com.gestion_users.ms_gestion_users.dto.AuthRequest;
import com.gestion_users.ms_gestion_users.dto.AuthResponse;
import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ChangePasswordResponse;
import com.gestion_users.ms_gestion_users.dto.ForgotPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ForgotPasswordResponse;
import com.gestion_users.ms_gestion_users.dto.HashRequest;
import com.gestion_users.ms_gestion_users.dto.HashResponse;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import com.gestion_users.ms_gestion_users.service.email.EmailService;
import com.gestion_users.ms_gestion_users.service.user.UsuarioProfileService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repo;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioProfileService usuarioProfileService;
    private final EmailService emailService;

    public AuthServiceImpl(
        UsuarioRepository repo,
        JwtProperties jwtProperties,
        PasswordEncoder passwordEncoder,
        UsuarioProfileService usuarioProfileService,
        EmailService emailService
    ) {
        this.repo = repo;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
        this.usuarioProfileService = usuarioProfileService;
        this.emailService = emailService;
    }

    @Override
    public AuthResponse login(AuthRequest req) {
        UsuarioModel user = repo.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Contraseña incorrecta");

        long expirationMs = jwtProperties.getExpMin() * 60 * 1000L;

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        // Construir claims del JWT
        var jwtBuilder = Jwts.builder()
            .subject(user.getUsername())
            .claim("role", user.getRole())
            .claim("userId", user.getId())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs));

        // Agregar pacienteId si el usuario es PATIENT
        if (user.getPacienteId() != null) {
            jwtBuilder.claim("pacienteId", user.getPacienteId());
        }

        // Agregar empleadoId si el usuario es LAB_EMPLOYEE
        if (user.getEmpleadoId() != null) {
            jwtBuilder.claim("empleadoId", user.getEmpleadoId());
        }

        // Agregar flag si la contraseña es temporal
        if (user.isPasswordTemporal()) {
            jwtBuilder.claim("requiresPasswordChange", true);
        }

        String token = jwtBuilder.signWith(key).compact();

        // DTO orientado al Frontend (sin exponer el password)
        UsuarioResponse usuario = usuarioProfileService.buildProfile(user);
        return new AuthResponse(token, usuario);
    }

    @Override
    public HashResponse generatePasswordHash(HashRequest req) {
        String hash = passwordEncoder.encode(req.getPassword());
        return new HashResponse(req.getPassword(), hash);
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest req) {
        UsuarioModel user = repo.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        String newHash = passwordEncoder.encode(req.getNewPassword());
        user.setPassword(newHash);
        repo.save(user);
        
        return new ResetPasswordResponse(
            user.getUsername(), 
            "Contraseña actualizada exitosamente",
            newHash
        );
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest req) {
        // Validar formato de email
        if (req.getEmail() == null || !req.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Formato de email inválido");
        }

        // Buscar usuario por email (username es el email)
        UsuarioModel user = repo.findByUsername(req.getEmail())
            .orElseThrow(() -> new RuntimeException("No existe una cuenta asociada a este email"));

        // Validar que el usuario esté activo
        if (!"ACTIVO".equals(user.getEstado())) {
            throw new RuntimeException("La cuenta asociada a este email está desactivada");
        }

        // Generar contraseña temporal aleatoria (8 caracteres alfanuméricos)
        String temporaryPassword = generateRandomPassword(8);

        // Hashear la contraseña temporal
        String hashedPassword = passwordEncoder.encode(temporaryPassword);

        // Actualizar usuario con contraseña temporal
        user.setPassword(hashedPassword);
        user.setPasswordTemporal("S"); // Marcar como contraseña temporal
        repo.save(user);

        // Enviar email con la contraseña temporal
        emailService.sendTemporaryPasswordEmail(user.getUsername(), temporaryPassword);

        // Devolver contraseña también en la respuesta (útil si el email falla o está deshabilitado)
        return new ForgotPasswordResponse(
            user.getUsername(),
            "Se ha enviado una contraseña temporal a tu correo electrónico. También puedes verla a continuación. Por seguridad, cámbiala en tu primer inicio de sesión.",
            temporaryPassword // Devolver contraseña en texto plano (solo esta vez)
        );
    }

    @Override
    public ChangePasswordResponse changePassword(String username, ChangePasswordRequest req) {
        // Buscar usuario
        UsuarioModel user = repo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
        }

        // Validar requisitos mínimos de la nueva contraseña
        if (req.getNewPassword() == null || req.getNewPassword().length() < 6) {
            throw new RuntimeException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar contraseña y marcar como NO temporal
        String newHash = passwordEncoder.encode(req.getNewPassword());
        user.setPassword(newHash);
        user.setPasswordTemporal("N"); // Marcar como contraseña definitiva
        repo.save(user);

        return new ChangePasswordResponse(
            user.getUsername(),
            "Contraseña actualizada exitosamente",
            true
        );
    }

    // Método auxiliar para generar contraseña aleatoria
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}