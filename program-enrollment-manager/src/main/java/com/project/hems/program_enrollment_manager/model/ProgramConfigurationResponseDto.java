package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.project.hems.hems_api_contracts.contract.program.ProgramPriority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramConfigurationResponseDto {
    private Map<String,Object> programDescription;
    private ProgramPriority priority;
}
