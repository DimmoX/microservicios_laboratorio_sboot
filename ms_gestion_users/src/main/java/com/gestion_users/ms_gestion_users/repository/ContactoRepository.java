package com.gestion_users.ms_gestion_users.repository;

import com.gestion_users.ms_gestion_users.model.ContactoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactoRepository extends JpaRepository<ContactoModel, Long> {
    Optional<ContactoModel> findByEmail(String email);
}
