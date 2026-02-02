package com.hems.project.Virtual_Power_Plant.entity.simulator;

public enum ChargingStatus {
    FULL,
    CHARGING, // Solar/Grid -> Battery
    DISCHARGING, // Battery -> Home/Grid
    IDLE, // Battery full or disconnected
    EMPTY // SoC at 0%
}
