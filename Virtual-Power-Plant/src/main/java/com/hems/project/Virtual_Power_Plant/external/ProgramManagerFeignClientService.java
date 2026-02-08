package com.hems.project.Virtual_Power_Plant.external;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.program.ProgramConfigurationRequestDto;
import com.project.hems.hems_api_contracts.contract.program.ProgramConfigurationResponseDto;
import com.project.hems.hems_api_contracts.contract.program.ProgramConfigurationUpdateRequestDto;
import com.project.hems.hems_api_contracts.contract.program.ProgramConfigurationUpdateResponseDto;
import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;

import jakarta.validation.Valid;

@FeignClient(name = "PROGRAM-ENROLLMENT-MANAGER")
public interface ProgramManagerFeignClientService {

    @GetMapping("/get-all-programs")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Program> getAllPrograms(
            @RequestParam(name = "pageNumber", required = false) int pageNumber,
            @RequestParam(name = "pageSize", required = false) int pageSize);

    @GetMapping("/get-program/{programId}")
    public ResponseEntity<Program> getOneProgram(@PathVariable UUID programId);

    @PostMapping("/create-program")
    public ResponseEntity<Program> createNewProgram(@RequestBody @Valid Program program);


    @PostMapping("/configure-program/{programId}")
    public ResponseEntity<ProgramConfigurationResponseDto> configureProgram(
        @RequestBody ProgramConfigurationRequestDto dto,
        @PathVariable UUID programId
    );

    @PostMapping("/find-program-by-site")
    public ResponseEntity<List<Program>> findProgramBySiteId(@RequestParam UUID siteId);

    @PostMapping("/find-site-by-program")
    public ResponseEntity<List<UUID>> findSiteIdByProgram(@RequestParam UUID programId);

    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(
        @RequestParam UUID siteId,
        @RequestParam UUID programId
    );
    

    @PutMapping("/update-program/{programId}")
    public ResponseEntity<ProgramConfigurationUpdateResponseDto> updateProgram(
        @PathVariable UUID programId,
        @RequestBody ProgramConfigurationUpdateRequestDto programConfigurationRequestDto);


    @PostMapping("/activate-paogram/{programId}")
    public ResponseEntity<String> activateProgram(@PathVariable UUID programId);


     @PostMapping("/deactivate-paogram/{programId}")
    public ResponseEntity<String> deactivateProgram(@PathVariable UUID programId);
}

    
