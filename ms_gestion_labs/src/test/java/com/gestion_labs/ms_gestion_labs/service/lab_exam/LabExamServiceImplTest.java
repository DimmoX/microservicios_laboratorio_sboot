package com.gestion_labs.ms_gestion_labs.service.lab_exam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.repository.LabExamRepository;

@ExtendWith(MockitoExtension.class)
class LabExamServiceImplTest {

    @Mock
    private LabExamRepository repo;

    @InjectMocks
    private LabExamServiceImpl service;

    private LabExamModel labExam;
    private LabExamKey key;

    @BeforeEach
    void setUp() {
        key = new LabExamKey(1L, 10L);
        labExam = new LabExamModel();
        labExam.setId(key);
        labExam.setPrecio(new BigDecimal("50000"));
        labExam.setVigenteDesde(LocalDate.now());
        labExam.setVigenteHasta(LocalDate.now().plusMonths(6));
    }

    @Test
    void testListAll() {
        // Arrange
        LabExamModel labExam2 = new LabExamModel();
        labExam2.setId(new LabExamKey(2L, 20L));
        labExam2.setPrecio(new BigDecimal("75000"));
        
        when(repo.findAll()).thenReturn(Arrays.asList(labExam, labExam2));

        // Act
        List<LabExamModel> result = service.listAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testListAllListaVacia() {
        // Arrange
        when(repo.findAll()).thenReturn(Arrays.asList());

        // Act
        List<LabExamModel> result = service.listAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findAll();
    }

    @Test
    void testListByLaboratorio() {
        // Arrange
        Long idLaboratorio = 1L;
        when(repo.findById_IdLaboratorio(idLaboratorio)).thenReturn(Arrays.asList(labExam));

        // Act
        List<LabExamModel> result = service.listByLaboratorio(idLaboratorio);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(idLaboratorio, result.get(0).getId().getIdLaboratorio());
        verify(repo, times(1)).findById_IdLaboratorio(idLaboratorio);
    }

    @Test
    void testListByLaboratorioListaVacia() {
        // Arrange
        Long idLaboratorio = 999L;
        when(repo.findById_IdLaboratorio(idLaboratorio)).thenReturn(Arrays.asList());

        // Act
        List<LabExamModel> result = service.listByLaboratorio(idLaboratorio);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findById_IdLaboratorio(idLaboratorio);
    }

    @Test
    void testListByExamen() {
        // Arrange
        Long idExamen = 10L;
        when(repo.findById_IdExamen(idExamen)).thenReturn(Arrays.asList(labExam));

        // Act
        List<LabExamModel> result = service.listByExamen(idExamen);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(idExamen, result.get(0).getId().getIdExamen());
        verify(repo, times(1)).findById_IdExamen(idExamen);
    }

    @Test
    void testListByExamenListaVacia() {
        // Arrange
        Long idExamen = 999L;
        when(repo.findById_IdExamen(idExamen)).thenReturn(Arrays.asList());

        // Act
        List<LabExamModel> result = service.listByExamen(idExamen);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, times(1)).findById_IdExamen(idExamen);
    }

    @Test
    void testGet() {
        // Arrange
        when(repo.findById(key)).thenReturn(Optional.of(labExam));

        // Act
        LabExamModel result = service.get(1L, 10L);

        // Assert
        assertNotNull(result);
        assertEquals(key, result.getId());
        assertEquals(new BigDecimal("50000"), result.getPrecio());
        verify(repo, times(1)).findById(key);
    }

    @Test
    void testGetNoEncontrado() {
        // Arrange
        when(repo.findById(key)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.get(1L, 10L);
        });
        
        assertTrue(exception.getMessage().contains("Precio no encontrado"));
        verify(repo, times(1)).findById(key);
    }

    @Test
    void testUpsertNuevo() {
        // Arrange
        when(repo.findById(key)).thenReturn(Optional.empty());
        when(repo.save(labExam)).thenReturn(labExam);

        // Act
        LabExamModel result = service.upsert(labExam);

        // Assert
        assertNotNull(result);
        assertEquals(key, result.getId());
        verify(repo, times(1)).findById(key);
        verify(repo, times(1)).save(labExam);
    }

    @Test
    void testUpsertActualizacionCompleta() {
        // Arrange
        LabExamModel existing = new LabExamModel();
        existing.setId(key);
        existing.setPrecio(new BigDecimal("40000"));
        existing.setVigenteDesde(LocalDate.now().minusMonths(1));
        existing.setVigenteHasta(LocalDate.now().plusMonths(3));

        LabExamModel update = new LabExamModel();
        update.setId(key);
        update.setPrecio(new BigDecimal("60000"));
        update.setVigenteDesde(LocalDate.now());
        update.setVigenteHasta(LocalDate.now().plusMonths(12));

        when(repo.findById(key)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        // Act
        LabExamModel result = service.upsert(update);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("60000"), result.getPrecio());
        assertEquals(update.getVigenteDesde(), result.getVigenteDesde());
        assertEquals(update.getVigenteHasta(), result.getVigenteHasta());
        verify(repo, times(1)).findById(key);
        verify(repo, times(1)).save(existing);
    }

    @Test
    void testUpsertActualizacionParcialSoloPrecio() {
        // Arrange
        LabExamModel existing = new LabExamModel();
        existing.setId(key);
        existing.setPrecio(new BigDecimal("40000"));
        existing.setVigenteDesde(LocalDate.now().minusMonths(1));
        existing.setVigenteHasta(LocalDate.now().plusMonths(3));

        LabExamModel update = new LabExamModel();
        update.setId(key);
        update.setPrecio(new BigDecimal("55000"));
        // No se actualizan fechas

        when(repo.findById(key)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        // Act
        LabExamModel result = service.upsert(update);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("55000"), result.getPrecio());
        // Las fechas deben mantenerse sin cambios
        assertEquals(existing.getVigenteDesde(), result.getVigenteDesde());
        assertEquals(existing.getVigenteHasta(), result.getVigenteHasta());
        verify(repo, times(1)).findById(key);
        verify(repo, times(1)).save(existing);
    }

    @Test
    void testUpsertActualizacionParcialSoloFechas() {
        // Arrange
        LabExamModel existing = new LabExamModel();
        existing.setId(key);
        existing.setPrecio(new BigDecimal("40000"));
        existing.setVigenteDesde(LocalDate.now().minusMonths(1));
        existing.setVigenteHasta(LocalDate.now().plusMonths(3));

        LabExamModel update = new LabExamModel();
        update.setId(key);
        // No se actualiza precio
        update.setVigenteDesde(LocalDate.now());
        update.setVigenteHasta(LocalDate.now().plusMonths(12));

        when(repo.findById(key)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        // Act
        LabExamModel result = service.upsert(update);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("40000"), result.getPrecio()); // Sin cambios
        assertEquals(update.getVigenteDesde(), result.getVigenteDesde());
        assertEquals(update.getVigenteHasta(), result.getVigenteHasta());
        verify(repo, times(1)).findById(key);
        verify(repo, times(1)).save(existing);
    }

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(repo).deleteById(key);

        // Act
        service.delete(1L, 10L);

        // Assert
        verify(repo, times(1)).deleteById(key);
    }

    @Test
    void testDeleteConError() {
        // Arrange
        doThrow(new RuntimeException("Error al eliminar")).when(repo).deleteById(key);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            service.delete(1L, 10L);
        });
        
        verify(repo, times(1)).deleteById(key);
    }
}
