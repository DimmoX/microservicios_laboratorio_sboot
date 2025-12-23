package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.service.paciente.PacienteService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteService pacienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private PacienteModel testPaciente;

    @BeforeEach
    void setUp() {
        testPaciente = new PacienteModel();
        testPaciente.setId(1L);
        testPaciente.setPnombre("María");
        testPaciente.setPapellido("González");
        testPaciente.setRut("98765432-1");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllPatients() throws Exception {
        List<PacienteModel> pacientes = Arrays.asList(testPaciente);
        when(pacienteService.findAll()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Pacientes obtenidos exitosamente"))
                .andExpect(jsonPath("$.data").isArray());

        verify(pacienteService, times(1)).findAll();
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetAllPatientsAsEmployee() throws Exception {
        List<PacienteModel> pacientes = Arrays.asList(testPaciente);
        when(pacienteService.findAll()).thenReturn(pacientes);

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"));

        verify(pacienteService, times(1)).findAll();
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetPatientById() throws Exception {
        when(pacienteService.findById(1L)).thenReturn(testPaciente);

        mockMvc.perform(get("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Paciente obtenido exitosamente"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(pacienteService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetPatientByIdError() throws Exception {
        when(pacienteService.findById(anyLong())).thenThrow(new RuntimeException("Paciente no encontrado"));

        mockMvc.perform(get("/pacientes/999"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCreatePatient() throws Exception {
        when(pacienteService.create(any(PacienteModel.class))).thenReturn(testPaciente);

        String pacienteJson = objectMapper.writeValueAsString(testPaciente);

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pacienteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Paciente creado exitosamente"));

        verify(pacienteService, times(1)).create(any(PacienteModel.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdatePatient() throws Exception {
        when(pacienteService.update(anyLong(), any(PacienteModel.class))).thenReturn(testPaciente);

        String pacienteJson = objectMapper.writeValueAsString(testPaciente);

        mockMvc.perform(put("/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pacienteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Paciente actualizado exitosamente"));

        verify(pacienteService, times(1)).update(anyLong(), any(PacienteModel.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeletePatient() throws Exception {
        doNothing().when(pacienteService).delete(1L);

        mockMvc.perform(delete("/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Paciente eliminado exitosamente"));

        verify(pacienteService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllPatientsError() throws Exception {
        when(pacienteService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/pacientes"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreatePatientAsEmployeeShouldFail() throws Exception {
        String pacienteJson = objectMapper.writeValueAsString(testPaciente);

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(pacienteJson))
                .andExpect(status().isForbidden());
    }
}
