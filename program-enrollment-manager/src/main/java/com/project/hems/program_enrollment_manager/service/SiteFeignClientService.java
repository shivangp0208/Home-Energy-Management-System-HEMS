package com.project.hems.program_enrollment_manager.service;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.hems.hems_api_contracts.contract.site.SiteDto;


@FeignClient(name = "site-service")
public interface SiteFeignClientService {

    @GetMapping("/fetch-site-by-id/{siteId}")
    public ResponseEntity<SiteDto> getSite(@PathVariable(name = "siteId") UUID siteId);
}
