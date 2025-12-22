package com.gestion_resultados.ms_gestion_resultados.service;

import java.time.OffsetDateTime;
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

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import com.gestion_resultados.ms_gestion_resultados.repository.ResultadoExamenRepository;
import com.gestion_resultados.ms_gestion_resultados.service.resultados.ResultadoServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios para ResultadoService")
class ResultadoServiceTest {

    @Mock
    private ResultadoExamenRepository resultadoRepository;

    @InjectMocks
    private ResultadoServiceImpl resultadoService;

    private ResultadoExamenModel resultado;

    @BeforeEach
    void setUp() {
        resultado = new ResultadoExamenModel();
        resultado.setId(1L);
        resultado.setAgendaId(100L);
        resultado.setPacienteId(10L);
        resultado.setLabId(1L);
        resultado.setExamenId(1L);
        resultado.setEmpleadoId(5L);
        resultado.setFechaMuestra(OffsetDateTime.now());
        resultado.setValor("120");
        resultado.setUnidad("mg/dL");
        resultado.setEstado("PENDIENTE");
        resultado.setObservacion("Resultados normales");
    }

    @Test
    @DisplayName("Debe retornar todos los resultados")
    void testFindAll() {
        // Arrange
        ResultadoExamenModel resultado2 = new ResultadoExamenModel();
        resultado2.setId(2L);
        resultado2.setPacienteId(11L);
        resultado2.setValor("85");
        resultado2.setEstado("EMITIDO");

        when(resultadoRepository.findAll()).thenReturn(Arrays.asList(resultado, resultado2));

        // Act
        List<ResultadoExamenModel> resultados = resultadoService.findAll();

        // Assert
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        verify(resultadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar un resultado por ID")
    void testFindById() {
        // Arrange
        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));

        // Act
        ResultadoExamenModel encontrado = resultadoService.findById(1L);

        // Assert
        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        assertEquals("120", encontrado.getValor());
        verify(resultadoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el resultado no existe")
    void testFindByIdNotFound() {
        // Arrange
        when(resultadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resultadoService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Resultado no encontrado"));
    }

    @Test
    @DisplayName("Debe retornar resultados por paciente ID")
    void testFindByPaciente() {
        // Arrange
        when(resultadoRepository.findByPacienteId(10L)).thenReturn(Arrays.asList(resultado));

        // Act
        List<ResultadoExamenModel> resultados = resultadoService.findByPaciente(10L);

        // Assert
        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals(10L, resultados.get(0).getPacienteId());
        verify(resultadoRepository, times(1)).findByPacienteId(10L);
    }

    @Test
    @DisplayName("Debe crear un nuevo resultado con validación")
    void testCreate() {
        // Arrange
        ResultadoExamenModel nuevoResultado = new ResultadoExamenModel();
        nuevoResultado.setAgendaId(101L);
        nuevoResultado.setPacienteId(12L);
        nuevoResultado.setLabId(1L);
        nuevoResultado.setExamenId(2L);
        nuevoResultado.setEmpleadoId(5L);
        nuevoResultado.setFechaMuestra(OffsetDateTime.now());
        nuevoResultado.setValor("95");
        nuevoResultado.setUnidad("mg/dL");
        nuevoResultado.setEstado("PENDIENTE");

        when(resultadoRepository.findByAgendaId(101L)).thenReturn(Optional.empty());
        when(resultadoRepository.save(any(ResultadoExamenModel.class))).thenAnswer(invocation -> {
            ResultadoExamenModel r = invocation.getArgument(0);
            r.setId(2L);
            return r;
        });

        // Act
        ResultadoExamenModel creado = resultadoService.create(nuevoResultado);

        // Assert
        assertNotNull(creado);
        assertEquals(2L, creado.getId());
        assertEquals("95", creado.getValor());
        verify(resultadoRepository, times(1)).save(any(ResultadoExamenModel.class));
    }

    @Test
    @DisplayName("Debe actualizar un resultado existente parcialmente")
    void testUpdate() {
        // Arrange
        ResultadoExamenModel actualizacion = new ResultadoExamenModel();
        actualizacion.setValor("130");
        actualizacion.setEstado("EMITIDO");

        when(resultadoRepository.findById(1L)).thenReturn(Optional.of(resultado));
        when(resultadoRepository.save(any(ResultadoExamenModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResultadoExamenModel actualizado = resultadoService.update(1L, actualizacion);

        // Assert
        assertNotNull(actualizado);
        assertEquals("130", actualizado.getValor());
        assertEquals("EMITIDO", actualizado.getEstado());
        assertNotNull(actualizado.getFechaResultado()); // Se establece al cambiar a EMITIDO
        verify(resultadoRepository, times(1)).save(any(ResultadoExamenModel.class));
    }

    @Test
    @DisplayName("Debe rechazar creación sin campos obligatorios")
    void testCreateWithoutRequiredFields() {
        // Arrange
        ResultadoExamenModel resultadoInvalido = new ResultadoExamenModel();
        resultadoInvalido.setFechaMuestra(OffsetDateTime.now());
        // Falta valor, unidad, estado

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            resultadoService.create(resultadoInvalido);
        });

        assertTrue(exception.getMessage().contains("obligatorio"));
    }

    @Test
    @DisplayName("Debe eliminar un resultado")
    void testDelete() {
        // Arrange
        doNothing().when(resultadoRepository).deleteById(1L);

        // Act
        resultadoService.delete(1L);

        // Assert
        verify(resultadoRepository, times(1)).deleteById(1L);
    }
}
