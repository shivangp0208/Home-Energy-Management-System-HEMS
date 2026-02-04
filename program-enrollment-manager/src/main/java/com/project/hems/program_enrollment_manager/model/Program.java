package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Program {

    private UUID programId;
    private String programName;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    // This is optional, if want to give priority to a particular program
    private ProgramPriority programPriority;

    private ProgramType programType;
}
