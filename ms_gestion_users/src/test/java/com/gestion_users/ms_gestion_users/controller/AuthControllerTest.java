package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.dto.AuthRequest;
import com.gestion_users.ms_gestion_users.dto.AuthResponse;
import com.gestion_users.ms_gestion_users.dto.HashRequest;
import com.gestion_users.ms_gestion_users.dto.HashResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.service.BlacklistSyncService;
import com.gestion_users.ms_gestion_users.service.TokenBlacklistService;
import com.gestion_users.ms_gestion_users.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenBlacklistService blacklistService;

    @MockBean
    private BlacklistSyncService syncService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private HashRequest hashRequest;
    private HashResponse hashResponse;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("test@user.com");
        authRequest.setPassword("password123");

        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        usuario.setUsername("test@user.com");
        usuario.setRol("ADMIN");

        authResponse = new AuthResponse();
        authResponse.setToken("jwt.token.here");
        authResponse.setUsuario(usuario);

        hashRequest = new HashRequest();
        hashRequest.setPassword("password123");

        hashResponse = new HashResponse();
        hashResponse.setPassword("password123");
        hashResponse.setHash("$2a$10$hashed.password");
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Login exitoso"))
                .andExpect(jsonPath("$.data.token").value("jwt.token.here"));
    }

    @Test
    void testLoginFailure() throws Exception {
        when(authService.login(any())).thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    void testGenerateHash() throws Exception {
        when(authService.generatePasswordHash(any())).thenReturn(hashResponse);

        mockMvc.perform(post("/auth/generate-hash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hashRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Hash generado exitosamente"))
                .andExpect(jsonPath("$.data.hash").exists());
    }

    @Test
    void testGenerateHashError() throws Exception {
        when(authService.generatePasswordHash(any())).thenThrow(new RuntimeException("Error al generar hash"));

        mockMvc.perform(post("/auth/generate-hash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hashRequest)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    @WithMockUser
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer jwt.token.here"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Logout exitoso"));
    }

    @Test
    void testLogoutWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"));
    }
}
