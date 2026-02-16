package com.hems.project.Virtual_Power_Plant.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import com.hems.project.Virtual_Power_Plant.dto.SiteCollectionRequestDto;
import com.hems.project.Virtual_Power_Plant.dto.SiteCollectionResponseDto;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import com.hems.project.Virtual_Power_Plant.repository.VppRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.external.ProgramManagerFeignClientService;
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
    private final VppRepository vppRepository;



    public String importPower(SignalForImport signalForImport){

        kafkaTemplate.send(vppRequirement, signalForImport);
        log.debug("importPower: vpp requirement is send to dispatch manager total site is :- "+signalForImport.getRequirement().size());
        return "successfull";
    }

    public SiteCollectionResponseDto createCollection(UUID vppId,SiteCollectionRequestDto dto){
        //fetch vpp entity
         Vpp vpp = vppRepository.findById(vppId).orElseThrow(()-> new RuntimeException("vpp is not found"));
         Map<String, List<UUID>> siteCollection = vpp.getSiteCollection();

         if(siteCollection==null){
             siteCollection=new HashMap<>();
         }
         siteCollection.put(dto.getCollectionName(),dto.getSiteIds());

         vpp.setSiteCollection(siteCollection);

         vppRepository.save(vpp);
        SiteCollectionResponseDto response =SiteCollectionResponseDto.builder()
                .message("successfully create collection")
                .collectionName(dto.getCollectionName())
                .siteIds(dto.getSiteIds())
                .build();

        return response;


    }


      


 



    
}



