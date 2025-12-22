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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.repository.ExamenRepository;
import com.gestion_labs.ms_gestion_labs.service.examen.ExamenServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para ExamenService")
class ExamenServiceTest {

    @Mock
    private ExamenRepository examenRepository;

    @InjectMocks
    private ExamenServiceImpl examenService;

    private ExamenModel examen;

    @BeforeEach
    void setUp() {
        examen = new ExamenModel();
        examen.setId(1L);
        examen.setCodigo("EX01");
        examen.setNombre("Hemograma Completo");
        examen.setTipo("SANGRE");
    }

    @Test
    @DisplayName("Debe retornar todos los exámenes")
    void testFindAll() {
        // Arrange
        ExamenModel examen2 = new ExamenModel();
        examen2.setId(2L);
        examen2.setCodigo("EX02");
        examen2.setNombre("Glicemia");
        examen2.setTipo("SANGRE");

        when(examenRepository.findAll()).thenReturn(Arrays.asList(examen, examen2));

        // Act
        List<ExamenModel> resultado = examenService.findAll();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Hemograma Completo", resultado.get(0).getNombre());
        verify(examenRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar un examen por ID")
    void testFindById() {
        // Arrange
        when(examenRepository.findById(1L)).thenReturn(Optional.of(examen));

        // Act
        ExamenModel resultado = examenService.findById(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("EX01", resultado.getCodigo());
        verify(examenRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el examen no existe")
    void testFindByIdNotFound() {
        // Arrange
        when(examenRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            examenService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Examen no encontrado"));
        verify(examenRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear un nuevo examen")
    void testCreate() {
        // Arrange
        ExamenModel nuevoExamen = new ExamenModel();
        nuevoExamen.setCodigo("EX03");
        nuevoExamen.setNombre("Perfil Lipídico");
        nuevoExamen.setTipo("SANGRE");

        when(examenRepository.save(any(ExamenModel.class))).thenReturn(nuevoExamen);

        // Act
        ExamenModel resultado = examenService.create(nuevoExamen);

        // Assert
        assertNotNull(resultado);
        assertEquals("EX03", resultado.getCodigo());
        assertEquals("Perfil Lipídico", resultado.getNombre());
        verify(examenRepository, times(1)).save(any(ExamenModel.class));
    }

    @Test
    @DisplayName("Debe actualizar un examen existente")
    void testUpdate() {
        // Arrange
        ExamenModel actualizacion = new ExamenModel();
        actualizacion.setNombre("Hemograma Actualizado");
        actualizacion.setTipo("SANGRE_VENOSA");

        when(examenRepository.findById(1L)).thenReturn(Optional.of(examen));
        when(examenRepository.save(any(ExamenModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ExamenModel resultado = examenService.update(1L, actualizacion);

        // Assert
        assertNotNull(resultado);
        assertEquals("Hemograma Actualizado", resultado.getNombre());
        assertEquals("SANGRE_VENOSA", resultado.getTipo());
        verify(examenRepository, times(1)).findById(1L);
        verify(examenRepository, times(1)).save(any(ExamenModel.class));
    }
}
