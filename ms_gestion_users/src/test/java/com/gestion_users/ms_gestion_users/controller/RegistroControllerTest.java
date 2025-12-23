package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.dto.*;
import com.gestion_users.ms_gestion_users.service.registro.RegistroService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistroService registroService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistroPacienteRequest registroPacienteRequest;
    private RegistroEmpleadoRequest registroEmpleadoRequest;
    private RegistroResponse registroResponse;

    @BeforeEach
    void setUp() {
        // Setup Paciente Request
        ContactoDTO contacto = new ContactoDTO();
        contacto.setFono1("+56912345678");
        contacto.setEmail("test@test.cl");

        DireccionDTO direccion = new DireccionDTO();
        direccion.setCalle("Av. Principal");
        direccion.setNumero(123);
        direccion.setCiudad("Santiago");
        direccion.setComuna("Providencia");

        registroPacienteRequest = new RegistroPacienteRequest();
        registroPacienteRequest.setPnombre("Juan");
        registroPacienteRequest.setPapellido("Pérez");
        registroPacienteRequest.setRut("12345678-9");
        registroPacienteRequest.setContacto(contacto);
        registroPacienteRequest.setDireccion(direccion);
        registroPacienteRequest.setPassword("Password123");

        // Setup Empleado Request
        registroEmpleadoRequest = new RegistroEmpleadoRequest();
        registroEmpleadoRequest.setPnombre("María");
        registroEmpleadoRequest.setPapellido("González");
        registroEmpleadoRequest.setRut("98765432-1");
        registroEmpleadoRequest.setCargo("Técnico");
        registroEmpleadoRequest.setContacto(contacto);
        registroEmpleadoRequest.setDireccion(direccion);
        registroEmpleadoRequest.setPassword("Password123");

        // Setup Response
        registroResponse = new RegistroResponse();
        registroResponse.setMensaje("Registro exitoso");
        registroResponse.setUsuarioId(1L);
        registroResponse.setUsername("test@test.cl");
    }

    @Test
    void testRegistrarPacientePublic() throws Exception {
        when(registroService.registrarPaciente(any(RegistroPacienteRequest.class)))
                .thenReturn(registroResponse);

        String requestJson = objectMapper.writeValueAsString(registroPacienteRequest);

        mockMvc.perform(post("/registro/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Paciente registrado exitosamente"))
                .andExpect(jsonPath("$.data.mensaje").value("Registro exitoso"));

        verify(registroService, times(1)).registrarPaciente(any(RegistroPacienteRequest.class));
    }

    @Test
    void testRegistrarPacienteError() throws Exception {
        when(registroService.registrarPaciente(any(RegistroPacienteRequest.class)))
                .thenThrow(new RuntimeException("Error en el registro"));

        String requestJson = objectMapper.writeValueAsString(registroPacienteRequest);

        mockMvc.perform(post("/registro/paciente")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"))
                .andExpect(jsonPath("$.description").value("Error al registrar paciente: Error en el registro"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegistrarEmpleadoAsAdmin() throws Exception {
        when(registroService.registrarEmpleado(any(RegistroEmpleadoRequest.class)))
                .thenReturn(registroResponse);

        String requestJson = objectMapper.writeValueAsString(registroEmpleadoRequest);

        mockMvc.perform(post("/registro/empleado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Empleado registrado exitosamente"))
                .andExpect(jsonPath("$.data.mensaje").value("Registro exitoso"));

        verify(registroService, times(1)).registrarEmpleado(any(RegistroEmpleadoRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegistrarEmpleadoError() throws Exception {
        when(registroService.registrarEmpleado(any(RegistroEmpleadoRequest.class)))
                .thenThrow(new RuntimeException("Error en el registro"));

        String requestJson = objectMapper.writeValueAsString(registroEmpleadoRequest);

        mockMvc.perform(post("/registro/empleado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"))
                .andExpect(jsonPath("$.description").value("Error al registrar empleado: Error en el registro"));
    }

    @Test
    void testRegistrarEmpleadoSinAutenticacionShouldFail() throws Exception {
        String requestJson = objectMapper.writeValueAsString(registroEmpleadoRequest);

        mockMvc.perform(post("/registro/empleado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testRegistrarEmpleadoAsEmployeeShouldFail() throws Exception {
        String requestJson = objectMapper.writeValueAsString(registroEmpleadoRequest);

        mockMvc.perform(post("/registro/empleado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isForbidden());
    }
}
