// Repositorio JPA para Laboratorio
package com.gestion_labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_labs.model.LaboratorioModel;

@Repository
public interface LaboratorioRepository extends JpaRepository<LaboratorioModel, Long> {
    // Puedes agregar métodos personalizados aquí
}
