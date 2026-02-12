package com.project.hems.hems_api_contracts.contract.program;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AddProgramConfigInSite {

        private UUID programId;

        private String programName;

        private Map<String, Object> programDescription;

        private LocalDateTime startDateTime;

        private LocalDateTime endDateTime;

        private ProgramPriority programPriority;

        private ProgramType programType;

        private ProgramStatus programStatus;

}
