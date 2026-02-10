package com.project.hems.hems_api_contracts.contract.program;

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
