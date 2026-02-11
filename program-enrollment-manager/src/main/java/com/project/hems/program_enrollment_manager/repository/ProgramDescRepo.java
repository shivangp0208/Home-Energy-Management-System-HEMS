package com.project.hems.program_enrollment_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.ProgramDescEntity;

@Repository
public interface ProgramDescRepo extends JpaRepository<ProgramDescEntity, Long> {

}
