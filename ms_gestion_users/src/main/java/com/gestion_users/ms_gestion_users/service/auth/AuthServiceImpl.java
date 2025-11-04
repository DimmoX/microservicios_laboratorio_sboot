package com.gestion_users.ms_gestion_users.service.auth;

import com.gestion_users.ms_gestion_users.config.JwtProperties;
import com.gestion_users.ms_gestion_users.dto.*;
import com.gestion_users.ms_gestion_users.repository.UsuarioRepository;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repo;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UsuarioRepository repo, JwtProperties jwtProperties, PasswordEncoder passwordEncoder) { 
        this.repo = repo;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(AuthRequest req) {
        UsuarioModel user = repo.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Contraseña incorrecta");

        long expirationMs = jwtProperties.getExpMin() * 60 * 1000L;
        
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        
        String token = Jwts.builder()
            .subject(user.getUsername())
            .claim("role", user.getRole())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact();

        return new AuthResponse(token);
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