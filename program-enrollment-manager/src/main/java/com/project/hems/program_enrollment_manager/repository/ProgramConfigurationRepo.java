package com.project.hems.program_enrollment_manager.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.ProgramConfigurationEntity;

@Repository
public interface ProgramConfigurationRepo extends JpaRepository<ProgramConfigurationEntity,UUID>{

    
} 