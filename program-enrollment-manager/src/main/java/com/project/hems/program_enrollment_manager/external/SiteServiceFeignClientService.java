package com.project.hems.program_enrollment_manager.external;

import com.project.hems.hems_api_contracts.contract.program.AddProgramConfigInSite;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@Service
@FeignClient(name = "SITE-SERVICE-HEMS")
public interface SiteServiceFeignClientService {

    @PostMapping("/api/v1/site/{site-id}/add-program")
     ResponseEntity<Map<UUID,String>> addPrograminSite(
            @PathVariable("site-id") UUID siteId,
            @RequestBody AddProgramConfigInSite addProgramConfigInSite);

}
