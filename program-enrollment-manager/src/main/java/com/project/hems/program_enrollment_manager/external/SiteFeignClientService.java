package com.project.hems.program_enrollment_manager.external;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

@FeignClient(name = "site-service", path = "/api/v1/site")
public interface SiteFeignClientService {

        @GetMapping("/fetch-site-by-id/{siteId}")
        public SiteDto getSite(
                        @PathVariable(name = "siteId", required = true) UUID siteId,
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);

        @PutMapping("/{siteId}/add-program")
        public SiteDto addPrograminSite(
                        @PathVariable(name = "siteId", required = true) UUID siteId,
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram,
                        @RequestBody ProgramFeignDto program);

        @GetMapping("/fetch-site-by-program/{programId}")
        public ResponseEntity<List<SiteDto>> getAllSitesInProgram(
                        @PathVariable(name = "programId", required = true) UUID programId,
                        @RequestParam(name = "includeProgram", required = false, defaultValue = "false") boolean includeProgram);
}
