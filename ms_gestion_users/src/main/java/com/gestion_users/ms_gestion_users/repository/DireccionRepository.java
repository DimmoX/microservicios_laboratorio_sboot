package com.gestion_users.ms_gestion_users.repository;

import com.gestion_users.ms_gestion_users.model.DireccionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionRepository extends JpaRepository<DireccionModel, Long> {
}
