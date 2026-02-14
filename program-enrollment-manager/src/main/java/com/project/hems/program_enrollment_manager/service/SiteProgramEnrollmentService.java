package com.project.hems.program_enrollment_manager.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollment;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;
import com.project.hems.program_enrollment_manager.web.exception.ProgramExpiredException;
import com.project.hems.program_enrollment_manager.web.exception.ProgramNotFoundException;
import com.project.hems.program_enrollment_manager.web.exception.SiteAlreadyEnroledException;
import com.project.hems.program_enrollment_manager.web.exception.SiteNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiteProgramEnrollmentService {

    private final SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;
    private final ProgramRepository programRepository;
    private final SiteFeignClientService siteFeignClientService;
    private final ModelMapper mapper;

    // this service is work for checking which site enroll in which program
    // which site enroll in past which program and which date that join and which
    // date vpp release that site

    // find which program enroll in which site
    // public List<Program> findProgramBySite(UUID siteId) {
    // List<ProgramEntity> programsBySiteId =
    // siteProgramEnrollmentRepo.findProgramsBySiteId(siteId);
    // return programsBySiteId.stream()
    // .map(entity -> mapper.map(entity, Program.class))
    // .toList();
    // }

    // find which site enroll in which program
    public Program findSiteIdByProgramId(UUID programId) {
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> new ProgramNotFoundException("unable to find program detail with program detail = " + programId));

        Program program = mapper.map(programEntity, Program.class);
        program.getSites().forEach(site -> {
            site = siteFeignClientService.getSite(site.getSiteId()).getBody();
        });

        return program;
    }

    // TODO: implement the logic for checking program conflict for a site
    // enroll site in particular program
    @Transactional
    public SiteProgramEnrollment enrollSiteinProgram(UUID siteId, @NonNull UUID programId) {
        // first check is program and site is available
        log.info("programId is {} and siteId is {} ", programId, siteId);

        ProgramEntity programEntity = programRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException(
                        "unable to find program detail for program id = " + programId));

        if (siteProgramEnrollmentRepo
                .existsBySiteAndProgram(siteId, programEntity)) {
            log.error("enrollSiteinProgram: site with site id " + siteId + " is already enroled in program "
                    + programId);
            throw new SiteAlreadyEnroledException(
                    "site with site id " + siteId + " is already enroled in program " + programId);
        }

        @Valid
        SiteDto siteDto = siteFeignClientService.getSite(siteId).getBody();
        if (siteDto == null) {
            log.error("enrollSiteinProgram unable to find site detail with site id = " + siteId);
            throw new SiteNotFoundException("unable to find site detail with site id = " + siteId);
        }

        // now we check program start and end time ke valid che date ni range ma che ke
        // nai
        if (LocalDate.now().isAfter(programEntity.getEndDate())) {
            throw new ProgramExpiredException(
                    "program with program id " + programId + " had expired on " + programEntity.getEndDate());
        }

        SiteProgramEnrollmentEntity enrolmentEntity = new SiteProgramEnrollmentEntity();
        enrolmentEntity.setProgram(programEntity);
        enrolmentEntity.setSite(siteDto.getSiteId());

        SiteProgramEnrollmentEntity savedEntity = siteProgramEnrollmentRepo.save(enrolmentEntity);
        SiteProgramEnrollment savedEnrolment = mapper.map(savedEntity, SiteProgramEnrollment.class);
        savedEnrolment.setSite(siteDto);

        siteFeignClientService.addPrograminSite(siteId, mapper.map(programEntity, Program.class));
        return savedEnrolment;
    }
}
