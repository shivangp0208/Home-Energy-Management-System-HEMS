package com.hems.project.Virtual_Power_Plant.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hems.project.Virtual_Power_Plant.service.VppService;
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
    //add controller to enroll site in parogram 
    @PostMapping("/enroll-site")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteInPorgram(@RequestBody SiteEnrollRequest siteProgramDto){
        //first check in program service whether site is already enroll in this program or not 
        SiteEnrollSuccessResponse siteEnrollSuccessResponse= vppService.enrollSiteInProgram(siteProgramDto);
        return ResponseEntity.ok(siteEnrollSuccessResponse);
    }
    

}
