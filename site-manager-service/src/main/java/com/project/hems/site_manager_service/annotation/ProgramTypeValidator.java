package com.project.hems.site_manager_service.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ProgramTypeValidator implements ConstraintValidator<ValidateProgramType,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        List<String> employeeType=List.of("PEAK_SAVING","EMERGENCY_BACKUP","VIRTUAL_POWER_PLANT",
                "peak_saving","emergency_backup","virtual_power_plant");

        return employeeType.contains(value);
    }
}
