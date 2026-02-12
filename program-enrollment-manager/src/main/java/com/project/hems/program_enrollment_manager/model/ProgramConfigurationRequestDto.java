package com.project.hems.program_enrollment_manager.model;

import java.util.Map;

import com.project.hems.hems_api_contracts.contract.program.ProgramPriority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramConfigurationRequestDto {
    private Map<String,Object> programDescription;
    private ProgramPriority programPriority;
}
