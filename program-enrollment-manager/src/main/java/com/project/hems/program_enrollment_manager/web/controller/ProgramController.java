package com.project.hems.program_enrollment_manager.web.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.hems.hems_api_contracts.contract.vpp.SiteEnrollSuccessResponse;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationRequestDto;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationResponseDto;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationUpdateRequestDto;
import com.project.hems.program_enrollment_manager.model.ProgramConfigurationUpdateResponseDto;
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

    //TODO:-
    //aa /get-all-programs ma jovu pages nu kam che ke nai shivang ne puchvu 
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
    public ResponseEntity<Program> getOneProgram(@PathVariable UUID programId) {
         Program programById = programService.findProgramById(programId);;
         return new ResponseEntity<>(programById,HttpStatus.OK);
    }

    @PostMapping("/create-program")
    public ResponseEntity<Program> createNewProgram(@RequestBody @Valid Program program) {
         Program saveNewProgram = programService.saveNewProgram(program);
        return new ResponseEntity<>(saveNewProgram,HttpStatus.CREATED);
    }
 
    @PostMapping("/configure-program/{programId}")
    public ResponseEntity<ProgramConfigurationResponseDto> configureProgram(
        @RequestBody ProgramConfigurationRequestDto dto,
        @PathVariable UUID programId
    ){
        ProgramConfigurationResponseDto configureProgram = programService.configureProgram(dto, programId);;
        return new ResponseEntity<>(configureProgram,HttpStatus.OK);
    }



    // @DeleteMapping("/delete-program/{programId}") AAMA MAI CHANGE KARYU CHE DELETE NA JAGYAE ACTIVATE AND DEACTIVATE CHE 
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    // public void deleteProgram(@PathVariable("programId") UUID programId) {
    //     programService.deleteProgram(programId);
    // }

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
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(
        @RequestParam UUID siteId,
        @RequestParam UUID programId
    ){
        SiteEnrollSuccessResponse enrollSiteinProgram = siteProgramEnrollmentService.enrollSiteinProgram(siteId, programId);;
        return new ResponseEntity<>(enrollSiteinProgram,HttpStatus.OK);
    }
    

    @PutMapping("/update-program/{programId}")
    public ResponseEntity<ProgramConfigurationUpdateResponseDto> updateProgram(
        @PathVariable UUID programId,
        @RequestBody ProgramConfigurationUpdateRequestDto programConfigurationRequestDto)
    {
        ProgramConfigurationUpdateResponseDto updateProgram = siteProgramEnrollmentService.updateProgram(programConfigurationRequestDto,programId);;
        return new ResponseEntity<>(updateProgram,HttpStatus.OK);
    }

    //activate program 
    @PostMapping("/activate-paogram/{programId}")
    public ResponseEntity<String> activateProgram(@PathVariable UUID programId){
         String activateProgram = programService.activateProgram(programId);;
         return new ResponseEntity<>(activateProgram,HttpStatus.OK);
    }


    //deactivate program 
     @PostMapping("/deactivate-paogram/{programId}")
    public ResponseEntity<String> deactivateProgram(@PathVariable UUID programId){
     String deactivateProgram = programService.deactivateProgram(programId);
        return new ResponseEntity<>(deactivateProgram,HttpStatus.OK);
    }





}
