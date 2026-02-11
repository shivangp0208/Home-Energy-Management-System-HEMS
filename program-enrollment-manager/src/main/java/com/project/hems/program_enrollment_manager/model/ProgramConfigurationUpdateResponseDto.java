package com.project.hems.program_enrollment_manager.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProgramConfigurationUpdateResponseDto {

    // private ProgramType type;
    // private String programDescription;
    // private ProgramPriority priority;
    // private LocalDateTime startDateTime;
    // private LocalDateTime endDateTime;        
    // private LocalDateTime updateProgramConfiguration;
    private UUID programId;
    private String message;
}
