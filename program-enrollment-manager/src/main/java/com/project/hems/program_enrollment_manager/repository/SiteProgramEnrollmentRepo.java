package com.project.hems.program_enrollment_manager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;

@Repository
public interface SiteProgramEnrollmentRepo extends JpaRepository<SiteProgramEnrollmentEntity, UUID> {

       @Query("SELECT spe.program FROM SiteProgramEnrollmentEntity spe " +
                     "WHERE spe.siteId = :siteId")
       List<ProgramEntity> findProgramsBySiteId(@Param("siteId") UUID siteId);

       @Query("SELECT spe.siteId FROM SiteProgramEnrollmentEntity spe " +
                     "WHERE spe.program.programId = :programId")
       List<UUID> findSiteIdByProgramId(@Param("programId") UUID programId);

       // @Query("SELECT s FROM SiteProgramEnrollmentEntity WHERE
       // programId=:programId")
       // List<SiteProgramEnrollmentEntity>
       // findSiteProgramEnrollmentEntityByProgramId(@Param("programId") UUID
       // programId);
       List<SiteProgramEnrollmentEntity> findByProgram_ProgramId(UUID programId);

       @Query("SELECT s.enrollmentId FROM SiteProgramEnrollmentEntity s " +
                     "WHERE s.program.programId = :programId " +
                     "AND s.siteId = :siteId")
       Optional<UUID> findEnrollmentId(@Param("programId") UUID programId, @Param("siteId") UUID siteId);

}