package com.project.hems.program_enrollment_manager.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.model.ProgramType;
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
    public SiteEnrollSuccessResponse enrollSiteinProgram(UUID siteId,UUID programId){
        //first check is program and site is available 
        log.info("programId is {} and siteId is {} ",programId,siteId);
        ProgramEntity programEntity = programRepository.findById(programId).orElseThrow(()->
                    new RuntimeException(" program is not found")
                );
    
        //now we check program start and end time ke valid che date ni range ma che ke nai
        LocalDateTime now=LocalDateTime.now();
        // LocalDateTime endDateTime = programEntity.getEndDateTime();;
         if(now.isBefore(programEntity.getStartDateTime()) ||now.isAfter(programEntity.getEndDateTime())) {
        throw new RuntimeException("Program is not active currently");
    }


        SiteProgramEnrollmentEntity siteProgramEnrollmentEntity=SiteProgramEnrollmentEntity.builder()
                                .enrollmentDate(LocalDateTime.now())
                                .program(programEntity)
                                .status("ACTIVE")
                                .siteId(siteId)
                                .build();
        SiteProgramEnrollmentEntity savedEnrollmentEntity = siteProgramEnrollmentRepo.save(siteProgramEnrollmentEntity);;

        SiteEnrollSuccessResponse siteEnrollSuccessResponse=SiteEnrollSuccessResponse.builder()
        .enrollTime(Instant.now())
        .message("successfully site "+ siteId +" enroll in program "+programId)
        .programId(programId)
        .siteId(siteId)
        .success(true)
        .build();

        return siteEnrollSuccessResponse;
    }
}
