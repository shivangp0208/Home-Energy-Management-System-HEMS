package com.hems.project.Virtual_Power_Plant.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hems.project.Virtual_Power_Plant.dto.GenerationMode;
import com.hems.project.Virtual_Power_Plant.service.VppSimulationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vpp-simulator")
@RequiredArgsConstructor
public class VppSimulatorController {

    private final VppSimulationService vppSimulationService;

    @PostMapping("/start/{vppId}")
    public ResponseEntity<?> start(
            @PathVariable UUID vppId,
            @RequestParam double maxCapacityW,
            @RequestParam double batteryCapacityWh
    ) {
        vppSimulationService.startVpp(vppId, maxCapacityW, batteryCapacityWh);

        return ResponseEntity.ok(Map.of(
                "message", "VPP simulator started",
                "vppId", vppId.toString(),
                "maxCapacityW", maxCapacityW,
                "batteryCapacityWh", batteryCapacityWh
        ));
    }

    @PostMapping("/stop/{vppId}")
    public ResponseEntity<?> stop(@PathVariable UUID vppId) {
        vppSimulationService.stopVpp(vppId);

        return ResponseEntity.ok(Map.of(
                "message", "VPP simulator stopped",
                "vppId", vppId.toString()
        ));
    }

    @PostMapping("/{vppId}/mode")
    public ResponseEntity<?> mode(
            @PathVariable UUID vppId,
            @RequestParam GenerationMode mode
    ) {
        vppSimulationService.setMode(vppId, mode);

        return ResponseEntity.ok(Map.of(
                "message", "Mode updated",
                "vppId", vppId.toString(),
                "mode", mode.toString()
        ));
    }

    @PostMapping("/{vppId}/target-export")
    public ResponseEntity<?> target(
            @PathVariable UUID vppId,
            @RequestParam double targetExportW
    ) {
        vppSimulationService.setTargetExport(vppId, targetExportW);

        return ResponseEntity.ok(Map.of(
                "message", "Target export updated",
                "vppId", vppId.toString(),
                "targetExportW", targetExportW
        ));
    }
}
