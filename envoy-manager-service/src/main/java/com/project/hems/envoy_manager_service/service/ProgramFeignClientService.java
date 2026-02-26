package com.project.hems.envoy_manager_service.service;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.hems.hems_api_contracts.contract.program.Program;

@FeignClient(name = "program-enrollment-manager")
public interface ProgramFeignClientService {

    @GetMapping("/programs/{programId}")
    public ResponseEntity<Program> getOneProgram(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @PathVariable(name = "programId", required = true) UUID programId);
}
