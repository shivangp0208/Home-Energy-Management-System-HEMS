package com.hems.project.Virtual_Power_Plant.controller;

import java.util.Map;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;
import com.project.hems.hems_api_contracts.contract.vpp.VppStrategyMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hems.project.Virtual_Power_Plant.service.VppSimulationService;
import lombok.RequiredArgsConstructor;

@Tag(name = "vpp simulator controller")
@Slf4j
@RestController
@RequestMapping("/api/v1/vpp-simulator")
@RequiredArgsConstructor
public class VppSimulatorController {

    private final VppSimulationService vppSimulationService;

    @Operation(
            summary = "start vpp",
            description = "start a vpp with max capacity and battery capacity, both must be greater than 0"
    )
    @ApiResponse(responseCode = "200", description = "vpp started successfully")
    @ApiResponse(responseCode = "400", description = "invalid capacity values")
    @PostMapping("/start/{vppId}")
    public ResponseEntity<?> start(
            @PathVariable UUID vppId,
            @RequestParam double maxCapacityW,
            @RequestParam double batteryCapacityWh
    ) {

        if (maxCapacityW <= 0 || batteryCapacityWh <= 0) {
            return ResponseEntity.badRequest().body("Capacity must be > 0");
        }

        vppSimulationService.startVpp(vppId, maxCapacityW, batteryCapacityWh);

        log.info("start controller is execute");
        return ResponseEntity.ok(Map.of(
                "message", "VPP started",
                "vppId", vppId
        ));
    }

    @Operation(
            summary = "stop vpp",
            description = "stop a running vpp by its id"
    )
    @ApiResponse(responseCode = "200", description = "vpp stopped successfully")
    @PostMapping("/stop/{vppId}")
    public ResponseEntity<?> stop(@PathVariable UUID vppId) {
        vppSimulationService.stopVpp(vppId);
        return ResponseEntity.ok(Map.of("message", "Stopped", "vppId", vppId));
    }


    @Operation(
            summary = "set generation mode",
            description = "update the generation mode of a vpp"
    )
    @ApiResponse(responseCode = "200", description = "generation mode updated successfully")
    @PostMapping("/{vppId}/mode")
    public ResponseEntity<?> mode(
            @PathVariable UUID vppId,
            @RequestParam GenerationMode mode
    ) {
        vppSimulationService.setMode(vppId, mode);

        return ResponseEntity.ok(Map.of(
                "message", "Generation mode updated",
                "mode", mode
        ));
    }

    @Operation(
            summary = "set strategy mode",
            description = "update the strategy mode of a vpp"
    )
    @ApiResponse(responseCode = "200", description = "strategy mode updated successfully")
    @PostMapping("/{vppId}/strategy-mode")
    public ResponseEntity<?> strategyMode(
            @PathVariable UUID vppId,
            @RequestParam VppStrategyMode strategyMode
    ) {
        vppSimulationService.setStrategyMode(vppId, strategyMode);

        return ResponseEntity.ok(Map.of(
                "message", "Strategy mode updated",
                "strategyMode", strategyMode
        ));
    }

    @Operation(
            summary = "set target export",
            description = "update the target export value in watts for a vpp"
    )
    @ApiResponse(responseCode = "200", description = "target export updated successfully")
    @PostMapping("/{vppId}/target-export")
    public ResponseEntity<?> targetExport(
            @PathVariable UUID vppId,
            @RequestParam double targetExportW
    ) {
        vppSimulationService.setTargetExport(vppId, targetExportW);

        return ResponseEntity.ok(Map.of(
                "message", "Target export updated",
                "targetExportW", targetExportW
        ));
    }

    @Operation(
            summary = "set site demand",
            description = "update the site demand value in watts for a vpp"
    )
    @ApiResponse(responseCode = "200", description = "site demand updated successfully")
    @PostMapping("/{vppId}/site-demand")
    public ResponseEntity<?> siteDemand(
            @PathVariable UUID vppId,
            @RequestParam double siteDemandW
    ) {
        vppSimulationService.setSiteDemand(vppId, siteDemandW);

        return ResponseEntity.ok(Map.of(
                "message", "Site demand updated",
                "siteDemandW", siteDemandW
        ));
    }
}


//TODO:-
//in this add validation maxCapacityW>0 che batteryCapacityWh> che ke nai check this in this controller
//controller ni under j if() lagaine joi levu if() true to service call karvi otherwise error throw kari..