package com.hems.project.Virtual_Power_Plant.external;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "site-service", path = "/api/v1/site")
public interface SiteFeignClientService {

        // @GetMapping("/fetch-all-owner")
        // public ResponseEntity<List<OwnerDto>> getAllOwner();

        @GetMapping("/fetch-site-by-id/{siteId}")
        public SiteDto getSite(
                        @PathVariable(name = "siteId", required = true) UUID siteId,
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);

        @GetMapping("/fetch-all-site")
        public ResponseEntity<List<SiteDto>> getAllSites(
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);

        @GetMapping("/fetch-site-by-region/{city}")
        public ResponseEntity<List<SiteDto>> getAllSiteByRegion(
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
                        @PathVariable(name = "city", required = true) String city);

        @GetMapping("/fetch-all-region")
        public ResponseEntity<List<String>> fethcAllAvailableRegion(
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);

        @PostMapping("/check-site-available/{siteId}")
        public ResponseEntity<Boolean> checkSiteIsAvailableOtNot(
                        @PathVariable(name = "siteId", required = true) UUID siteId);

        @PostMapping("/check-sites-available")
        public List<UUID> verifyAllSites(@RequestBody Set<UUID> siteIds);

        @PostMapping("/sites/batch")
        public Set<SiteDto> getAllSiteDetail(
                        @RequestBody Set<UUID> siteIds,
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);
}
