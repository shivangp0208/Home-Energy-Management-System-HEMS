package com.project.hems.hems_api_contracts.contract.program;

import java.time.LocalDateTime;
import java.util.UUID;

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

    private String programName;

    private LocalDateTime startDateTime;
    
    private LocalDateTime endDateTime;

    private ProgramPriority programPriority;

    private ProgramType programType;

    private ProgramStatus programStatus;


}
