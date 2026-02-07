package com.hems.project.Virtual_Power_Plant.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.external.ProgramManagerFeignClientService;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramConfigurationRequestDto;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramConfigurationResponseDto;
import com.project.hems.hems_api_contracts.contract.program.model.ProgramEntity;
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

    private final ProgramManagerFeignClientService programManagerFeignClientService;

    
    public String importPower(SignalForImport signalForImport){
        kafkaTemplate.send(vppRequirement, signalForImport);
        log.debug("importPower: vpp requirement is send to dispatch manager total site is :- "+signalForImport.getRequirement().size());
        return "successfull";
    }


      public ResponseEntity<List<ProgramEntity>> findProgramBySite(UUID siteId ){
         ResponseEntity<List<ProgramEntity>> programBySiteId = programManagerFeignClientService.findProgramBySiteId(siteId);
        return programBySiteId;
    }


    //find which site enroll in which program 
    public ResponseEntity<List<UUID>> findSiteIdByProgramId(UUID programId){
      ResponseEntity<List<UUID>> siteIdByProgram = programManagerFeignClientService.findSiteIdByProgram(programId);
        return siteIdByProgram;
    }

    //enroll site in particular program
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(UUID siteId,UUID programId){
       ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram = programManagerFeignClientService.enrollSiteinProgram(siteId, programId);
       return enrollSiteinProgram;
    }

    public ResponseEntity<ProgramConfigurationResponseDto> updateProgram(ProgramConfigurationRequestDto dto,UUID programId){
        ResponseEntity<ProgramConfigurationResponseDto> updateProgram = programManagerFeignClientService.updateProgram(programId, dto);;
        return updateProgram;
    }




 



    
}



