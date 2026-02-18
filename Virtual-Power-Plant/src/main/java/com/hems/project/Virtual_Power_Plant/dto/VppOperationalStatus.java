package com.hems.project.Virtual_Power_Plant.dto;

public enum VppOperationalStatus {

    INACTIVE,//it means haji apde empty entry kari che so by default aa rehse

    STOPPED,        // VPP not running (default state)

    STARTING,       // Startup sequence running

    RUNNING,        // VPP actively generating / exporting

    PAUSED,         // Temporarily paused (optional but useful)

    MAINTENANCE,    // Planned maintenance

    FAULT           // Unexpected failure (trip, grid fault, etc)

}
