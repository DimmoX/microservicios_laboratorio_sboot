package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import com.gestion_users.ms_gestion_users.service.empleado.EmpleadoService;
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
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpleadoModel testEmpleado;

    @BeforeEach
    void setUp() {
        testEmpleado = new EmpleadoModel();
        testEmpleado.setId(1L);
        testEmpleado.setPnombre("Juan");
        testEmpleado.setPapellido("Pérez");
        testEmpleado.setRut("12345678-9");
        testEmpleado.setCargo("Técnico");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllEmployees() throws Exception {
        List<EmpleadoModel> empleados = Arrays.asList(testEmpleado);
        when(empleadoService.findAll()).thenReturn(empleados);

        mockMvc.perform(get("/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Empleados obtenidos exitosamente"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testGetAllEmployeesAsEmployee() throws Exception {
        List<EmpleadoModel> empleados = Arrays.asList(testEmpleado);
        when(empleadoService.findAll()).thenReturn(empleados);

        mockMvc.perform(get("/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetEmployeeById() throws Exception {
        when(empleadoService.findById(1L)).thenReturn(testEmpleado);

        mockMvc.perform(get("/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.pnombre").value("Juan"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetEmployeeByIdError() throws Exception {
        when(empleadoService.findById(anyLong())).thenThrow(new RuntimeException("Empleado no encontrado"));

        mockMvc.perform(get("/empleados/999"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testCreateEmployee() throws Exception {
        when(empleadoService.create(any())).thenReturn(testEmpleado);

        mockMvc.perform(post("/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmpleado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Empleado creado exitosamente"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testUpdateEmployee() throws Exception {
        when(empleadoService.update(anyLong(), any())).thenReturn(testEmpleado);

        mockMvc.perform(put("/empleados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmpleado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Empleado actualizado exitosamente"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testDeleteEmployee() throws Exception {
        doNothing().when(empleadoService).delete(1L);

        mockMvc.perform(delete("/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("000"))
                .andExpect(jsonPath("$.description").value("Empleado eliminado exitosamente"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void testGetAllEmployeesError() throws Exception {
        when(empleadoService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/empleados"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    @WithMockUser(authorities = "EMPLOYEE")
    void testCreateEmployeeAsEmployeeShouldFail() throws Exception {
        mockMvc.perform(post("/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmpleado)))
                .andExpect(status().isForbidden());
    }
}
