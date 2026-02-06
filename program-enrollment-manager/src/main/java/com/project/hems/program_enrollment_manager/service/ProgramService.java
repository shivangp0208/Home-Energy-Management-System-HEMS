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

import com.project.hems.program_enrollment_manager.config.ProgramMapper;
import com.project.hems.program_enrollment_manager.entity.ProgramConfigurationEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentAudit;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationRequestDto;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationResponseDto;
import com.project.hems.program_enrollment_manager.model.ProgramStatus;
import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollmentAuditDto;
import com.project.hems.program_enrollment_manager.model.SiteStatus;
import com.project.hems.program_enrollment_manager.repository.ProgramConfigurationRepo;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentAuditRepo;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;
import com.project.hems.program_enrollment_manager.web.exception.ProgramNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramMapper programMapper;

    //only progrm related find configuration,when program is created , what type of program it is that we see in this service 
    private final ProgramRepository programRepository;
    private final ProgramConfigurationRepo programConfigurationRepo;
    private final SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;
    private final SiteProgramEnrollmentAuditRepo siteProgramEnrollmentAuditRepo;
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

    // public void deleteProgram(UUID programId) {

    //     Optional<ProgramEntity> optionalProgram = programRepository.findById(programId);

    //     if (optionalProgram.isEmpty()) {
    //         log.error("findProgramById: unable to find program detail for given program id = " + programId);
    //         throw new ProgramNotFoundException(
    //                 "unable to find program detail for given program id = " + programId.toString());
    //     }
    
    // }

    //find All Program

    //find programById 

    //save new program

    //delete program 
    @Transactional
    public String deleteProgram(UUID programId){
        //first we find program
        ProgramEntity program = programRepository.findById(programId).orElseThrow(()-> new RuntimeException("program not found "+programId));

        //then we find program config che ke nai 
        //km ke banne delete karvu padse ek sathe 
        ProgramConfigurationEntity programConfig = programConfigurationRepo.findByProgram_programId(programId).orElseThrow(()-> new RuntimeException("program config not found for programId "+programId));;


        programConfigurationRepo.delete(programConfig);
        log.info("program config is deleted");


        programRepository.delete(program);
        log.info("program is deleted ");

        return "program is successfully deleted";
    }


    //activate program 
    public String activateProgram(@PathVariable UUID programId){
        //first we check this programId is available in database or not 
       boolean existsById = programRepository.existsById(programId);
       if(!existsById){
        throw new RuntimeException("program is not found with id "+programId);
       }
       Optional<ProgramStatus> programStatus=programRepository.findProgramStatusByProgramId(programId);

       if(programStatus.isPresent() && programStatus.get()==ProgramStatus.ACTIVE){
        //throw exception 
        throw new RuntimeException("site is already ACTIVATED");
       }
       //fetch program Entity and save new status 
       ProgramEntity programEntity = programRepository.findById(programId).get();
       programEntity.setProgramStatus(ProgramStatus.ACTIVE);

         //fetch all site which enroll in that program and one by one update that status also 
       List<SiteProgramEnrollmentEntity> allEnrollSite = siteProgramEnrollmentRepo.findByProgram_ProgramId(programId);
       allEnrollSite.forEach((siteProgramEnrollmentEntity)->{
        siteProgramEnrollmentEntity.setSiteStatus(SiteStatus.ACTIVE);
        siteProgramEnrollmentRepo.save(siteProgramEnrollmentEntity);
        log.info("siteId {} is successfully ACTIVE",siteProgramEnrollmentEntity.getSiteId());

         //have darek id mate log padiee audit table ma 

        //find siteId
        UUID siteId = siteProgramEnrollmentEntity.getSiteId();;
        //find enrollment id 
       Optional<UUID> enrollmentId = siteProgramEnrollmentRepo.findEnrollmentId(programId, siteId);
       if(!enrollmentId.isPresent()){
        throw new RuntimeException("no enrollment id is found for this programId "+ programId + "and siteId "+siteId);
       }
       log.info("first");
        SiteProgramEnrollmentAuditDto siteProgramEnrollmentAudit=SiteProgramEnrollmentAuditDto
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
        //convert dto to entity and save and return dto 
        SiteProgramEnrollmentAudit siteProgramEnrollmentAuditEntity = programMapper.toEntity(siteProgramEnrollmentAudit);;
       log.info("third");
        siteProgramEnrollmentAuditRepo.save(siteProgramEnrollmentAuditEntity);
        siteProgramEnrollmentAuditRepo.flush();
        log.info("audit is successfull saved in table");

       });
       
       programRepository.save(programEntity);

     
       return "programId "+ programId+ " is activated successfulyy";
       
    }


    //deactivate program 
    public String deactivateProgram(@PathVariable UUID programId){
        //first we check this programId is available in database or not 
       boolean existsById = programRepository.existsById(programId);
       if(!existsById){
        throw new RuntimeException("program is not found with id "+programId);
       }

        Optional<ProgramStatus> programStatus=programRepository.findProgramStatusByProgramId(programId);

       if(programStatus.isPresent() && programStatus.get()==ProgramStatus.DEACTIVATED){
        throw new RuntimeException("site is already DEACTIVATED");
       }
       //fetch program Entity and save new status 
       ProgramEntity programEntity = programRepository.findById(programId).get();
       programEntity.setProgramStatus(ProgramStatus.DEACTIVATED);
       
       //fetch all site which enroll in that program and one by one update that status also 
       List<SiteProgramEnrollmentEntity> allEnrollSite = siteProgramEnrollmentRepo.findByProgram_ProgramId(programId);
       allEnrollSite.forEach((siteProgramEnrollmentEntity)->{
        siteProgramEnrollmentEntity.setSiteStatus(SiteStatus.DEACTIVATED);
        siteProgramEnrollmentRepo.save(siteProgramEnrollmentEntity);
        log.info("siteId {} is successfully DEACTIVATED",siteProgramEnrollmentEntity.getSiteId());

        //TODO:-
        //change aa akhu je audit table ma jay che logic ene ek function ma lakhi ne ahiya function call karvu 
        //have darek id mate log padiee audit table ma 

        //find siteId
        UUID siteId = siteProgramEnrollmentEntity.getSiteId();;
        //find enrollment id 
       Optional<UUID> enrollmentId = siteProgramEnrollmentRepo.findEnrollmentId(programId, siteId);
       if(!enrollmentId.isPresent()){
        throw new RuntimeException("no enrollment id is found for this programId "+ programId + "and siteId "+siteId);
       }
        SiteProgramEnrollmentAuditDto siteProgramEnrollmentAudit=SiteProgramEnrollmentAuditDto
                                .builder()
                                .changeAt(LocalDateTime.now())
                                .enrollmentId(enrollmentId.get())
                                .oldSiteStatus(SiteStatus.ACTIVE)
                                .newSiteStatus(SiteStatus.DEACTIVATED)
                                .programId(programId)
                                .reason("Vpp decide to discountiue this program")
                                .changeBy("VPP_ADMIN")
                                .build();
        
        //convert dto to entity and save and return dto 
        SiteProgramEnrollmentAudit siteProgramEnrollmentAuditEntity = programMapper.toEntity(siteProgramEnrollmentAudit);;

        siteProgramEnrollmentAuditRepo.save(siteProgramEnrollmentAuditEntity);
        siteProgramEnrollmentAuditRepo.flush();
       log.info("audit is successfull saved in table");

       });

       programRepository.save(programEntity);

     
       return "programId "+ programId+ " is Deactivated successfulyy";
    }


    //configure-program service
    public ProgramConfigurationResponseDto configureProgram(ProgramConfigurationRequestDto dto,UUID programId){
        //first we check program is available or not 

        boolean existsById = programRepository.existsById(programId);
        if(!existsById){
            throw new RuntimeException("for configuration program is not found");
        }

        //fetch program 
        Optional<ProgramEntity> program = programRepository.findById(programId);
        ProgramConfigurationEntity programConfigurationEntity=ProgramConfigurationEntity
                                        .builder()
                                        .createdAt(LocalDateTime.now())
                                        .priority(dto.getProgramPriority())
                                        .programDescription(dto.getProgramDescription())
                                        .program(program.get())
                                        .updatedAt(LocalDateTime.now())
                                        .build();
        programConfigurationRepo.save(programConfigurationEntity);
      
        ProgramConfigurationResponseDto programConfigurationResponseDto=ProgramConfigurationResponseDto
                                         .builder()
                                         .priority(dto.getProgramPriority())
                                         .programDescription(dto.getProgramDescription())
                                         .build();


        return programConfigurationResponseDto;
    }

}
