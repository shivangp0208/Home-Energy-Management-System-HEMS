package com.project.hems.hems_api_contracts.contract.program;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@NotNull
public class Program {

    private UUID programId;

    @NotEmpty(message = "program name cannot be empty or null")
    private String programName;

    @NotNull(message = "start date time cannot be null")
    @FutureOrPresent(message = "start date time cannot be in past, it can be in present or future")
    private LocalDate startDate;

    @NotNull(message = "end date time cannot be null")
    @Future(message = "end date time cannot be in past, it can be in future only")
    private LocalDate endDate;

    // This is optional, if want to give priority to a particular program
    @JsonIgnore
    private ProgramPriority programPriority;

    @NotNull(message = "program type cannot be null")
    private ProgramType programType;

    @NotNull(message = "program description is required and cannot be null")
    private ProgramDescription programDescription;

    private ProgramStatus programStatus;

    @ToString.Exclude
    private List<SiteDto> sites = new ArrayList<>();

}
