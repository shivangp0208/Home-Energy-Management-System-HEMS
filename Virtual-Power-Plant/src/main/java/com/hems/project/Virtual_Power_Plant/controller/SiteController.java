package com.hems.project.Virtual_Power_Plant.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "VPP site controller", description = "APIs for managing sites and owners inside VPP")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vpp")
@RestController
public class SiteController {
    private final SiteCreationService service;

    @Operation(summary = "Fetch all sites",
            description = "Retrieves all registered sites from Site Manager Service via Feign client.")
    @GetMapping("/fetch-all-site")
    public ResponseEntity<List<SiteResponseDto>> fetchAllSite(){
        log.info("received request in VppService: fetch all site");
        return service.fetchAllSites();
    }
    //todo:-
    //pagination and sorting apply karvu site service ma controller che j only use karvu ahiya

    @Operation(summary = "Fetch all owners",
            description = "Retrieves all registered site owners from Site Manager Service.")
    @GetMapping("/fetch-all-owner")
    public ResponseEntity<List<OwnerDto>> fetchAllOwner(){
        log.info("received request in VppService: fetch all owner");
        return service.fetchAllOnwer();
    }

    @Operation(summary = "Health check API",
            description = "Simple endpoint to verify that the VPP service is running.")
    @GetMapping("/check")
    public String getMethodName() {
        return "checking done";
    }

    @Operation(summary = "Validate JWT token",
            description = "Returns decoded JWT details of the authenticated user. Used to verify authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid token")
    })
     @GetMapping("/check-token")
    public String checkToken( @AuthenticationPrincipal Jwt jwt) {
        return jwt.toString();
    }


    @Operation(summary = "Fetch sites by region",
            description = "Retrieves all sites filtered by a specific region (city).")
    @ApiResponse(responseCode = "200", description = "Sites fetched successfully by region")
    @GetMapping("/fetch-site-by-region/{city}")
    public ResponseEntity<List<SiteResponseDto>> fetchAllSiteByRegion(@PathVariable String city){
        log.info("received request in VppService : fetch sites by region city={}", city);
        return service.fetchSitesByRegion(city);
    }

    @Operation(summary = "Fetch all regions",
            description = "Retrieves a list of all available regions (cities) where sites are registered.")
    @ApiResponse(responseCode = "200", description = "Regions fetched successfully")
    @GetMapping("/fetch-all-region")
    public ResponseEntity<List<String>> fetchAllRegion(){
        log.info("received request in VppService : fetch all regoin");
       return service.fetchAllRegion();
    }
    



    
    
    
}
