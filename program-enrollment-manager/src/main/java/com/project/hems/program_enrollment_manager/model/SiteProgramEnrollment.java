package com.project.hems.program_enrollment_manager.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.site.Site;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteProgramEnrollment {

    private UUID enrollmentId;

    @NotNull(message = "site id cannot be null")
    private Site site;
    
    @NotNull(message = "program detail cannot be null")
    private Program program;

    private LocalDateTime enrollmentTime;
}