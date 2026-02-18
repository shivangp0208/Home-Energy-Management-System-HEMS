package com.hems.project.Virtual_Power_Plant.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hems.project.Virtual_Power_Plant.external.SiteFeignClientService;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class SiteCreationService {

    private final SiteFeignClientService service;

    
    public ResponseEntity<List<SiteDto>> fetchAllSites(){
       return service.getAllSitesV2();
    }

        public ResponseEntity<List<OwnerDto>> fetchAllOnwer(){
          return service.getAllOwner();
        }

        public ResponseEntity<List<SiteDto>> fetchSitesByRegion(String city){
          return service.getAllSiteByRegion(city);
        }  

        public ResponseEntity<List<String>> fetchAllRegion(){
          return service.fethcAllAvailableRegion();
        }

            public ResponseEntity<Boolean> checkSiteIsAvailable(UUID siteId){
                 ResponseEntity<Boolean> booleanResponseEntity = service.checkSiteIsAvailableOtNot(siteId);
                 return booleanResponseEntity;
            }
}
