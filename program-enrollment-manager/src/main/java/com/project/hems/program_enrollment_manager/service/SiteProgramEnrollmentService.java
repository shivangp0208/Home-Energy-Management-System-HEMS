package com.project.hems.program_enrollment_manager.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
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
    public List<Program> findProgramBySite(UUID siteId) {
        List<ProgramEntity> programsBySiteId = siteProgramEnrollmentRepo.findProgramsBySiteId(siteId);
        return programsBySiteId.stream()
                .map(entity -> mapper.map(entity, Program.class))
                .toList();
    }

    // find which site enroll in which program
    public List<UUID> findSiteIdByProgramId(UUID programId) {
        List<UUID> siteIdByProgramId = siteProgramEnrollmentRepo.findSiteIdByProgramId(programId);
        return siteIdByProgramId;
    }

    // TODO: implement the logic for checking program conflict for a site
    // enroll site in particular program
    public SiteProgramEnrollment enrollSiteinProgram(UUID siteId, @NonNull UUID programId) {
        // first check is program and site is available
        log.info("programId is {} and siteId is {} ", programId, siteId);
        ProgramEntity programEntity = programRepository.findById(programId)
                .orElseThrow(() -> new ProgramNotFoundException(
                        "unable to find program detail for program id = " + programId));

        SiteDto siteEntity = siteFeignClientService.getSite(siteId).getBody();
        if (siteEntity == null) {
            throw new SiteNotFoundException("unable to find site detail with site id = " + siteId);
        }

        // now we check program start and end time ke valid che date ni range ma che ke
        // nai
        if (LocalDate.now().isAfter(programEntity.getEndDate())) {
            throw new ProgramExpiredException(
                    "program with program id " + programId + " had expired on " + programEntity.getEndDate());
        }

        SiteProgramEnrollment siteProgramEnrollment = SiteProgramEnrollment.builder()
                .program(mapper.map(programEntity, Program.class))
                .site(siteEntity)
                .build();

        SiteProgramEnrollmentEntity savedEnrollmentEntity = siteProgramEnrollmentRepo
                .save(mapper.map(siteProgramEnrollment, SiteProgramEnrollmentEntity.class));

        return mapper.map(savedEnrollmentEntity, SiteProgramEnrollment.class);
    }
}
