package com.project.hems.program_enrollment_manager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.repository.SiteProgramEnrollmentRepo;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SiteProgramEnrollmentService {

    private final SiteProgramEnrollmentRepo siteProgramEnrollmentRepo;
    
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
        return null;
    }



}
