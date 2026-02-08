package com.hems.project.Virtual_Power_Plant.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import com.hems.project.Virtual_Power_Plant.service.SiteCreationService;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteResponseDto;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vpp")
@RestController
public class SiteController {
    private final SiteCreationService service;
    
    @GetMapping("/fetch-all-site")
    public ResponseEntity<List<SiteResponseDto>> fetchAllSite(){
        log.info("received request in VppService: fetch all site");
        return service.fetchAllSites();
    }
    //todo:-
    //pagination and sorting apply karvu site service ma controller che j only use karvu ahiya


    @GetMapping("/fetch-all-owner")
    public ResponseEntity<List<OwnerDto>> fetchAllOwner(){
        log.info("received request in VppService: fetch all owner");
        return service.fetchAllOnwer();
    }

    @GetMapping("/check")
    public String getMethodName() {
        return "checking done";
    }

     @GetMapping("/check-token")
    public String checkToken( @AuthenticationPrincipal Jwt jwt) {
        return jwt.toString();
    }

    @GetMapping("/fetch-site-by-region/{city}")
    public ResponseEntity<List<SiteResponseDto>> fetchAllSiteByRegion(@PathVariable String city){
        log.info("received request in VppService : fetch sites by region city={}", city);
        return service.fetchSitesByRegion(city);
    }

    @GetMapping("/fetch-all-region")
    public ResponseEntity<List<String>> fetchAllRegion(){
        log.info("received request in VppService : fetch all regoin");
       return service.fetchAllRegion();
    }
    



    
    
    
}
