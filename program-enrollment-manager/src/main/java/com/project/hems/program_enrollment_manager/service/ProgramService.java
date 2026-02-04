package com.project.hems.program_enrollment_manager.service;

import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.web.exception.ProgramNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ModelMapper mapper;

    public Page<Program> findAllPrograms(Pageable pageReq) {
        return programRepository.findAll(pageReq)
                .map(entity -> mapper.map(entity, Program.class));
    }

    public Program findProgramById(UUID programId) {

        Optional<ProgramEntity> optionalProgram = programRepository.findById(programId);

        if (optionalProgram.isEmpty()) {
            log.error("findProgramById: unable to find program detail for given program id = " + programId);
            throw new ProgramNotFoundException(
                    "unable to find program detail for given program id = " + programId.toString());
        }

        return mapper.map(optionalProgram.get(), Program.class);

    }

    public Program saveNewProgram(Program program) {

        ProgramEntity savedEntity = programRepository.save(mapper.map(program, ProgramEntity.class));

        return mapper.map(savedEntity, Program.class);
    }

    public void deleteProgram(UUID programId) {

        Optional<ProgramEntity> optionalProgram = programRepository.findById(programId);

        if (optionalProgram.isEmpty()) {
            log.error("findProgramById: unable to find program detail for given program id = " + programId);
            throw new ProgramNotFoundException(
                    "unable to find program detail for given program id = " + programId.toString());
        }
    }
}
