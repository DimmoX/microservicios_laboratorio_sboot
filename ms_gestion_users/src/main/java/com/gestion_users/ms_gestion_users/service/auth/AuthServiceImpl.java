package com.gestion_users.ms_gestion_users.service.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gestion_users.ms_gestion_users.config.JwtProperties;
import com.gestion_users.ms_gestion_users.dto.AuthRequest;
import com.gestion_users.ms_gestion_users.dto.AuthResponse;
import com.gestion_users.ms_gestion_users.dto.HashRequest;
import com.gestion_users.ms_gestion_users.dto.HashResponse;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import com.gestion_users.ms_gestion_users.service.user.UsuarioProfileService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repo;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioProfileService usuarioProfileService;

    public AuthServiceImpl(
        UsuarioRepository repo,
        JwtProperties jwtProperties,
        PasswordEncoder passwordEncoder,
        UsuarioProfileService usuarioProfileService
    ) {
        this.repo = repo;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
        this.usuarioProfileService = usuarioProfileService;
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
}