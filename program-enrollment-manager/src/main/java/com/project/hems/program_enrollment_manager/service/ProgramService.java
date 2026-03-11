package com.project.hems.program_enrollment_manager.service;

import com.project.hems.hems_api_contracts.contract.program.Program;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface ProgramService {

     Page<Program> findAllPrograms(@NonNull Pageable pageReq, boolean includeSite);

     List<Program> findAllProgramsBySites(UUID siteId, boolean includeSite);

     Program findProgramById(@NonNull UUID programId, boolean includeSite);

     Program createNewProgram(@Valid Program program);

     Program updateProgram(@NonNull UUID programId, @Valid Program program, boolean includeSites);

     Program activateProgram(@PathVariable @NonNull UUID programId);

     Program deactivateProgram(@PathVariable @NonNull UUID programId);
}
