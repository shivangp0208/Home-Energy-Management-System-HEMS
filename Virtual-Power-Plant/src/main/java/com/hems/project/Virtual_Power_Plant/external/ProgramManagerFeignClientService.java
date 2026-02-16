package com.hems.project.Virtual_Power_Plant.external;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;

import jakarta.validation.Valid;

@FeignClient(name = "program-enrollment-manager")
public interface ProgramManagerFeignClientService {

    @GetMapping("/get-all-programs")
    ResponseEntity<Program> getAllPrograms(
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    );

    @GetMapping("/get-program/{programId}")
    ResponseEntity<Program> getOneProgram(
            @PathVariable("programId") UUID programId
    );

    @PostMapping("/create-program")
    ResponseEntity<Program> createNewProgram(
            @RequestBody @Valid Program program
    );

    @PostMapping("/find-program-by-site")
    ResponseEntity<List<Program>> findProgramBySiteId(
            @RequestParam("siteId") UUID siteId
    );

    @PostMapping("/find-site-by-program")
    ResponseEntity<List<UUID>> findSiteIdByProgram(
            @RequestParam("programId") UUID programId
    );

    @PostMapping("/enroll-site-in-program")
    ResponseEntity<SiteEnrollSuccessResponse> enrollSiteInProgram(
            @RequestParam("siteId") UUID siteId,
            @RequestParam("programId") UUID programId
    );

    @PostMapping("/activate-program/{programId}")
    ResponseEntity<String> activateProgram(
            @PathVariable("programId") UUID programId
    );

    @PostMapping("/deactivate-program/{programId}")
    ResponseEntity<String> deactivateProgram(
            @PathVariable("programId") UUID programId
    );
}