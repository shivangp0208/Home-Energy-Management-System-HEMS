package com.project.hems.program_enrollment_manager.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;

import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.external.SiteFeignClientService;
import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollment;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;
import com.project.hems.program_enrollment_manager.exception.ProgramExpiredException;
import com.project.hems.program_enrollment_manager.exception.ProgramNotFoundException;
import com.project.hems.program_enrollment_manager.exception.ProgramStateConflictException;
import com.project.hems.program_enrollment_manager.exception.SiteAlreadyEnroledException;
import com.project.hems.program_enrollment_manager.exception.SiteNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiteProgramEnrollmentServiceImpl implements SiteProgramEnrollmentService {

        private final SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;
        private final ProgramRepository programRepository;
        private final SiteFeignClientService siteFeignClientService;
        private final ModelMapper mapper;

        // this service is work for checking which site enroll in which program
        // which site enroll in past which program and which date that join and which
        // date vpp release that site

        @Transactional
        public SiteProgramEnrollment enrollSiteinProgram(UUID siteId, @NonNull UUID programId) {

                log.info("enrollSiteinProgram: START | programId={} siteId={}", programId, siteId);

                // ---- Fetch Program ----
                ProgramEntity programEntity = programRepository.findById(programId)
                                .orElseThrow(() -> {
                                        log.error("enrollSiteinProgram: Program not found | programId={}", programId);
                                        return new ProgramNotFoundException(
                                                        "unable to find program detail for program id = " + programId);
                                });

                log.debug("enrollSiteinProgram: Program found | programId={} startDate={} endDate={}",
                                programId, programEntity.getStartDate(), programEntity.getEndDate());

                // ---- Check existing enrollment ----
                if (siteProgramEnrollmentRepo.existsBySiteAndProgram(siteId, programEntity)) {
                        log.warn(
                                        "enrollSiteinProgram: Enrollment already exists | siteId={} programId={}",
                                        siteId, programId);

                        throw new SiteAlreadyEnroledException(
                                        "site with site id " + siteId + " is already enroled in program " + programId);
                }

                log.debug("enrollSiteinProgram: No existing enrollment found | siteId={} programId={}",
                                siteId, programId);

                // ---- Fetch Site from Site Service ----
                SiteDto siteDto = siteFeignClientService.getSite(siteId, false);

                if (siteDto == null) {
                        log.error("enrollSiteinProgram: Site not found from site service | siteId={}", siteId);
                        throw new SiteNotFoundException(
                                        "unable to find site detail with site id = " + siteId);
                }

                log.debug("enrollSiteinProgram: Site fetched successfully | siteId={}", siteId);

                // ---- Validate Program Expiry ----
                if (LocalDate.now().isAfter(programEntity.getEndDate())) {
                        log.warn(
                                        "enrollSiteinProgram: Program expired | programId={} expiredOn={}",
                                        programId, programEntity.getEndDate());

                        throw new ProgramExpiredException(
                                        "program with program id " + programId
                                                        + " had expired on " + programEntity.getEndDate());
                }

                log.debug("enrollSiteinProgram: Program is active | programId={}", programId);

                // ---- Check for Program Date Conflicts ----
                log.debug("enrollSiteinProgram: Checking for date conflicts | siteId={} newStartDate={} newEndDate={}",
                                siteId, programEntity.getStartDate(), programEntity.getEndDate());

                List<SiteProgramEnrollmentEntity> siteEnrolledInProgram = siteProgramEnrollmentRepo
                                .findBySite(siteId);

                boolean isProgramConflictPresent = siteEnrolledInProgram
                                .stream()
                                .filter(enrollmentEntity -> {
                                        LocalDate startDateProgramA = programEntity.getStartDate();
                                        LocalDate endDateProgramA = programEntity.getEndDate();
                                        LocalDate startDateProgramB = enrollmentEntity.getProgram().getStartDate();
                                        LocalDate endDateProgramB = enrollmentEntity.getProgram().getEndDate();

                                        return ((startDateProgramA.isBefore(endDateProgramB)
                                                        || startDateProgramA.isEqual(endDateProgramB))
                                                        && (endDateProgramA.isAfter(startDateProgramB)
                                                                        || endDateProgramA.isEqual(startDateProgramB)));
                                })
                                .findAny()
                                .isPresent();

                if (isProgramConflictPresent) {
                        log.error("enrollSiteinProgram: Date conflict detected! Site is already enrolled in a program during this timeframe | siteId={}",
                                        siteId);
                        throw new ProgramStateConflictException(
                                        "Cannot enroll site " + siteId + ". The dates " + programEntity.getStartDate() +
                                                        " to " + programEntity.getEndDate()
                                                        + " overlap with an existing program enrollment.");
                }

                // ---- Persist Enrollment ----
                SiteProgramEnrollmentEntity enrolmentEntity = new SiteProgramEnrollmentEntity();
                enrolmentEntity.setProgram(programEntity);
                enrolmentEntity.setSite(siteDto.getSiteId());

                SiteProgramEnrollmentEntity savedEntity;
                try {
                        savedEntity = siteProgramEnrollmentRepo.save(enrolmentEntity);
                } catch (DataIntegrityViolationException ex) {
                        // because if two thread same time e avse and jose ke site enroll nathi program
                        // ma
                        // existsBySiteAndProgram with this method so false return karse and banne
                        // thread save karse ..
                        // toh ene save karse so race condition na thayy etle we catch..
                        log.warn("race condition: site already enrolled | siteId={} programId={}", siteId, programId);
                        throw new SiteAlreadyEnroledException(
                                        "site with site id " + siteId + " is already enrolled in program " + programId);
                }

                log.info(
                                "enrollSiteinProgram: Enrollment saved | enrollmentId={} siteId={} programId={}",
                                savedEntity.getSite(), siteId, programId);

                // ---- Map response ----
                SiteProgramEnrollment savedEnrolment = mapper.map(savedEntity, SiteProgramEnrollment.class);
                savedEnrolment.setSite(siteDto);

                // ---- Update Site Service ----
                log.debug(
                                "enrollSiteinProgram: Updating site service with program | siteId={} programId={}",
                                siteId, programId);

                siteFeignClientService.addPrograminSite(
                                siteId,
                                false,
                                mapper.map(programEntity, ProgramFeignDto.class));

                log.info(
                                "enrollSiteinProgram: END | enrollment successful | siteId={} programId={}",
                                siteId, programId);

                return savedEnrolment;
        }
}
