package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.Map;


import com.project.hems.hems_api_contracts.contract.program.ProgramPriority;
import com.project.hems.hems_api_contracts.contract.program.ProgramType;
import lombok.Data;

@Data
public class ProgramConfigurationUpdateRequestDto {
    private ProgramType type;
    private Map<String,Object> programDescription;
    private ProgramPriority priority;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;    
}
