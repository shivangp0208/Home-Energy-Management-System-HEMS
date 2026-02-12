package com.project.hems.program_enrollment_manager.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.AddProgramConfigInSite;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;
import com.project.hems.program_enrollment_manager.entity.ProgramConfigurationEntity;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationUpdateRequestDto;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationUpdateResponseDto;
import com.project.hems.program_enrollment_manager.model.SiteStatus;
import com.project.hems.program_enrollment_manager.repository.ProgramConfigurationRepo;
import com.project.hems.program_enrollment_manager.repository.ProgramRepository;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SiteProgramEnrollmentService {

    private final SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;
    private final ProgramRepository programRepository;
    private final ProgramConfigurationRepo programConfigurationRepo;
    private final SiteManagerService siteManagerService;

    //this service is work for checking which site enroll in which program
    //which site enroll in past which program and which date that join and which date vpp release that site 

    //find which program enroll in which site 
    public List<ProgramEntity> findProgramBySite(UUID siteId ){
        List<ProgramEntity> programsBySiteId = siteProgramEnrollmentRepo.findProgramsBySiteId(siteId);
        return programsBySiteId;
    }


    //find which site enroll in which program 
    public List<UUID> findSiteIdByProgramId(UUID programId){
        List<UUID> siteIdByProgramId = siteProgramEnrollmentRepo.findSiteIdByProgramId(programId);;
        return siteIdByProgramId;
    }


    //enroll site in particular program
    @Transactional
    public SiteEnrollSuccessResponse enrollSiteinProgram(UUID siteId,UUID programId){
        //first check is program and site is available 
        log.info("programId is {} and siteId is {} ",programId,siteId);
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(()->
                    new RuntimeException(" program is not found")
                );

        //find program config based on programId
        ProgramConfigurationEntity programConfigurationEntity=programConfigurationRepo.findByProgram_programId(programId)
                .orElseThrow(()-> new RuntimeException("program configuration is not found for programId"+programId));

    
        //now we check program start and end time ke valid che date ni range ma che ke nai
        LocalDateTime now=LocalDateTime.now();
        // LocalDateTime endDateTime = programEntity.getEndDateTime();;
         if(now.isBefore(programEntity.getStartDateTime()) ||now.isAfter(programEntity.getEndDateTime())) {
        throw new RuntimeException("Program is not active currently");
    }


        SiteProgramEnrollmentEntity siteProgramEnrollmentEntity=SiteProgramEnrollmentEntity.builder()
                                .enrollmentDate(LocalDateTime.now())
                                .program(programEntity)
                                .siteStatus(SiteStatus.ACTIVE)
                                .siteId(siteId)
                                .build();
        SiteProgramEnrollmentEntity savedEnrollmentEntity = siteProgramEnrollmentRepo.save(siteProgramEnrollmentEntity);;

        //now have ahiya site service ni method call karine site table ma pan update kari daisu
        AddProgramConfigInSite programConfig=AddProgramConfigInSite.builder()
                .programId(programId)
                .programPriority(programConfigurationEntity.getPriority())
                .programStatus(programEntity.getProgramStatus())
                .programType(programEntity.getProgramType())
                .programName(programEntity.getProgramName())
                .programDescription(programConfigurationEntity.getProgramDescription())
                .endDateTime(programEntity.getEndDateTime())
                .startDateTime(programEntity.getStartDateTime())
                .build();

        siteManagerService.addProgramInSite(siteId,programConfig);
        log.info("program is successfully add in site");

        SiteEnrollSuccessResponse siteEnrollSuccessResponse=SiteEnrollSuccessResponse.builder()
        .enrollTime(Instant.now())
        .message("successfully site "+ siteId +" enroll in program "+programId)
        .programId(programId)
        .siteId(siteId)
        .success(true)
        .build();

        return siteEnrollSuccessResponse;
    }


    public ProgramConfigurationUpdateResponseDto updateProgram(ProgramConfigurationUpdateRequestDto programConfigurationRequestDto,UUID programId) {
        //find program from programId
       ProgramEntity program = programRepository.findById(programId).orElseThrow(()-> new RuntimeException("program not found"));;
    
       //find programConfiguration from programId
       ProgramConfigurationEntity programConfig = programConfigurationRepo.findByProgram_programId(programId).orElseThrow(()->
        new RuntimeException("program Configuration not found"));
    
       Optional.ofNullable(programConfigurationRequestDto.getType()).ifPresent(program::setProgramType);
       Optional.ofNullable(programConfigurationRequestDto.getStartDateTime()).ifPresent(program::setStartDateTime);
       Optional.ofNullable(programConfigurationRequestDto.getEndDateTime()).ifPresent(program::setEndDateTime);

       Optional.ofNullable(programConfigurationRequestDto.getProgramDescription()).ifPresent(programConfig::setProgramDescription);
       Optional.ofNullable(programConfigurationRequestDto.getPriority()).ifPresent(programConfig::setPriority);
       
       //we update updatedTime of programConfiguration
       programConfig.setUpdatedAt(LocalDateTime.now());

       //save program first
       programRepository.save(program);
       log.info("updated program is saved successfully");
       programConfigurationRepo.save(programConfig);
       log.info("updated program config is saved successfully. configId = {}",program.getProgramId());

       ProgramConfigurationUpdateResponseDto programConfigurationResponseDto=ProgramConfigurationUpdateResponseDto
                                                .builder()
                                                .programId(programId)
                                                .message("program updated successfully and programId = "+programId)
                                                .build();
        return programConfigurationResponseDto;

    }
}
