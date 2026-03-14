package com.project.hems.program_enrollment_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollment;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.program_enrollment_manager.service.ProgramServiceImpl;
import com.project.hems.program_enrollment_manager.service.SiteProgramEnrollmentServiceImpl;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "program controller", description = "endpoints to manage and configure programs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/program")
@Slf4j
public class ProgramController {

    private final ProgramServiceImpl programService;
    private final SiteProgramEnrollmentServiceImpl siteProgramEnrollmentService;

    @PreAuthorize("hasAuthority('admin:access')")
    @GetMapping("/programs")
    @ResponseStatus(HttpStatus.OK)
    public Page<Program> getAllPrograms(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize) {
        log.info("GET req to get all programs");
        PageRequest pageReq = PageRequest.of(pageNumber, pageSize);

        return programService.findAllPrograms(pageReq, includeSite);
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @Operation(summary = "get program by id", description = "retrieve a single program using its unique programId")
    @ApiResponse(responseCode = "200", description = "program found and returned successfully")
    @ApiResponse(responseCode = "404", description = "program with given id not found")
    @GetMapping("/programs/{programId}")
    public ResponseEntity<Program> getOneProgram(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @PathVariable(name = "programId", required = true) @NonNull UUID programId) {
        log.info("GET req to get program with id {}", programId);
        Program programById = programService.findProgramById(programId, includeSite);
        return new ResponseEntity<>(programById, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @Operation(summary = "create new program", description = "create a new program with provided details")
    @ApiResponse(responseCode = "201", description = "program created successfully")
    @PostMapping("/create-program")
    public ResponseEntity<Program> createNewProgram(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @RequestBody @Valid Program program) {
        log.info("POST req to create new program");
        Program saveNewProgram = programService.createNewProgram(program);
        return new ResponseEntity<>(saveNewProgram, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @Operation(summary = "update program configuration", description = "update configuration details of an existing program")
    @ApiResponse(responseCode = "200", description = "program configuration updated successfully")
    @PutMapping("/update-program/{programId}")
    public ResponseEntity<Program> updateProgram(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @PathVariable(name = "programId", required = true) @NonNull UUID programId,
            @RequestBody Program program) {
        log.info("PUT req to update program deatils with programId {}", programId);
        Program updatedProgram = programService.updateProgram(programId, program, includeSite);
        return new ResponseEntity<>(updatedProgram, HttpStatus.CREATED);
    }

    // here we find enroll site in particular program
    @PreAuthorize("hasAuthority('admin:access')")
    @Operation(summary = "enroll site in program", description = "enroll a site in a specific program")
    @ApiResponse(responseCode = "200", description = "site enrolled in program successfully")
    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteProgramEnrollment> enrollSiteinProgram(
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite,
            @RequestParam(name = "siteId", required = true) UUID siteId,
            @RequestParam(name = "programId", required = true) @NonNull UUID programId) {
        log.info("POST req for enrolling site with siteId = {} and program with programId = {}", siteId, programId);
        SiteProgramEnrollment enrollSiteinProgram = siteProgramEnrollmentService.enrollSiteinProgram(siteId,
                programId);
        return new ResponseEntity<>(enrollSiteinProgram, HttpStatus.OK);
    }

    // activate program
    @PreAuthorize("hasAuthority('admin:access')")
    @Operation(summary = "activate program", description = "activate a program by its programId")
    @ApiResponse(responseCode = "200", description = "program activated successfully")
    @PatchMapping("/activate-program/{programId}")
    public ResponseEntity<Program> activateProgram(
            @PathVariable(name = "programId", required = true) @NonNull UUID programId,
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite) {
        log.info("PATCH req for activate program with programId {}", programId);
        Program activatedProgram = programService.activateProgram(programId);
        return new ResponseEntity<>(activatedProgram, HttpStatus.OK);
    }

    // deactivate program
    @PreAuthorize("hasAuthority('admin:access')")
    @Operation(summary = "deactivate program", description = "deactivate a program by its programId")
    @ApiResponse(responseCode = "200", description = "program deactivated successfully")
    @PatchMapping("/deactivate-program/{programId}")
    public ResponseEntity<Program> deactivateProgram(
            @PathVariable(name = "programId", required = true) @NonNull UUID programId,
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite) {
        log.info("PATCH req to deactivate program with programId {}", programId);
        Program deactivatedProgram = programService.deactivateProgram(programId);
        return new ResponseEntity<>(deactivatedProgram, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @GetMapping("/fetch-programs-by-site/{siteId}")
    public List<Program> getAllProgramBySiteId(
            @PathVariable(name = "siteId", required = false) UUID siteId,
            @RequestParam(name = "includeSite", required = false, defaultValue = "false") boolean includeSite) {
        log.info("GET req to fetch all programs under site with siteId = " + siteId);
        return programService.findAllProgramsBySites(siteId, includeSite);
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @GetMapping("/fetch-programsIds-by-site/{siteId}")
    public List<UUID> getAllProgramIdBySiteId(
            @PathVariable(name = "siteId", required = false) UUID siteId) {
        log.info("GET req to fetch all programsId under site with siteId = " + siteId);
        return siteProgramEnrollmentService.findAllProgramIdsBySiteId(siteId);
    }

    @PreAuthorize("hasAuthority('admin:access')")
    @GetMapping("/check-program-available/{programId}")
    public boolean checkProgramIdIsAvailable(@PathVariable UUID programId) {
        log.info("GET req to check program availability under programId = " + programId);
        return programService.checkProgramIsAvailable(programId);
    }


}
