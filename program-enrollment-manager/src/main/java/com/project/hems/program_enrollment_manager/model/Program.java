package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private LocalDateTime startDateTime;
    
    @NotNull(message = "end date time cannot be null")
    @Future(message = "end date time cannot be in past, it can be in future only")
    private LocalDateTime endDateTime;

    // This is optional, if want to give priority to a particular program
    @JsonIgnore
    private ProgramPriority programPriority;

    @NotNull(message = "program type cannot be null")
    private ProgramType programType;

    private ProgramStatus programStatus;


}
