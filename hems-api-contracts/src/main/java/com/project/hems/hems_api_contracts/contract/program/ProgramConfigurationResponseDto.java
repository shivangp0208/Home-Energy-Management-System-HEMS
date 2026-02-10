package com.project.hems.hems_api_contracts.contract.program;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramConfigurationResponseDto {
    private Map<String,Object> programDescription;
    private ProgramPriority priority;
}
