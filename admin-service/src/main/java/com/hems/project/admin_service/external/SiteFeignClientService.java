package com.hems.project.admin_service.external;

import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "SITE-SERVICE-HEMS", path = "/api/v1/site")
public interface SiteFeignClientService {

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
        public List<UUID> getAllSiteIds();



        @GetMapping("/get-all-siteId-by-meter-status")
        List<UUID> getAllSiteIdByMeterStatus(@RequestParam("flag") boolean flag);


}
