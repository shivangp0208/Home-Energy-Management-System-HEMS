package com.hems.project.Virtual_Power_Plant.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.external.SiteFeignClientService;
import com.hems.project.hems_api_contracts.contract.site.OwnerDto;
import com.hems.project.hems_api_contracts.contract.site.SiteResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteCreationService {

    private final SiteFeignClientService service;

    
    public ResponseEntity<List<SiteResponseDto>> fetchAllSites(){
       return service.getAllSitesV2();
    }

        public ResponseEntity<List<OwnerDto>> fetchAllOnwer(){
          return service.getAllOwner();
        }

        public ResponseEntity<List<SiteResponseDto>> fetchSitesByRegion(String city){
          return service.getAllSiteByRegion(city);
        }  
}
