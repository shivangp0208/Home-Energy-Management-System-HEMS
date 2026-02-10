package com.hems.project.Virtual_Power_Plant.dto;

public enum VppOperationalStatus {

    STOPPED,        // VPP not running (default state)

    STARTING,       // Startup sequence running

    RUNNING,        // VPP actively generating / exporting

    PAUSED,         // Temporarily paused (optional but useful)

    MAINTENANCE,    // Planned maintenance

    FAULT           // Unexpected failure (trip, grid fault, etc)

}
