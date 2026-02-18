package com.project.hems.program_enrollment_manager.util;

import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.external.SiteFeignClientService;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.web.exception.ProgramNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProgramHelperMethods {

    private final ProgramRepository programRepository;
    private final ModelMapper mapper;
    private final SiteFeignClientService siteFeignClientService;

    public Program getProgramByProgramId(@NonNull UUID programId, boolean includeSite) {
        Optional<ProgramEntity> optionalProgram = programRepository.findById(programId);

        if (optionalProgram.isEmpty()) {
            log.error("getProgramByProgramId: unable to find program detail for given program id = " + programId);
            throw new ProgramNotFoundException(
                    "unable to find program detail for given program id = " + programId.toString());
        }

        Program program = mapper.map(optionalProgram.get(), Program.class);

        if (includeSite) {
            program.setSites(
                    siteFeignClientService.getAllSitesInProgram(programId, false).getBody());
            log.debug("getProgramByProgramId: include site is true serializing sites");
        } else {
            log.debug("getProgramByProgramId: include site is false, not serializing sites");
        }

        return program;
    }

    public Program getProgramFromEntity(ProgramEntity programEntity, boolean includeSite) {
        log.debug("getProgramFromEntity: fetching program dto from entity");
        Program program = mapper.map(programEntity, Program.class);

        if (includeSite) {
            program.setSites(
                    siteFeignClientService.getAllSitesInProgram(
                            programEntity.getProgramId(), false).getBody());
            log.debug("getProgramFromEntity: include site is true serializing sites");
        } else {
            log.debug("getProgramFromEntity: include site is false, not serializing sites");
        }

        return program;
    }
}
