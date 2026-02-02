package com.hems.project.hems_api_contracts.contract.site.simulator;

public enum BatteryMode {
    AUTO, // Your "Priority Decision Matrix" logic
    FORCE_DISCHARGE, // VPP Request: Dump battery to grid
    FORCE_CHARGE, // VPP Request: Charge from grid immediately
    ECO_MODE // Save battery for specific hours
}
