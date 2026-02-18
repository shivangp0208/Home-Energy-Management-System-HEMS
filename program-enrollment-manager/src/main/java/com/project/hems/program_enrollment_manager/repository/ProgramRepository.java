package com.project.hems.program_enrollment_manager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.ProgramEntity;

@Repository
public interface ProgramRepository extends JpaRepository<ProgramEntity, UUID> {
    @Query("SELECT p.programStatus FROM ProgramEntity p WHERE p.programId = :programId")
    Optional<ProgramStatus> findProgramStatusByProgramId(UUID programId);

    boolean existsByProgramId(UUID programId);

    @Query("""
            SELECT p
            FROM ProgramEntity p
            WHERE :siteId MEMBER OF p.sites
            """)
    public List<ProgramEntity> findAllProgramEnrolledBySites(@Param("siteId") UUID siteId);

}
