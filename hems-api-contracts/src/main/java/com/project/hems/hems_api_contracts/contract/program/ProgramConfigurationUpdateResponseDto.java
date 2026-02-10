package com.project.hems.hems_api_contracts.contract.program;

import java.time.LocalDateTime;
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
