package com.hems.project.Virtual_Power_Plant.external;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.hems.hems_api_contracts.contract.program.model.ProgramConfigurationRequestDto;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramConfigurationResponseDto;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramEntity;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;

@FeignClient(name = "PROGRAM-ENROLLMENT-MANAGER")
public interface ProgramManagerFeignClientService {


    @PostMapping("/find-program-by-site")
    public ResponseEntity<List<ProgramEntity>> findProgramBySiteId(@RequestParam UUID siteId);

    //here we in particular program how many site is enroll
    @PostMapping("/find-site-by-program")
    public ResponseEntity<List<UUID>> findSiteIdByProgram(@RequestParam UUID programId);

    //here we find enroll site in particular program 
    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(
        @RequestParam UUID siteId,
        @RequestParam UUID programId
    );

        @PutMapping("/update-program/{programId}")
        public ResponseEntity<ProgramConfigurationResponseDto> updateProgram(
        @PathVariable UUID programId,
        @RequestBody ProgramConfigurationRequestDto programConfigurationRequestDto);
    
} 

    
