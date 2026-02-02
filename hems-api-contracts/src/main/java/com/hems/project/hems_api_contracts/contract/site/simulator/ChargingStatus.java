package com.hems.project.hems_api_contracts.contract.site.simulator;

public enum ChargingStatus {
    FULL,
    CHARGING, // Solar/Grid -> Battery
    DISCHARGING, // Battery -> Home/Grid
    IDLE, // Battery full or disconnected
    EMPTY // SoC at 0%
}
