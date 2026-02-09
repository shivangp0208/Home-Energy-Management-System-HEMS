package com.hems.project.Virtual_Power_Plant.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hems.project.Virtual_Power_Plant.service.VppSimulationService;
import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vpp-simulator")
@RequiredArgsConstructor
public class VppSimulatorController {

    //TODO:-
    //in this add validation maxCapacityW>0 che batteryCapacityWh> che ke nai check this in this controller
    //controller ni under j if() lagaine joi levu if() true to service call karvi otherwise error throw kari..
    private final VppSimulationService vppSimulationService;

    @PostMapping("/start/{vppId}")
    public ResponseEntity<?> start(
            @PathVariable("vppId") UUID vppId,
            @RequestParam("maxCapacityW") double maxCapacityW,
            @RequestParam("batteryCapacityWh") double batteryCapacityWh
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
    public ResponseEntity<?> stop(@PathVariable("vppId") UUID vppId) {
        vppSimulationService.stopVpp(vppId);

        return ResponseEntity.ok(Map.of(
                "message", "VPP simulator stopped",
                "vppId", vppId.toString()
        ));
    }

    @PostMapping("/{vppId}/mode")
    public ResponseEntity<?> mode(
            @PathVariable("vppId") UUID vppId,
            @RequestParam("mode") GenerationMode mode
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
            @PathVariable("vppId") UUID vppId,
            @RequestParam("targetExportW") double targetExportW
    ) {
        vppSimulationService.setTargetExport(vppId, targetExportW);

        return ResponseEntity.ok(Map.of(
                "message", "Target export updated",
                "vppId", vppId.toString(),
                "targetExportW", targetExportW
        ));
    }

    @PostMapping("/{vppId}/site-demand")
    public ResponseEntity<?> siteDemand(
            @PathVariable UUID vppId,
            @RequestParam("siteDemandW") double siteDemandW
    ) {
        //TODO:-
        //pelu kafka ma send kariee chiee reuiqrement vadu e ahiya intergrate karvu
        //vppSimulationService.setSiteDemand(vppId, siteDemandW);
        return ResponseEntity.ok(Map.of("message","Site demand updated","vppId",vppId.toString(),"siteDemandW",siteDemandW));
    }



}
