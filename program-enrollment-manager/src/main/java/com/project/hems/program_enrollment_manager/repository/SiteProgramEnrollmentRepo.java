package com.project.hems.program_enrollment_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;

@Repository
public interface SiteProgramEnrollmentRepo extends JpaRepository<SiteProgramEnrollmentEntity, UUID> {

       boolean existsBySiteAndProgram(UUID site, ProgramEntity program);
}