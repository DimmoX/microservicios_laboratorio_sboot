package com.gestion_users.ms_gestion_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestion_users.ms_gestion_users.model.EmpleadoModel;

public interface EmpleadoRepository extends JpaRepository<EmpleadoModel, Long> {}