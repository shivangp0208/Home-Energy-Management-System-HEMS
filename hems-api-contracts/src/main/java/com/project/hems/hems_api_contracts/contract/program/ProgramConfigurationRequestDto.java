package com.project.hems.hems_api_contracts.contract.program;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramConfigurationRequestDto {
    private Map<String,Object> programDescription;
    private ProgramPriority programPriority;   
}
