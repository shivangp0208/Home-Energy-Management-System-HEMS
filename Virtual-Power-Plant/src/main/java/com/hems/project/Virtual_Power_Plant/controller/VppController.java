package com.hems.project.Virtual_Power_Plant.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hems.project.Virtual_Power_Plant.service.VppService;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramConfigurationRequestDto;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramConfigurationResponseDto;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramEntity;
import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollRequest;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vpp")
public class VppController {

    private final VppService vppService;

    @PostMapping("/send-requirement")
    public String sendSignalForImport(@RequestBody SignalForImport signalForImport){
    log.info("received request : send signal to import power from region = {} ",signalForImport.getRegionName());
        vppService.importPower(signalForImport);
        return "send import details to vpp service";
    }

 

    //TODO:-
    //make a controller from which vpp create program
        //here we find which site is enroll in which program
    @PostMapping("/find-program-by-site")
    public ResponseEntity<List<ProgramEntity>> findProgramBySiteId(@RequestParam UUID siteId){
        ResponseEntity<List<ProgramEntity>> programBySite = vppService.findProgramBySite(siteId);
        return programBySite;
    }

    //here we in particular program how many site is enroll
    @PostMapping("/find-site-by-program")
    public ResponseEntity<List<UUID>> findSiteIdByProgram(@RequestParam UUID programId){
        ResponseEntity<List<UUID>> siteIdByProgramId = vppService.findSiteIdByProgramId(programId);
        return siteIdByProgramId;
    }

    //here we find enroll site in particular program 
    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(
        @RequestParam UUID siteId,
        @RequestParam UUID programId
    ){
       ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram = vppService.enrollSiteinProgram(siteId, programId);
       return enrollSiteinProgram;
    }

    //vpp can update program priority and program configuration
    //and start and end data 

    @PutMapping("/update-program/{programId}")
    public ResponseEntity<ProgramConfigurationResponseDto> updateProgram(
        @RequestBody ProgramConfigurationRequestDto dto,
        @PathVariable UUID programId
    ){
        ResponseEntity<ProgramConfigurationResponseDto> updateProgram = vppService.updateProgram(dto, programId);
        return updateProgram;
    }
}
