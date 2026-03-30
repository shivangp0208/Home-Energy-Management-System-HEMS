package com.project.hems.site_manager_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.hems.site_manager_service.entity.Owner;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepo extends JpaRepository<Owner, UUID> {

    Optional<Owner> findByEmail(String email);
}
