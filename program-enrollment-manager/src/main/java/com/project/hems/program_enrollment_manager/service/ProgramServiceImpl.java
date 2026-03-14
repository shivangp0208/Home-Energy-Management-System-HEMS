package com.project.hems.program_enrollment_manager.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.ProgramStatus;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.project.hems.program_enrollment_manager.util.ProgramHelperMethods;
import com.project.hems.program_enrollment_manager.exception.ProgramNotFoundException;
import com.project.hems.program_enrollment_manager.exception.ProgramStateConflictException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final ModelMapper mapper;
    private final ProgramHelperMethods programHelperMethods;

    // find All Program
    @Override
    public Page<Program> findAllPrograms(@NonNull Pageable pageReq, boolean includeSite) {
        return programRepository.findAll(pageReq)
                .map(entity -> programHelperMethods.getProgramFromEntity(entity, includeSite));
    }

    // find All Program
    @Override
    public List<Program> findAllProgramsBySites(UUID siteId, boolean includeSite) {
        log.debug("findAllProgramsBySites: finding all program with siteId = {}", siteId);
        return programRepository.findAllProgramEnrolledBySites(siteId)
                .stream()
                .map(entity -> programHelperMethods.getProgramFromEntity(entity, includeSite))
                .toList();
    }

    // find programById
    @Override
    public Program findProgramById(@NonNull UUID programId, boolean includeSite) {
        return programHelperMethods.getProgramByProgramId(programId, includeSite);
    }

    // save new program
    @Transactional
    @Override
    public Program createNewProgram(@Valid Program program) {
        try {
            ProgramEntity entity = mapper.map(program, ProgramEntity.class);
            entity.getProgramDescription().setProgram(entity);
            ProgramEntity savedEntity = programRepository.save(entity);
            return mapper.map(savedEntity, Program.class);

        } catch (DataIntegrityViolationException ex) {
            throw new ProgramStateConflictException("program already exists with same unique fields");
        }
    }

    // update program
    @Transactional
    @Override
    public Program updateProgram(@NonNull UUID programId, @Valid Program program, boolean includeSites) {
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> {
                    log.error("unable to find program detail with program id = " + programId);
                    return new ProgramNotFoundException("unable to find program detail with program id = " + programId);
                });

        programEntity.setProgramName(program.getProgramName());
        programEntity.setStartDate(program.getStartDate());
        programEntity.setEndDate(program.getEndDate());
        programEntity.setProgramType(program.getProgramType());

        ProgramDescEntity existingDesc = programEntity.getProgramDescription();
        mapper.map(program.getProgramDescription(), existingDesc);

        programEntity.getProgramDescription().setProgram(programEntity);

        try {
            ProgramEntity savedProgram = programRepository.save(programEntity);
            log.info("updateProgram: program detail updated successfully");
            return programHelperMethods.getProgramFromEntity(savedProgram, includeSites);

        } catch (DataIntegrityViolationException ex) {
            throw new ProgramStateConflictException("Update conflict: duplicate/constraint violation");
        }

    }

    // activate program
    @Transactional
    @Override
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
    @Transactional
    @Override
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

    public boolean checkProgramIsAvailable(UUID programId) {

        Optional<ProgramEntity> program = programRepository.findById(programId);
        if (program.isEmpty()) {
            throw new RuntimeException("program not found with id " + programId);
        }

        return program.get().getProgramStatus() == ProgramStatus.ACTIVE;
    }
}