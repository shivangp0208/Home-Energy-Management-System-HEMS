package com.hems.project.Virtual_Power_Plant.repository;

import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VppRepository extends JpaRepository<Vpp, UUID> {
    Optional<Vpp> findByEmail(String email);
}
