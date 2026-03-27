package com.hems.project.virtual_power_plant.controller;

import java.util.List;
import java.util.UUID;

import com.hems.project.virtual_power_plant.dto.SiteCollectionRequestDto;
import com.hems.project.virtual_power_plant.dto.SiteCollectionResponseDto;
import com.hems.project.virtual_power_plant.service.VppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import com.hems.project.virtual_power_plant.service.SiteCreationService;
import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "VPP site controller", description = "APIs for managing sites and owners inside VPP")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/vpp")
@RestController
public class SiteController {
     private final SiteCreationService service;
     private final VppService vppService;
//
//     @Operation(summary = "Fetch all sites",
//             description = "Retrieves all registered sites from Site Manager Service via Feign client.")
//     @GetMapping("/fetch-all-site")
//     public ResponseEntity<List<SiteDto>> fetchAllSite() {
//         log.info("received request in VppService: fetch all site");
//         return service.fetchAllSites();
//     }
//     //todo:-
//     //pagination and sorting apply karvu site service ma controller che j only use karvu ahiya
//
//     @Operation(summary = "Fetch all owners",
//             description = "Retrieves all registered site owners from Site Manager Service.")
//     @GetMapping("/fetch-all-owner")
//     public ResponseEntity<List<OwnerDto>> fetchAllOwner() {
//         log.info("received request in VppService: fetch all owner");
//         return service.fetchAllOnwer();
//     }
//
//     @Operation(summary = "Health check API",
//             description = "Simple endpoint to verify that the VPP service is running.")
//     @GetMapping("/check")
//     public String getMethodName() {
//         return "checking done";
//     }
//
//     @Operation(summary = "Validate JWT token",
//             description = "Returns decoded JWT details of the authenticated user. Used to verify authentication.")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Token is valid"),
//             @ApiResponse(responseCode = "401", description = "Unauthorized / Invalid token")
//     })
//     @GetMapping("/check-token")
//     public String checkToken(@AuthenticationPrincipal Jwt jwt) {
//         return jwt.toString();
//     }
//
//
//     @Operation(summary = "Fetch sites by region",
//             description = "Retrieves all sites filtered by a specific region (city).")
//     @ApiResponse(responseCode = "200", description = "Sites fetched successfully by region")
//     @GetMapping("/fetch-site-by-region/{city}")
//     public ResponseEntity<List<SiteDto>> fetchAllSiteByRegion(@PathVariable String city) {
//         log.info("received request in VppService : fetch sites by region city={}", city);
//         return service.fetchSitesByRegion(city);
//     }
//
//     @Operation(summary = "Fetch all regions",
//             description = "Retrieves a list of all available regions (cities) where sites are registered.")
//     @ApiResponse(responseCode = "200", description = "Regions fetched successfully")
//     @GetMapping("/fetch-all-region")
//     public ResponseEntity<List<String>> fetchAllRegion() {
//         log.info("received request in VppService : fetch all regoin");
//         return service.fetchAllRegion();
//     }
//
//     //make group of site and name it to one collection
//     //take collectio vppId in requestParam and list of sideId and collectionName in request Body and return dto
//     @PostMapping("/create-collection/{vppId}")
//     public ResponseEntity<SiteCollectionResponseDto> createCollection(
//             @RequestBody SiteCollectionRequestDto dto,
//             @PathVariable("vppId") UUID vppId
//     ) {
//          SiteCollectionResponseDto collection = vppService.createCollection(vppId, dto);
//
//         return new ResponseEntity<>(collection, HttpStatus.OK);
//     }

    @GetMapping("/check/{siteId}")
     public ResponseEntity<Boolean> createCollection(
             @PathVariable("siteId") UUID siteId
     ) {

        final ResponseEntity<Boolean> response = service.checkSiteIsAvailable(siteId);
        return response;
     }





}
