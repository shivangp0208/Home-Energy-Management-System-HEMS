package com.project.hems.program_enrollment_manager.service;

import java.util.Optional;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.hems.program_enrollment_manager.entity.ProgramDescEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.web.exception.ProgramNotFoundException;
import com.project.hems.program_enrollment_manager.web.exception.ProgramStateConflictException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ModelMapper mapper;
    private final SiteFeignClientService siteFeignClientService;

    // find All Program
    public Page<Program> findAllPrograms(@NonNull Pageable pageReq) {
        return programRepository.findAll(pageReq)
                .map(entity -> mapper.map(entity, Program.class));
    }

    // find programById
    public Program findProgramById(@NonNull UUID programId) {

        Optional<ProgramEntity> optionalProgram = programRepository.findById(programId);

        if (optionalProgram.isEmpty()) {
            log.error("findProgramById: unable to find program detail for given program id = " + programId);
            throw new ProgramNotFoundException(
                    "unable to find program detail for given program id = " + programId.toString());
        }

        Program program = mapper.map(optionalProgram.get(), Program.class);
        program.setSites(siteFeignClientService.getAllSitesInProgram(programId).getBody());
        
        return program;

    }

    // save new program
    @Transactional
    public Program createNewProgram(@Valid Program program) {

        ProgramEntity entity = mapper.map(program, ProgramEntity.class);

        entity.getProgramDescription().setProgram(entity);

        ProgramEntity savedEntity = programRepository.save(entity);

        return mapper.map(savedEntity, Program.class);
    }

    // update program
    public Program updateProgram(@NonNull UUID programId, @Valid Program program) {
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> {
                    log.error("unable to find program detail with program id = " + programId);
                    return new ProgramNotFoundException("unable to find program detail with program id = " + programId);
                });

        programEntity.setProgramName(program.getProgramName());
        programEntity.setStartDate(program.getStartDate());
        programEntity.setEndDate(program.getEndDate());
        programEntity.setProgramType(program.getProgramType());
        programEntity.setProgramDescription(mapper.map(program.getProgramDescription(), ProgramDescEntity.class));

        programEntity.getProgramDescription().setProgram(programEntity);

        ProgramEntity savedProgram = programRepository.save(programEntity);

        log.info("updateProgram: program detail updated successfully");
        return mapper.map(savedProgram, Program.class);
    }

    // activate program
    public Program activateProgram(@PathVariable @NonNull UUID programId) {
        // first we check this programId is available in database or not
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> {
                    log.error("unable to find program detail with program id = " + programId);
                    return new ProgramNotFoundException("unable to find program detail with program id = " + programId);
                });

        ProgramStatus programStatus = programEntity.getProgramStatus();
        if (programStatus != null && programStatus == ProgramStatus.ACTIVE) {
            // throw exception
            throw new ProgramStateConflictException(
                    "program already is in ACTIVE state with program id = " + programId);
        }
        // fetch program Entity and save new status
        programEntity.setProgramStatus(ProgramStatus.ACTIVE);

        ProgramEntity savedProgram = programRepository.save(programEntity);
        return mapper.map(savedProgram, Program.class);

    }

    // deactivate program
    public Program deactivateProgram(@PathVariable @NonNull UUID programId) {
        // first we check this programId is available in database or not
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> {
                    log.error("unable to find program detail with program id = " + programId);
                    return new ProgramNotFoundException("unable to find program detail with program id = " + programId);
                });

        ProgramStatus programStatus = programEntity.getProgramStatus();
        if (programStatus != null && programStatus == ProgramStatus.DEACTIVATED) {
            throw new ProgramStateConflictException(
                    "program is already DEACTIVATED state with program id = " + programId);
        }
        // fetch program Entity and save new status
        programEntity.setProgramStatus(ProgramStatus.DEACTIVATED);

        ProgramEntity savedProgram = programRepository.save(programEntity);

        return mapper.map(savedProgram, Program.class);
    }

}
