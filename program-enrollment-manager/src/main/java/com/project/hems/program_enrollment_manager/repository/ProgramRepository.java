package com.project.hems.program_enrollment_manager.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.model.ProgramStatus;

@Repository
public interface ProgramRepository extends JpaRepository<ProgramEntity, UUID> {
    @Query("SELECT p.programStatus FROM ProgramEntity p WHERE p.programId = :programId")
    Optional<ProgramStatus> findProgramStatusByProgramId(UUID programId);


    boolean existsByProgramId(UUID programId);


}
