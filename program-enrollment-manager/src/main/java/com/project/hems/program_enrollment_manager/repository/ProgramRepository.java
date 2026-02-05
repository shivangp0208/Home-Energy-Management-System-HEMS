package com.project.hems.program_enrollment_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.hems.program_enrollment_manager.entity.ProgramEntity;

public interface ProgramRepository extends JpaRepository<ProgramEntity, UUID> {

}
