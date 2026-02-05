package com.hems.project.Virtual_Power_Plant.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.vpp.SignalForImport;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollRequest;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VppService {

    private final KafkaTemplate<String,SignalForImport> kafkaTemplate;
    @Value("${property.config.kafka.vpp-service-topic}")
    public String vppRequirement;

    
    public String importPower(SignalForImport signalForImport){
        kafkaTemplate.send(vppRequirement, signalForImport);
        log.debug("importPower: vpp requirement is send to dispatch manager total site is :- "+signalForImport.getRequirement().size());
        return "successfull";
    }


 

    public SiteEnrollSuccessResponse enrollSiteInProgram(SiteEnrollRequest siteProgramDto){
     SiteEnrollSuccessResponse siteEnrollSuccessResponse=SiteEnrollSuccessResponse.builder()
        .success(true)
        .message("site enroll successfully")
        .siteEnrollRequest(Map.of(
            "siteId",siteProgramDto.getSiteId(),
            "programId",siteProgramDto.getProgramId()
        ))
        .enrollTime(Instant.now())
        .build();

        return siteEnrollSuccessResponse;
    }


    
}



