package com.project.hems.program_enrollment_manager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

@FeignClient(name = "site-service", path = "/api/v1/site")
public interface SiteFeignClientService {

    @GetMapping("/fetch-site-by-id/{siteId}")
    public ResponseEntity<SiteDto> getSite(@PathVariable(name = "siteId", required = true) UUID siteId);

    @PutMapping("/{site-id}/add-program")
    public SiteDto addPrograminSite(
            @PathVariable("site-id") UUID siteId,
            @RequestBody Program program);

    @GetMapping("/fetch-site-by-program/{programId}")
    public ResponseEntity<List<SiteDto>> getAllSitesInProgram(
            @PathVariable(name = "programId", required = true) UUID programId);
}
