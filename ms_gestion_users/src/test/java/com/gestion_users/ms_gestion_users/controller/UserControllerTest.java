package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.service.user.UserService;
import com.gestion_users.ms_gestion_users.service.user.UsuarioProfileService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UsuarioProfileService usuarioProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioModel testUser;
    private UsuarioResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = new UsuarioModel();
        testUser.setId(1L);
        testUser.setUsername("test@user.com");
        testUser.setRole("ADMIN");
        testUser.setEstado("ACTIVO");

        testUserResponse = new UsuarioResponse();
        testUserResponse.setId(1L);
        testUserResponse.setUsername("test@user.com");
        testUserResponse.setRol("ADMIN");
        testUserResponse.setActivo(true);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllUsers() throws Exception {
        List<UsuarioModel> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);
        when(usuarioProfileService.buildProfile(any())).thenReturn(testUserResponse);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Usuarios obtenidos exitosamente"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetAllUsersAsEmployee() throws Exception {
        List<UsuarioModel> users = Arrays.asList(testUser);
        when(userService.findAll()).thenReturn(users);
        when(usuarioProfileService.buildProfile(any())).thenReturn(testUserResponse);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserById() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);
        when(usuarioProfileService.buildProfile(any())).thenReturn(testUserResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("test@user.com"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetUserByIdError() throws Exception {
        when(userService.findById(anyLong())).thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCreateUser() throws Exception {
        when(userService.create(any())).thenReturn(testUser);
        when(usuarioProfileService.buildProfile(any())).thenReturn(testUserResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Usuario creado exitosamente"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateUserAsEmployeeWithPatientRole() throws Exception {
        testUser.setRole("PATIENT");
        when(userService.create(any())).thenReturn(testUser);
        when(usuarioProfileService.buildProfile(any())).thenReturn(testUserResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateUserAsEmployeeWithAdminRoleShouldFail() throws Exception {
        testUser.setRole("ADMIN");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllUsersException() throws Exception {
        when(userService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/users"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }
}
