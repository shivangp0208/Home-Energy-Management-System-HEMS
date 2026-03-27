package com.project.hems.site_manager_service.external;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.hems.hems_api_contracts.contract.program.Program;

@FeignClient(name = "program-enrollment-manager", path = "/api/v1/program")
public interface ProgramFeignClientService {

    @GetMapping("/fetch-programs-by-site/{siteId}")
    public List<Program> getAllProgramBySiteId(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @PathVariable(name = "siteId", required = false) UUID siteId);
}
