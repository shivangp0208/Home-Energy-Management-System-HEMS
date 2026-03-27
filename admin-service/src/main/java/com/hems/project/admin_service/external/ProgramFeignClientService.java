package com.hems.project.admin_service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "program-enrollment-manager", path = "/api/v1/program")
public interface ProgramFeignClientService {
    @GetMapping("/fetch-programsIds-by-site/{siteId}")
    public List<UUID> getAllProgramIdBySiteId(
            @PathVariable(name = "siteId", required = false) UUID siteId);

    @GetMapping("/check-program-available/{programId}")
    public boolean checkProgramIdIsAvailable(@PathVariable UUID programId);

}


