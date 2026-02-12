package com.project.hems.program_enrollment_manager.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "program controller", description = "endpoints to manage and configure programs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/program")
public class ProgramController {

    private final ProgramService programService;
    private final SiteProgramEnrollmentService siteProgramEnrollmentService;

    //TODO:-
    //.1aa /get-all-programs ma jovu pages nu kam che ke nai shivang ne puchvu 
    //2.program ma conflict ave toh e check karvani ek function banavu jema aa check thayy
    @Operation(
            summary = "get all programs",
            description = "retrieve paginated list of all programs"
    )
    @ApiResponse(responseCode = "200", description = "successfully retrieved list of programs")
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

    @Operation(
            summary = "get program by id",
            description = "retrieve a single program using its unique programId"
    )
    @ApiResponse(responseCode = "200", description = "program found and returned successfully")
    @ApiResponse(responseCode = "404", description = "program with given id not found")
    @GetMapping("/get-program/{programId}")
    public ResponseEntity<Program> getOneProgram(@PathVariable UUID programId) {
         Program programById = programService.findProgramById(programId);;
         return new ResponseEntity<>(programById,HttpStatus.OK);
    }

    @Operation(
            summary = "create new program",
            description = "create a new program with provided details"
    )
    @ApiResponse(responseCode = "201", description = "program created successfully")
    @PostMapping("/create-program")
    public ResponseEntity<Program> createNewProgram(@RequestBody @Valid Program program) {
         Program saveNewProgram = programService.saveNewProgram(program);
        return new ResponseEntity<>(saveNewProgram,HttpStatus.CREATED);
    }

    @Operation(
            summary = "configure program",
            description = "configure settings for an existing program by programId"
    )
    @ApiResponse(responseCode = "200", description = "program configured successfully")
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
    @Operation(
            summary = "find programs by site",
            description = "find all programs a particular site is enrolled in"
    )
    @ApiResponse(responseCode = "200", description = "list of programs returned successfully")
    @PostMapping("/find-program-by-site")
    public ResponseEntity<List<ProgramEntity>> findProgramBySiteId(@RequestParam UUID siteId){
        List<ProgramEntity> programBySite = siteProgramEnrollmentService.findProgramBySite(siteId);
        return new ResponseEntity<>(programBySite,HttpStatus.OK);
    }

    //here we in particular program how many site is enroll
    @Operation(
            summary = "find sites by program",
            description = "find all site ids enrolled in a particular program"
    )
    @ApiResponse(responseCode = "200", description = "list of site ids returned successfully")
    @PostMapping("/find-site-by-program")
    public ResponseEntity<List<UUID>> findSiteIdByProgram(@RequestParam UUID programId){
        List<UUID> listSiteIds=siteProgramEnrollmentService.findSiteIdByProgramId(programId);
        return new ResponseEntity<>(listSiteIds,HttpStatus.OK);
    }

    //TODO:-
    //jyare site enroll thayy tyare pan apde audit table ma eni entry padvi..
    //here we find enroll site in particular program
    @Operation(
            summary = "enroll site in program",
            description = "enroll a site in a specific program"
    )
    @ApiResponse(responseCode = "200", description = "site enrolled in program successfully")
    @PostMapping("/enroll-site-in-program")
    public ResponseEntity<SiteEnrollSuccessResponse> enrollSiteinProgram(
        @RequestParam UUID siteId,
        @RequestParam UUID programId
    ){
        SiteEnrollSuccessResponse enrollSiteinProgram = siteProgramEnrollmentService.enrollSiteinProgram(siteId, programId);;
        return new ResponseEntity<>(enrollSiteinProgram,HttpStatus.OK);
    }

    @Operation(
            summary = "update program configuration",
            description = "update configuration details of an existing program"
    )
    @ApiResponse(responseCode = "200", description = "program configuration updated successfully")
    @PutMapping("/update-program/{programId}")
    public ResponseEntity<ProgramConfigurationUpdateResponseDto> updateProgram(
        @PathVariable UUID programId,
        @RequestBody ProgramConfigurationUpdateRequestDto programConfigurationRequestDto)
    {
        ProgramConfigurationUpdateResponseDto updateProgram = siteProgramEnrollmentService.updateProgram(programConfigurationRequestDto,programId);;
        return new ResponseEntity<>(updateProgram,HttpStatus.OK);
    }

    //activate program
    @Operation(
            summary = "activate program",
            description = "activate a program by its programId"
    )
    @ApiResponse(responseCode = "200", description = "program activated successfully")
    @PostMapping("/activate-paogram/{programId}")
    public ResponseEntity<String> activateProgram(@PathVariable UUID programId){
         String activateProgram = programService.activateProgram(programId);;
         return new ResponseEntity<>(activateProgram,HttpStatus.OK);
    }


    //deactivate program
    @Operation(
            summary = "deactivate program",
            description = "deactivate a program by its programId"
    )
    @ApiResponse(responseCode = "200", description = "program deactivated successfully")
     @PostMapping("/deactivate-paogram/{programId}")
    public ResponseEntity<String> deactivateProgram(@PathVariable UUID programId){
     String deactivateProgram = programService.deactivateProgram(programId);
        return new ResponseEntity<>(deactivateProgram,HttpStatus.OK);
    }





}
