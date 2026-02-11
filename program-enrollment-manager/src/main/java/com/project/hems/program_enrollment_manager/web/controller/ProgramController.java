package com.project.hems.program_enrollment_manager.web.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.service.ProgramService;
import com.project.hems.program_enrollment_manager.service.SiteProgramEnrollmentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/program")
public class ProgramController {

    private final ProgramService programService;
    private final SiteProgramEnrollmentService siteProgramEnrollmentService;

    @GetMapping("/get-all-programs")
    @ResponseStatus(HttpStatus.OK)
    public Page<Program> getAllPrograms(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize) {

        PageRequest pageReq = PageRequest.of(pageNumber, pageSize);

        return programService.findAllPrograms(pageReq);
    }

    @GetMapping("/get-program/{programId}")
    public ResponseEntity<Program> getOneProgram(@PathVariable UUID programId) {
        Program programById = programService.findProgramById(programId);
        return new ResponseEntity<>(programById, HttpStatus.OK);
    }

    @PostMapping("/create-program")
    public ResponseEntity<Program> createNewProgram(@RequestBody @Valid Program program) {
        Program saveNewProgram = programService.createNewProgram(program);
        return new ResponseEntity<>(saveNewProgram, HttpStatus.CREATED);
    }

    @PutMapping("/update-program/{programId}")
    public ResponseEntity<Program> updateProgram(
            @PathVariable UUID programId,
            @RequestBody Program program) {
        Program updatedProgram = programService.updateProgram(programId, program);
        return new ResponseEntity<>(updatedProgram, HttpStatus.CREATED);
    }

    // TODO: instead of bringing just siteId or programId bring whole program POJO using feign client if required
    // here we find which site is enroll in which program
    @PostMapping("/find-program-by-site")
    public ResponseEntity<List<ProgramEntity>> findProgramBySiteId(@RequestParam UUID siteId) {
        List<ProgramEntity> programBySite = siteProgramEnrollmentService.findProgramBySite(siteId);
        return new ResponseEntity<>(programBySite, HttpStatus.OK);
    }

    // here we in particular program how many site is enroll
    @PostMapping("/find-site-by-program")
    public ResponseEntity<List<UUID>> findSiteIdByProgram(@RequestParam UUID programId) {
        List<UUID> listSiteIds = siteProgramEnrollmentService.findSiteIdByProgramId(programId);
        return new ResponseEntity<>(listSiteIds, HttpStatus.OK);
    }

    // here we find enroll site in particular program
    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(
            @RequestParam UUID siteId,
            @RequestParam UUID programId) {
        SiteEnrollSuccessResponse enrollSiteinProgram = siteProgramEnrollmentService.enrollSiteinProgram(siteId,
                programId);
        return new ResponseEntity<>(enrollSiteinProgram, HttpStatus.OK);
    }

    // delete a program
    @DeleteMapping("/delete-program/{programId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProgram(@PathVariable("programId") UUID programId) {
        programService.deleteProgram(programId);
    }

    // activate program
    @PostMapping("/activate-paogram/{programId}")
    public ResponseEntity<Program> activateProgram(@PathVariable UUID programId) {
        Program activatedProgram = programService.activateProgram(programId);
        return new ResponseEntity<>(activatedProgram, HttpStatus.OK);
    }

    // deactivate program
    @PostMapping("/deactivate-paogram/{programId}")
    public ResponseEntity<Program> deactivateProgram(@PathVariable UUID programId) {
        Program deactivatedProgram = programService.deactivateProgram(programId);
        return new ResponseEntity<>(deactivatedProgram, HttpStatus.OK);
    }

}
