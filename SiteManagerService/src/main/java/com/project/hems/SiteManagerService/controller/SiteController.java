package com.project.hems.SiteManagerService.controller;

import com.project.hems.SiteManagerService.dto.CursorSiteResponse;
import com.project.hems.SiteManagerService.dto.SiteRequestDto;
import com.project.hems.SiteManagerService.entity.Site;
import com.project.hems.SiteManagerService.service.SiteService;
import com.project.hems.hems_api_contracts.contract.site.SiteResponseDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/site")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Site APIs", description = "Read, Update and Delete Site")
public class SiteController {

    private final SiteService siteService;

    @PostMapping("/create-site")
    public ResponseEntity<Site> createSite(
            @RequestBody SiteRequestDto siteRequestDto,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("POST req to create site with site detail = {}", siteRequestDto);

        if (jwt != null) {
            log.debug("Authenticated JWT subject={}", jwt.getSubject());
            log.debug("Authenticated JWT claims={}", jwt.getClaims());
        }

        String userSub = jwt.getSubject();
        String email = jwt.getClaim("https://hems.com/email");

        log.debug("Creating site for userSub={}, email={}", userSub, email);

        Site site = siteService.createSite(siteRequestDto, userSub);

        log.info("Site created successfully. siteId={}, userSub={}",
                site.getId(), userSub);

        return new ResponseEntity<>(site, HttpStatus.CREATED);
    }

    @GetMapping("/fetch-site-by-id/{siteId}")
    public ResponseEntity<Site> getSite(@PathVariable UUID siteId) {

        log.info("GET req to fetch site by id with siteId={}", siteId);

        Site site = siteService.fetchSiteById(siteId);

        log.debug("Fetched site details. siteId={}", siteId);

        return new ResponseEntity<>(site, HttpStatus.OK);
    }

    @GetMapping("/fetch-all-site")
    public ResponseEntity<List<Site>> getAllSites() {

        log.info("GET req to fetch all site details");

        List<Site> sites = siteService.fetchAllSite();

        log.debug("Fetched all sites. count={}", sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @GetMapping("/fetch-all-site/v2")
    public ResponseEntity<List<SiteResponseDto>> getAllSitesV2() {

        log.info("GET req to fetch all site details v2");

        List<SiteResponseDto> sites = siteService.fetchAllSiteV2();

        log.debug("Fetched all sites v2. count={}", sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    //this is normal offset based pagination
    @GetMapping("/fetch-all-site/v2/pagging")
    public ResponseEntity<Page<SiteResponseDto>> getAllSitesV2WithPagging(
        @RequestParam(name = "offset",defaultValue = "0") int offset,
        @RequestParam(name = "pageSize",defaultValue = "2") int pageSize
    ) {

        log.info("GET req to fetch all site details v2 with pagging");

       Page<SiteResponseDto> allSiteV2WithPagination = siteService.findAllSiteV2WithPagination(offset,pageSize);;
        return new ResponseEntity<>(allSiteV2WithPagination, HttpStatus.OK);
    }

    //this is normal offset based pagination
    @GetMapping("/fetch-all-site/v2/pagging-sorting")
    public ResponseEntity<Page<SiteResponseDto>> getAllSitesV2WithPaggingAndSorting(
        @RequestParam(name = "offset",defaultValue = "0") int offset,
        @RequestParam(name = "pageSize",defaultValue = "2") int pageSize,
        @RequestParam(name = "field") String field
    ) {

        log.info("GET req to fetch all site details v2 with pagging and sorting");

       Page<SiteResponseDto> allSiteV2WithPagination = siteService.findAllSiteV2WithPaginationAndSorting(offset,pageSize,field);
        return new ResponseEntity<>(allSiteV2WithPagination, HttpStatus.OK);
    }

    //here we implement cursor based pagination
      @GetMapping("/fetch-all-site/v2/cursor")
    public ResponseEntity<CursorSiteResponse<SiteResponseDto>> getAllSitesV2WithPaggingAndSorting(
        @RequestParam(required = false) UUID cursor,
        @RequestParam(defaultValue = "10") int size
    ) {

        log.info("GET req to fetch all site details v2 with pagging and sorting");

       CursorSiteResponse<SiteResponseDto> sites = siteService.getSites(cursor, size);;
        return new ResponseEntity<>(sites, HttpStatus.OK);
    }






    // @GetMapping("/fetch-all-site") // public
    // CompletableFuture<ResponseEntity<List<SiteResponseDto>>> getAllSites() // {
    // // return siteService.fetchAllSites().thenApply(ResponseEntity::ok); // }

    @GetMapping("/fetch-site-by-region/{city}")
    public ResponseEntity<List<SiteResponseDto>> getAllSiteByRegion(
            @PathVariable String city) {

        log.info("GET req to fetch site by region with city={}", city);

        List<SiteResponseDto> sites = siteService.fetchSiteByRegion(city);

        log.debug("Fetched sites by region. city={}, count={}", city, sites.size());

        return new ResponseEntity<>(sites, HttpStatus.OK);
    }

    @GetMapping("/fetch-all-region")
    public ResponseEntity<List<String>> fethcAllAvailableRegion(){
        List<String> fetchAllRegion = siteService.fetchAllRegion();
        return new ResponseEntity(fetchAllRegion,HttpStatus.OK);
    }


}
