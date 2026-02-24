package com.project.hems.program_enrollment_manager.service;

import com.project.hems.hems_api_contracts.contract.program.ProgramFeignDto;
import com.project.hems.hems_api_contracts.contract.site.SiteDto;
import com.project.hems.program_enrollment_manager.entity.ProgramEntity;
import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentEntity;
import com.project.hems.program_enrollment_manager.exception.ProgramExpiredException;
import com.project.hems.program_enrollment_manager.exception.ProgramNotFoundException;
import com.project.hems.program_enrollment_manager.exception.SiteAlreadyEnroledException;
import com.project.hems.program_enrollment_manager.exception.SiteNotFoundException;
import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollment;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.UUID;

public interface SiteProgramEnrollmentService {
     SiteProgramEnrollment enrollSiteinProgram(UUID siteId, @NonNull UUID programId);

}
