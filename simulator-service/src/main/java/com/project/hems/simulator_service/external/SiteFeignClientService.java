package com.project.hems.simulator_service.external;

import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "site-manager-service", path = "/api/v1/site")
public interface SiteFeignClientService {
        @PutMapping("/update-meter-status/{siteId}")
        ResponseEntity<String> updateMeterStatus(@PathVariable("siteId") UUID siteId);
}
