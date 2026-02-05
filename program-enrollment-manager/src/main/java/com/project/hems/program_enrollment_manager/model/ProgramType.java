package com.project.hems.program_enrollment_manager.model;

public enum ProgramType {
    PEAK_SAVING, // This program type asks to use the battery power and save the grid consumption
    EMERGENCY_BACKUP, // Priotize the battery to get charge and use house consumption on GRID
    VIRTUAL_POWER_PLANT
}
