package com.hems.project.virtual_power_plant.repository;

import com.hems.project.virtual_power_plant.entity.Vpp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VppRepository extends JpaRepository<Vpp, UUID> {
    Optional<Vpp> findByEmail(String email);
}
