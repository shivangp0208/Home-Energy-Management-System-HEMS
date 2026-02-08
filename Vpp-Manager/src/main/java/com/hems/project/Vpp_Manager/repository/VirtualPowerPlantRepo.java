package com.hems.project.Vpp_Manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hems.project.Vpp_Manager.entity.VirtualPowerPlant;

@Repository
public interface VirtualPowerPlantRepo extends JpaRepository<VirtualPowerPlant,UUID>{

    
}
