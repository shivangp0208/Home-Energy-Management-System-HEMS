package com.project.hems.program_enrollment_manager.controller;

import com.hems.excel_module.model.ExcelImportResult;
import com.hems.excel_module.service.ExcelExportService;
import com.hems.excel_module.service.ExcelImportService;
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
import java.util.Map;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "program controller", description = "endpoints to manage and configure programs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/program")
@Slf4j
public class ProgramController {

    private final ProgramServiceImpl programService;
    private final SiteProgramEnrollmentServiceImpl siteProgramEnrollmentService;
    private final ExcelImportService excelImportService;

    @Operation(summary = "get all programs", description = "fetch all programs with pagination")
    @ApiResponse(responseCode = "200", description = "program list fetched successfully")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/programs")
    public ResponseEntity<Page<Program>> getAllPrograms(
            @RequestParam(defaultValue = "false") boolean includeSite,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {

        log.info("received request to fetch all programs page {} size {}", pageNumber, pageSize);

        try {
            PageRequest pageReq = PageRequest.of(pageNumber, pageSize);
            Page<Program> programs = programService.findAllPrograms(pageReq, includeSite);

            log.info("fetched {} programs", programs.getTotalElements());

            return ResponseEntity.ok(programs);

        } catch (Exception e) {
            log.error("error fetching programs: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PreAuthorize("hasAuthority('admin:read')")
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

    @PreAuthorize("hasAuthority('admin:write')")
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

    @PreAuthorize("hasAuthority('admin:write')")
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
    @PreAuthorize("hasAuthority('admin:write')")
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
    @PreAuthorize("hasAuthority('admin:write')")
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
    @PreAuthorize("hasAuthority('admin:write')")
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

    @Operation(
            summary = "get programs by site",
            description = "fetch all programs associated with a given site id"
    )
    @ApiResponse(responseCode = "200", description = "programs fetched successfully")
    @ApiResponse(responseCode = "500", description = "internal server error")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/fetch-programs-by-site/{siteId}")
    public ResponseEntity<List<Program>> getAllProgramBySiteId(
            @PathVariable UUID siteId,
            @RequestParam(defaultValue = "false") boolean includeSite) {

        log.info("received request to fetch programs for siteId {}", siteId);

        try {
            List<Program> programs = programService.findAllProgramsBySites(siteId, includeSite);

            log.info("fetched {} programs for siteId {}", programs.size(), siteId);

            return ResponseEntity.ok(programs);

        } catch (Exception e) {
            log.error("error fetching programs for siteId {}: {}", siteId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
            summary = "get program ids by site",
            description = "fetch all program ids associated with a given site id"
    )
    @ApiResponse(responseCode = "200", description = "program ids fetched successfully")
    @ApiResponse(responseCode = "500", description = "internal server error")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/fetch-programsIds-by-site/{siteId}")
    public ResponseEntity<List<UUID>> getAllProgramIdBySiteId(
            @PathVariable UUID siteId) {

        log.info("received request to fetch program ids for siteId {}", siteId);

        try {
            List<UUID> programIds = siteProgramEnrollmentService.findAllProgramIdsBySiteId(siteId);

            log.info("fetched {} program ids for siteId {}", programIds.size(), siteId);

            return ResponseEntity.ok(programIds);

        } catch (Exception e) {
            log.error("error fetching program ids for siteId {}: {}", siteId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "check program availability", description = "check if a program exists")
    @ApiResponse(responseCode = "200", description = "availability checked")
    @GetMapping("/check-program-available/{programId}")
    public ResponseEntity<Boolean> checkProgramIdIsAvailable(@PathVariable UUID programId) {

        log.info("checking program availability for programId {}", programId);

        try {
            boolean available = programService.checkProgramIsAvailable(programId);
            return ResponseEntity.ok(available);

        } catch (Exception e) {
            log.error("error checking program availability {}: {}", programId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "bulk enroll sites", description = "bulk enroll sites into programs using excel file")
    @ApiResponse(responseCode = "200", description = "bulk enrollment completed")
    @ApiResponse(responseCode = "400", description = "invalid file data")
    @PreAuthorize("hasAuthority('admin:write')")
    @PostMapping("/bulk-enroll")
    public ResponseEntity<String> bulkEnroll(@RequestParam("file") MultipartFile file) {

        log.info("received bulk enroll request");

        try {
            ExcelImportResult result = excelImportService.importFromExcel(file);

            if (!result.isSuccess()) {
                log.warn("bulk enroll failed due to validation errors");
                return ResponseEntity.badRequest().body(result.getErrors().toString());
            }

            for (Map<String, Object> row : result.getData()) {

                UUID siteId = UUID.fromString(row.get("siteId").toString());
                UUID programId = UUID.fromString(row.get("programId").toString());

                siteProgramEnrollmentService.enrollSiteinProgram(siteId, programId);
            }

            log.info("bulk enrollment completed for {} rows", result.getTotalRows());

            return ResponseEntity.ok("bulk enrollment done: " + result.getTotalRows());

        } catch (Exception e) {
            log.error("error during bulk enrollment: {}", e.getMessage(), e);
            throw e;
        }
    }

}
