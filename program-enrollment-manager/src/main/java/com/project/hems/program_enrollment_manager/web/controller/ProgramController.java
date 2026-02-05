package com.project.hems.program_enrollment_manager.web.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.service.ProgramService;
import com.project.hems.program_enrollment_manager.service.SiteProgramEnrollmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

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
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/program")
public class ProgramController {

    private final ProgramService programService;
    private final SiteProgramEnrollmentService siteProgramEnrollmentService;

    @GetMapping("/get-all-programs")
    @ResponseStatus(HttpStatus.OK)
    public Page<Program> getAllPrograms(
            @RequestParam(name = "pageNumber", required = false) int pageNumber,
            @RequestParam(name = "pageSize", required = false) int pageSize) {

        pageNumber = pageNumber < 0 ? 0 : pageNumber;
        pageSize = pageSize < 0 ? 0 : pageSize;

        PageRequest pageReq = PageRequest.of(pageNumber, pageSize);

        return programService.findAllPrograms(pageReq);
    }

    @GetMapping("/get-program/{programId}")
    @ResponseStatus(HttpStatus.OK)
    public Program getOneProgram(@PathVariable("programId") UUID programId) {
        return programService.findProgramById(programId);
    }

    @PostMapping("/create-program")
    @ResponseStatus(HttpStatus.CREATED)
    public Program createNewProgram(@RequestBody @Valid Program program) {
        return programService.saveNewProgram(program);
    }

    @DeleteMapping("/delete-program/{programId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgram(@PathVariable("programId") UUID programId) {
        programService.deleteProgram(programId);
    }

    //here we find which site is enroll in which program
    @PostMapping("/find-program-by-site")
    public ResponseEntity<List<ProgramEntity>> findProgramBySiteId(@RequestParam UUID siteId){
        List<ProgramEntity> programBySite = siteProgramEnrollmentService.findProgramBySite(siteId);
        return new ResponseEntity<>(programBySite,HttpStatus.OK);
    }

    //here we in particular program how many site is enroll
    @PostMapping("/find-site-by-program")
    public ResponseEntity<List<UUID>> findSiteIdByProgram(@RequestParam UUID programId){
        List<UUID> listSiteIds=siteProgramEnrollmentService.findSiteIdByProgramId(programId);
        return new ResponseEntity<>(listSiteIds,HttpStatus.OK);
    }

    //here we find enroll site in particular program 
    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(){
        
        return null;

    }
    
}
