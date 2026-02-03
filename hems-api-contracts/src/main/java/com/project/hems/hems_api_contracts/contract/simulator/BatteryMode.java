package com.project.hems.hems_api_contracts.contract.simulator;

public enum BatteryMode {
    AUTO, // Your "Priority Decision Matrix" logic
    FORCE_DISCHARGE, // VPP Request: Dump battery to grid
    FORCE_CHARGE, // VPP Request: Charge from grid immediately
    ECO_MODE // Save battery for specific hours
}
