package com.hems.project.Virtual_Power_Plant.service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import com.hems.project.Virtual_Power_Plant.Config.VppMapper;
import com.hems.project.Virtual_Power_Plant.dto.*;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import com.hems.project.Virtual_Power_Plant.repository.VppRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
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
    private final SiteCreationService siteCreationService;
    private final VppMapper vppMapper;


    public String importPower(SignalForImport signalForImport){

        kafkaTemplate.send(vppRequirement, signalForImport);
        log.debug("importPower: vpp requirement is send to dispatch manager total site is :- "+signalForImport.getRequirement().size());
        return "successfull";
    }

    public SiteCollectionResponseDto createCollection(UUID vppId,SiteCollectionRequestDto dto){
        //fetch vpp entity
         Vpp vpp = vppRepository.findById(vppId).orElseThrow(()-> new RuntimeException("vpp is not found"));
         //check siteId actually exists ot nrt
         List<UUID> siteIds = dto.getSiteIds();
         siteIds.forEach((siteId)->{
             //fieng client no no call lagavo padse
             siteCreationService.checkSiteIsAvailable(siteId);
         });
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

            public UUID createVpp(String authUserId, String email) {
                Vpp vpp = Vpp.builder()
                        .authUserId(authUserId)
                        .email(email)
                        .name("default VPP name")
                        .region("default region")
                        .totalSolarCapacityW(0.0)
                        .lastUpdatedTime(LocalDateTime.now())
                        .totalBatteryCapacityWh(0.0)
                        .availableBatteryCapacityWh(0.0)
                        .verificationStatus(VppVerificationStatus.DRAFT)
                        .operationalStatus(VppOperationalStatus.INACTIVE)
                        .establishedTime(LocalDateTime.now())
                        .build();

                return vppRepository.save(vpp).getId();
            }


            /*
            public VppUpdateResponseDto updateVpp(String email,VppUpdateRequestDto dto){
                //fetch vpp is there or not based on email
                 Vpp vpp = vppRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("vpp not found for email " + email));


                Optional.ofNullable(dto.getName()).ifPresent(vpp::setName);

                if (vpp.getRegion() == null && dto.getRegion() != null) {
                    vpp.setRegion(dto.getRegion());
                }
                if (vpp.getCountry() == null && dto.getCountry() != null) {
                    vpp.setCountry(dto.getCountry());
                }
                if (vpp.getCity() == null && dto.getCity() != null) {
                    vpp.setCity(dto.getCity());
                }

                Optional.ofNullable(dto.getTotalSolarCapacityW()).ifPresent(vpp::setTotalSolarCapacityW);
                Optional.ofNullable(dto.getTotalBatteryCapacityWh()).ifPresent(vpp::setTotalBatteryCapacityWh);
                Optional.ofNullable(dto.getAvailableBatteryCapacityWh()).ifPresent(vpp::setAvailableBatteryCapacityWh);
                Optional.ofNullable(dto.getCurrentLiveGeneratePowerW()).ifPresent(vpp::setCurrentLiveGeneratePowerW);

                Optional.ofNullable(dto.getMaxExportPowerCapacityW()).ifPresent(vpp::setMaxExportPowerCapacityW);
                Optional.ofNullable(dto.getMaxImportPowerCapacityW()).ifPresent(vpp::setMaxImportPowerCapacityW);
                Optional.ofNullable(dto.getMaxPowerGenerationCapacityW()).ifPresent(vpp::setMaxPowerGenerationCapacityW);

                Optional.ofNullable(dto.getTotalSites()).ifPresent(vpp::setTotalSites);
                Optional.ofNullable(dto.getOperationalStatus()).ifPresent(vpp::setOperationalStatus);
                Optional.ofNullable(dto.getEstablishedTime()).ifPresent(vpp::setEstablishedTime);
                Optional.ofNullable(dto.getSiteCollection()).ifPresent(vpp::setSiteCollection);
                vpp.setLastUpdatedTime(LocalDateTime.now());

                 Vpp updatedVpp = vppRepository.save(vpp);

                 VppUpdateResponseDto response = VppUpdateResponseDto.builder()
                        .id(updatedVpp.getId())
                        .name(updatedVpp.getName())
                        .region(updatedVpp.getRegion())
                        .country(updatedVpp.getCountry())
                        .city(updatedVpp.getCity())
                        .totalSolarCapacityW(updatedVpp.getTotalSolarCapacityW())
                        .totalBatteryCapacityWh(updatedVpp.getTotalBatteryCapacityWh())
                        .availableBatteryCapacityWh(updatedVpp.getAvailableBatteryCapacityWh())
                        .currentLiveGeneratePowerW(updatedVpp.getCurrentLiveGeneratePowerW())
                        .maxExportPowerCapacityW(updatedVpp.getMaxExportPowerCapacityW())
                        .maxImportPowerCapacityW(updatedVpp.getMaxImportPowerCapacityW())
                        .maxPowerGenerationCapacityW(updatedVpp.getMaxPowerGenerationCapacityW())
                        .totalSites(updatedVpp.getTotalSites())
                        .operationalStatus(updatedVpp.getOperationalStatus())
                        .establishedTime(updatedVpp.getEstablishedTime())
                         .lastUpdatedTime(updatedVpp.getLastUpdatedTime())
                         .siteCollection(updatedVpp.getSiteCollection())
                        .build();

                return response;
            }

             */

    @Transactional
    public VppUpdateResponseDto updateVppV2(String email, VppUpdateRequestDto dto) {

        Vpp vpp = vppRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("VPP not found"));
        vppMapper.updateVppFromDto(dto, vpp);

        return vppMapper.toDto(vpp);
    }


    public boolean deleteVpp(UUID vppId) {
        if (vppRepository.existsById(vppId)) {
            vppRepository.deleteById(vppId);
            return true;
        }
        else
            return false;
    }

    public List<Vpp> getAllVpps() {
        return vppRepository.findAll();
    }

    //get all vpp and aa bhi only admin ke super admin j joi sakse


    //check is vpp is available based on id
    public void validateVppExists(UUID vppId) {
        if (!vppRepository.existsById(vppId)) {
            throw new ResourceNotFoundException("VPP not found with id: " + vppId);
        }
    }

    public void validateOwnership(UUID vppId, String email) throws AccessDeniedException {
        Vpp vpp = vppRepository.findById(vppId)
                .orElseThrow(()-> new RuntimeException("Vpp is not found with id "+vppId));

        if (!vpp.getEmail().equals(email)) {
            throw new AccessDeniedException("You cannot access this VPP");
        }
    }

    public Vpp fetchVpp(UUID vppId) {
        Vpp vpp = vppRepository.findById(vppId)
                .orElseThrow(()-> new RuntimeException("Vpp is not found with id "+vppId));

        return vpp;
    }


    //make method jena thi apde verification statyus of document and time set kari sakiee



    //TODO:-
    //future ma id na jagyaee token perthi e apde check karvu hoy toh enu e banai rakhvu



    //TODO:-
    //if user has role is POWER HOUSE toh j e vpp create update and delete ne e badhu kari sakse..
    //create vpp,update vpp details , delete vpp and all..




      


 



    
}



