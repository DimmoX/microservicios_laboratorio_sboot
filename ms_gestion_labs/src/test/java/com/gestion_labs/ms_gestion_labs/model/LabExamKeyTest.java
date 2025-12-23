package com.gestion_labs.ms_gestion_labs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LabExamKeyTest {

    @Test
    void testConstructorVacio() {
        LabExamKey key = new LabExamKey();
        assertNotNull(key);
        assertNull(key.getIdLaboratorio());
        assertNull(key.getIdExamen());
    }

    @Test
    void testConstructorConParametros() {
        Long idLab = 1L;
        Long idExam = 100L;
        
        LabExamKey key = new LabExamKey(idLab, idExam);
        
        assertNotNull(key);
        assertEquals(idLab, key.getIdLaboratorio());
        assertEquals(idExam, key.getIdExamen());
    }

    @Test
    void testIdLaboratorioGetterSetter() {
        LabExamKey key = new LabExamKey();
        Long idLab = 5L;
        
        key.setIdLaboratorio(idLab);
        
        assertEquals(idLab, key.getIdLaboratorio());
    }

    @Test
    void testIdExamenGetterSetter() {
        LabExamKey key = new LabExamKey();
        Long idExam = 200L;
        
        key.setIdExamen(idExam);
        
        assertEquals(idExam, key.getIdExamen());
    }

    @Test
    void testSettersConValoresNull() {
        LabExamKey key = new LabExamKey(1L, 2L);
        
        key.setIdLaboratorio(null);
        key.setIdExamen(null);
        
        assertNull(key.getIdLaboratorio());
        assertNull(key.getIdExamen());
    }

    @Test
    void testEqualsConMismaInstancia() {
        LabExamKey key = new LabExamKey(1L, 100L);
        
        assertEquals(key, key);
    }

    @Test
    void testEqualsConObjetosIguales() {
        LabExamKey key1 = new LabExamKey(1L, 100L);
        LabExamKey key2 = new LabExamKey(1L, 100L);
        
        assertEquals(key1, key2);
        assertEquals(key2, key1);
    }

    @ParameterizedTest
    @CsvSource({
        "1, 100, 2, 200",  // Objetos completamente diferentes
        "1, 100, 2, 100",  // Id laboratorio diferente
        "1, 100, 1, 200"   // Id examen diferente
    })
    void testEqualsConObjetosDiferentes(Long idLab1, Long idExam1, Long idLab2, Long idExam2) {
        LabExamKey key1 = new LabExamKey(idLab1, idExam1);
        LabExamKey key2 = new LabExamKey(idLab2, idExam2);
        
        assertNotEquals(key1, key2);
    }

    @Test
    void testEqualsConNull() {
        LabExamKey key = new LabExamKey(1L, 100L);
        
        assertNotEquals(null, key);
    }

    @Test
    void testEqualsConOtraClase() {
        LabExamKey key = new LabExamKey(1L, 100L);
        String otroObjeto = "No soy LabExamKey";
        
        assertNotEquals(otroObjeto, key);
    }

    @Test
    void testEqualsConValoresNull() {
        LabExamKey key1 = new LabExamKey(null, null);
        LabExamKey key2 = new LabExamKey(null, null);
        
        assertEquals(key1, key2);
    }

    @Test
    void testEqualsConUnCampoNull() {
        LabExamKey key1 = new LabExamKey(1L, null);
        LabExamKey key2 = new LabExamKey(1L, null);
        
        assertEquals(key1, key2);
    }

    @Test
    void testHashCodeConsistente() {
        LabExamKey key = new LabExamKey(1L, 100L);
        
        int hash1 = key.hashCode();
        int hash2 = key.hashCode();
        
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashCodeObjetosIguales() {
        LabExamKey key1 = new LabExamKey(1L, 100L);
        LabExamKey key2 = new LabExamKey(1L, 100L);
        
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testHashCodeObjetosDiferentes() {
        LabExamKey key1 = new LabExamKey(1L, 100L);
        LabExamKey key2 = new LabExamKey(2L, 200L);
        
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testHashCodeConValoresNull() {
        LabExamKey key1 = new LabExamKey(null, null);
        LabExamKey key2 = new LabExamKey(null, null);
        
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testToStringContenidoCompleto() {
        LabExamKey key = new LabExamKey(5L, 250L);
        
        String resultado = key.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("LabExamKey"));
        assertTrue(resultado.contains("idLaboratorio=5"));
        assertTrue(resultado.contains("idExamen=250"));
    }

    @Test
    void testToStringConValoresNull() {
        LabExamKey key = new LabExamKey(null, null);
        
        String resultado = key.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("LabExamKey"));
        assertTrue(resultado.contains("idLaboratorio=null"));
        assertTrue(resultado.contains("idExamen=null"));
    }

    @Test
    void testToStringConIdsGrandes() {
        LabExamKey key = new LabExamKey(999999L, 888888L);
        
        String resultado = key.toString();
        
        assertTrue(resultado.contains("999999"));
        assertTrue(resultado.contains("888888"));
    }

    @Test
    void testSerializableContract() {
        LabExamKey key = new LabExamKey(1L, 100L);
        
        assertTrue(key instanceof java.io.Serializable);
    }

    @Test
    void testIdsCero() {
        LabExamKey key = new LabExamKey(0L, 0L);
        
        assertEquals(0L, key.getIdLaboratorio());
        assertEquals(0L, key.getIdExamen());
    }

    @Test
    void testIdsNegativos() {
        LabExamKey key = new LabExamKey(-1L, -100L);
        
        assertEquals(-1L, key.getIdLaboratorio());
        assertEquals(-100L, key.getIdExamen());
    }
}
