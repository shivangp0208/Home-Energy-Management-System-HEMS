package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.Map;


import lombok.Data;

@Data
public class ProgramConfigurationUpdateRequestDto {
    private ProgramType type;
    private Map<String,Object> programDescription;
    private ProgramPriority priority;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;    
}
