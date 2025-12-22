package com.gestion_labs.ms_gestion_labs.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.ms_gestion_labs.model.ContactoModel;
import com.gestion_labs.ms_gestion_labs.model.DireccionModel;
import com.gestion_labs.ms_gestion_labs.model.LaboratorioModel;
import com.gestion_labs.ms_gestion_labs.repository.ContactoRepository;
import com.gestion_labs.ms_gestion_labs.repository.DireccionRepository;
import com.gestion_labs.ms_gestion_labs.repository.LaboratorioRepository;
import com.gestion_labs.ms_gestion_labs.service.laboratorio.LaboratorioServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para LaboratorioService")
class LaboratorioServiceTest {

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private ContactoRepository contactoRepository;

    @InjectMocks
    private LaboratorioServiceImpl laboratorioService;

    private LaboratorioModel laboratorio;
    private DireccionModel direccion;
    private ContactoModel contacto;

    @BeforeEach
    void setUp() {
        direccion = new DireccionModel();
        direccion.setId(1L);
        direccion.setCalle("Av. Principal");
        direccion.setNumero(123);
        direccion.setCiudad("Santiago");
        direccion.setComuna("Providencia");
        direccion.setRegion("Metropolitana");

        contacto = new ContactoModel();
        contacto.setId(1L);
        contacto.setFono1("+56912345678");
        contacto.setEmail("lab@ejemplo.com");

        laboratorio = new LaboratorioModel();
        laboratorio.setId(1L);
        laboratorio.setNombre("Laboratorio Central");
        laboratorio.setTipo("CLINICO");
        laboratorio.setDirId(1L);
        laboratorio.setContactoId(1L);
    }

    @Test
    @DisplayName("Debe retornar todos los laboratorios")
    void testFindAll() {
        // Arrange
        when(laboratorioRepository.findAll()).thenReturn(Arrays.asList(laboratorio));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(contacto));

        // Act
        List<LaboratorioDTO> resultado = laboratorioService.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laboratorio Central", resultado.get(0).getNombre());
        verify(laboratorioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar un laboratorio por ID")
    void testFindById() {
        // Arrange
        when(laboratorioRepository.findById(1L)).thenReturn(Optional.of(laboratorio));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(contacto));

        // Act
        LaboratorioDTO resultado = laboratorioService.findById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Laboratorio Central", resultado.getNombre());
        assertEquals("Santiago", resultado.getDireccion().getCiudad());
        verify(laboratorioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el laboratorio no existe")
    void testFindByIdNotFound() {
        // Arrange
        when(laboratorioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            laboratorioService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Laboratorio no encontrado"));
    }

    @Test
    @DisplayName("Debe eliminar un laboratorio")
    void testDelete() {
        // Arrange
        doNothing().when(laboratorioRepository).deleteById(1L);

        // Act
        laboratorioService.delete(1L);

        // Assert
        verify(laboratorioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe crear un laboratorio correctamente")
    void testCreate() {
        // Arrange
        LaboratorioDTO dto = new LaboratorioDTO();
        dto.setNombre("Nuevo Laboratorio");
        dto.setTipo("QUIMICO");
        dto.setDireccion(direccion);
        dto.setContacto(contacto);

        when(direccionRepository.save(any(DireccionModel.class))).thenReturn(direccion);
        when(contactoRepository.save(any(ContactoModel.class))).thenReturn(contacto);
        when(laboratorioRepository.save(any(LaboratorioModel.class))).thenReturn(laboratorio);
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(contactoRepository.findById(1L)).thenReturn(Optional.of(contacto));

        // Act
        LaboratorioDTO resultado = laboratorioService.create(dto);

        // Assert
        assertNotNull(resultado);
        verify(direccionRepository, times(1)).save(any(DireccionModel.class));
        verify(contactoRepository, times(1)).save(any(ContactoModel.class));
        verify(laboratorioRepository, times(1)).save(any(LaboratorioModel.class));
    }
}
