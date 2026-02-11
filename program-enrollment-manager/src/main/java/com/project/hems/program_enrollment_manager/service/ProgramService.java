package com.project.hems.program_enrollment_manager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.hems.program_enrollment_manager.entity.ProgramDescEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentAudit;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.model.ProgramStatus;
import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollmentAuditDto;
import com.project.hems.program_enrollment_manager.model.SiteStatus;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentAuditRepo;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;
import com.project.hems.program_enrollment_manager.web.exception.ProgramNotFoundException;
import com.project.hems.program_enrollment_manager.web.exception.ProgramStateConflictException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    // only progrm related find configuration,when program is created , what type of
    // program it is that we see in this service
    private final ProgramRepository programRepository;
    private final SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;
    private final SiteProgramEnrollmentAuditRepo siteProgramEnrollmentAuditRepo;
    private final ModelMapper mapper;

    // find All Program
    public Page<Program> findAllPrograms(Pageable pageReq) {
        return programRepository.findAll(pageReq)
                .map(entity -> mapper.map(entity, Program.class));
    }

    // find programById
    public Program findProgramById(UUID programId) {

        Optional<ProgramEntity> optionalProgram = programRepository.findById(programId);

        if (optionalProgram.isEmpty()) {
            log.error("findProgramById: unable to find program detail for given program id = " + programId);
            throw new ProgramNotFoundException(
                    "unable to find program detail for given program id = " + programId.toString());
        }

        return mapper.map(optionalProgram.get(), Program.class);

    }

    // save new program
    public Program createNewProgram(@Valid Program program) {

        ProgramEntity entity = mapper.map(program, ProgramEntity.class);

        entity.getProgramDescription().setProgram(entity);

        ProgramEntity savedEntity = programRepository.save(entity);

        return mapper.map(savedEntity, Program.class);
    }

    // delete program
    @Transactional
    public void deleteProgram(UUID programId) {
        // first we find program
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> {
                    log.error("unable to find program detail with program id = " + programId);
                    return new ProgramNotFoundException("unable to find program detail with program id = " + programId);
                });

        programRepository.delete(programEntity);
        log.info("deleteProgram: program deleted successfully with program id = " + programId);
    }

    // update program
    public Program updateProgram(UUID programId, @Valid Program program) {
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
    public Program activateProgram(@PathVariable UUID programId) {
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

        // TODO: there is no need to change the program status in each site it will be reflected automatically
        // fetch all site which enroll in that program and one by one update that status
        // also
        List<SiteProgramEnrollmentEntity> allEnrollSite = siteProgramEnrollmentRepo.findByProgram_ProgramId(programId);
        allEnrollSite.forEach((siteProgramEnrollmentEntity) -> {
            siteProgramEnrollmentEntity.setSiteStatus(SiteStatus.ACTIVE);
            siteProgramEnrollmentRepo.save(siteProgramEnrollmentEntity);
            log.info("siteId {} is successfully ACTIVE", siteProgramEnrollmentEntity.getSiteId());

            // have darek id mate log padiee audit table ma

            // find siteId
            UUID siteId = siteProgramEnrollmentEntity.getSiteId();
            // find enrollment id
            Optional<UUID> enrollmentId = siteProgramEnrollmentRepo.findEnrollmentId(programId, siteId);
            if (!enrollmentId.isPresent()) {
                throw new RuntimeException(
                        "no enrollment id is found for this programId " + programId + "and siteId " + siteId);
            }
            log.info("first");
            SiteProgramEnrollmentAuditDto siteProgramEnrollmentAudit = SiteProgramEnrollmentAuditDto
                    .builder()
                    .changeAt(LocalDateTime.now())
                    .enrollmentId(enrollmentId.get())
                    .oldSiteStatus(SiteStatus.DEACTIVATED)
                    .newSiteStatus(SiteStatus.ACTIVE)
                    .programId(programId)
                    .reason("Vpp decide to continue this program")
                    .changeBy("VPP_ADMIN")
                    .build();
            log.info("second");
            // convert dto to entity and save and return dto
            SiteProgramEnrollmentAudit siteProgramEnrollmentAuditEntity = mapper.map(siteProgramEnrollmentAudit,
                    SiteProgramEnrollmentAudit.class);
            log.info("third");
            siteProgramEnrollmentAuditRepo.save(siteProgramEnrollmentAuditEntity);
            siteProgramEnrollmentAuditRepo.flush();
            log.info("audit is successfull saved in table");

        });

        ProgramEntity savedProgram = programRepository.save(programEntity);
        return mapper.map(savedProgram, Program.class);

    }

    // deactivate program
    public Program deactivateProgram(@PathVariable UUID programId) {
        // first we check this programId is available in database or not
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(
                () -> {
                    log.error("unable to find program detail with program id = " + programId);
                    return new ProgramNotFoundException("unable to find program detail with program id = " + programId);
                });

        ProgramStatus programStatus = programEntity.getProgramStatus();
        if (programStatus != null && programStatus == ProgramStatus.DEACTIVATED) {
            throw new ProgramStateConflictException("site is already DEACTIVATED");
        }
        // fetch program Entity and save new status
        programEntity.setProgramStatus(ProgramStatus.DEACTIVATED);

        // fetch all site which enroll in that program and one by one update that status
        // also
        List<SiteProgramEnrollmentEntity> allEnrollSite = siteProgramEnrollmentRepo.findByProgram_ProgramId(programId);
        allEnrollSite.forEach((siteProgramEnrollmentEntity) -> {
            siteProgramEnrollmentEntity.setSiteStatus(SiteStatus.DEACTIVATED);
            siteProgramEnrollmentRepo.save(siteProgramEnrollmentEntity);
            log.info("siteId {} is successfully DEACTIVATED", siteProgramEnrollmentEntity.getSiteId());

            // TODO:-
            // change aa akhu je audit table ma jay che logic ene ek function ma lakhi ne
            // ahiya function call karvu
            // have darek id mate log padiee audit table ma

            // find siteId
            UUID siteId = siteProgramEnrollmentEntity.getSiteId();
            // find enrollment id
            Optional<UUID> enrollmentId = siteProgramEnrollmentRepo.findEnrollmentId(programId, siteId);
            if (!enrollmentId.isPresent()) {
                throw new RuntimeException(
                        "no enrollment id is found for this programId " + programId + "and siteId " + siteId);
            }
            SiteProgramEnrollmentAuditDto siteProgramEnrollmentAudit = SiteProgramEnrollmentAuditDto
                    .builder()
                    .changeAt(LocalDateTime.now())
                    .enrollmentId(enrollmentId.get())
                    .oldSiteStatus(SiteStatus.ACTIVE)
                    .newSiteStatus(SiteStatus.DEACTIVATED)
                    .programId(programId)
                    .reason("Vpp decide to discountiue this program")
                    .changeBy("VPP_ADMIN")
                    .build();

            // convert dto to entity and save and return dto
            SiteProgramEnrollmentAudit siteProgramEnrollmentAuditEntity = mapper.map(siteProgramEnrollmentAudit,
                    SiteProgramEnrollmentAudit.class);

            siteProgramEnrollmentAuditRepo.save(siteProgramEnrollmentAuditEntity);
            siteProgramEnrollmentAuditRepo.flush();
            log.info("audit is successfull saved in table");

        });

        ProgramEntity savedProgram = programRepository.save(programEntity);

        return mapper.map(savedProgram, Program.class);
    }

}
