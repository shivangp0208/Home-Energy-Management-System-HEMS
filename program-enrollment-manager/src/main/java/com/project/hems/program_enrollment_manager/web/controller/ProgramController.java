package com.project.hems.program_enrollment_manager.web.controller;

import org.springframework.web.bind.annotation.RestController;

import com.project.hems.program_enrollment_manager.model.Program;
import com.project.hems.program_enrollment_manager.service.ProgramService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/program")
public class ProgramController {

    private final ProgramService programService;

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
    public Program createNewProgram(@RequestBody Program program) {
        return programService.saveNewProgram(program);
    }

    @DeleteMapping("/delete-program/{programId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProgram(@PathVariable("programId") UUID programId) {
        programService.deleteProgram(programId);
    }
}
