package com.hems.project.ADMIN_SERVICE.external;

import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "SITE-SERVICE-HEMS", path = "/api/v1/site")
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
                @RequestHeader("Authorization") String token,
                @PathVariable(name = "siteId", required = true) UUID siteId);

        @PostMapping("/check-sites-available")
        public List<UUID> verifyAllSites(@RequestHeader("Authorization") String token,
                                         @RequestBody Set<UUID> siteIds);

        @PostMapping("/sites/batch")
        public Set<SiteDto> getAllSiteDetail(
                @RequestBody Set<UUID> siteIds,
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);


        @GetMapping("/fetch-all-site-id")
        public List<UUID> getAllSiteDetail();

}
