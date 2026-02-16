package com.project.hems.program_enrollment_manager.service;

import com.project.hems.hems_api_contracts.contract.program.AddProgramConfigInSite;
import com.project.hems.program_enrollment_manager.external.SiteServiceFeignClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@Service
public class SiteManagerService {
    @Autowired
    private SiteServiceFeignClientService siteServiceFeignClientService;

    public ResponseEntity<Map<UUID,String>> addProgramInSite(
            @PathVariable("site-id") UUID siteId,
            @RequestBody AddProgramConfigInSite addProgramConfigInSite)
    {
        ResponseEntity<Map<UUID,String>> stringMap=siteServiceFeignClientService.addPrograminSite(siteId,addProgramConfigInSite);
        return stringMap;

    }

}
