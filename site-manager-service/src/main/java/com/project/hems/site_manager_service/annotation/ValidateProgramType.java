package com.project.hems.site_manager_service.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ProgramTypeValidator.class)
public @interface ValidateProgramType {

    public String message() default
            "Invalid program type. Program Type should be either PEAK_SAVING, EMERGENCY_BACKUP, VIRTUAL_POWER_PLANT, " +
                    "peak_saving, emergency_backup, or virtual_power_plant.";

    //aa be niche nu aa annotation ne specific group ma nakhva mate nu che
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
