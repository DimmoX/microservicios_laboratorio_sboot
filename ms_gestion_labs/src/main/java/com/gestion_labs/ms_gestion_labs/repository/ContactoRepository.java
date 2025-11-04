package com.gestion_labs.ms_gestion_labs.repository;

import com.gestion_labs.ms_gestion_labs.model.ContactoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactoRepository extends JpaRepository<ContactoModel, Long> {
}
