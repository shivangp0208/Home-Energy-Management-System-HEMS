package com.project.hems.program_enrollment_manager.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.hems.program_enrollment_manager.entity.SiteProgramEnrollmentAudit;
import com.project.hems.program_enrollment_manager.model.SiteProgramEnrollmentAuditDto;

@Configuration
public class ProgramMapper {

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper(); 
    }


     public SiteProgramEnrollmentAudit toEntity(SiteProgramEnrollmentAuditDto dto) {

        if (dto == null) {
            return null;
        }

        SiteProgramEnrollmentAudit entity = new SiteProgramEnrollmentAudit();

        entity.setAuditId(dto.getAuditId());
        entity.setEnrollmentId(dto.getEnrollmentId());
        entity.setProgramId(dto.getProgramId());
        entity.setOldSiteStatus(dto.getOldSiteStatus());
        entity.setNewSiteStatus(dto.getNewSiteStatus());
        entity.setChangeAt(dto.getChangeAt());
        entity.setReason(dto.getReason());
        entity.setChangeBy(dto.getChangeBy());

        return entity;
    }


}
