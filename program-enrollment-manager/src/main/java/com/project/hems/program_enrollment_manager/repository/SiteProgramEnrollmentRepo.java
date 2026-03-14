package com.project.hems.program_enrollment_manager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import java.util.List;


@Repository
public interface SiteProgramEnrollmentRepo extends JpaRepository<SiteProgramEnrollmentEntity, UUID> {

       boolean existsBySiteAndProgram(UUID site, ProgramEntity program);

       List<SiteProgramEnrollmentEntity> findBySiteAndProgram(UUID site, ProgramEntity program);

       List<SiteProgramEnrollmentEntity> findBySite(UUID site);

       @Query("SELECT spe.program.programId FROM SiteProgramEnrollmentEntity spe WHERE spe.site = :siteId")
       List<UUID> findProgramIdsBySiteId(@Param("siteId") UUID siteId);
}