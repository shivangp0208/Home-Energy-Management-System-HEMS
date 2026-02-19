package com.hems.project.Virtual_Power_Plant.external;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.hems.hems_api_contracts.contract.site.OwnerDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "SITE-SERVICE-HEMS")
public interface SiteFeignClientService {
  
        @GetMapping("/api/v1/owner/fetch-all-owner")
         ResponseEntity<List<OwnerDto>> getAllOwner();

         @GetMapping("/api/v1/site/fetch-all-site/v2")
        ResponseEntity<List<SiteDto>> getAllSitesV2();

          @GetMapping("/api/v1/site/fetch-site-by-region/{city}")
         ResponseEntity<List<SiteDto>> getAllSiteByRegion(@PathVariable("city") String city);

        @GetMapping("/api/v1/site/fetch-all-region")
       ResponseEntity<List<String>> fethcAllAvailableRegion();

        @PostMapping("/api/v1/site/check-site-available/{siteId}")
        ResponseEntity<Boolean> checkSiteIsAvailableOtNot(@PathVariable("siteId") UUID siteId);

}
