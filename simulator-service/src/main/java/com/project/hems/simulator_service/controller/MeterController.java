package com.project.hems.simulator_service.controller;

import com.project.hems.hems_api_contracts.contract.envoy.ActiveControlState;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;
import com.project.hems.simulator_service.service.MeterManagementService;
import com.project.hems.simulator_service.exception.MeterStatusNotFoudException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "meter controller", description = "endpoints to manage and monitor meters")
@Tag(name = "meter controller")
@Slf4j
@RestController
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
public class MeterController {

    private final MeterManagementService meterManagementService;
    private final Map<String, MeterSnapshot> meterReadings;

    @Operation(summary = "get meter data", description = "retrieve meter data for a specific site by siteId")
    @ApiResponse(responseCode = "200", description = "meter data fetched successfully")
    @ApiResponse(responseCode = "404", description = "meter data not found")
    @GetMapping("/get-meter-data/{siteId}")
    public ResponseEntity<MeterSnapshot> getMeterData(@PathVariable UUID siteId) {

        log.info("received request to fetch meter data for siteId {}", siteId);

        try {
            MeterSnapshot meterData = meterManagementService.getMeterData(siteId);

            if (meterData == null) {
                log.warn("meter data not found for siteId {}", siteId);
                throw new MeterStatusNotFoudException("meter not found for siteId " + siteId);
            }

            log.info("meter data fetched successfully for siteId {}", siteId);

            return ResponseEntity.ok(meterData);

        } catch (Exception e) {
            log.error("error fetching meter data for siteId {}: {}", siteId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "get all meter data", description = "retrieve all available meter readings")
    @ApiResponse(responseCode = "200", description = "all meter data fetched successfully")
    @GetMapping("/get-all-meter-data")
    public ResponseEntity<Map<String, MeterSnapshot>> getAllMeterData() {

        log.info("received request to fetch all meter data");

        try {
            log.info("total meter count {}", meterReadings.size());
            return ResponseEntity.ok(meterReadings);

        } catch (Exception e) {
            log.error("error fetching all meter data: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Operation(summary = "activate meter", description = "activate meter for a site with given battery capacity")
    @ApiResponse(responseCode = "201", description = "meter activated successfully")
    @PostMapping("/activate-meter/{siteId}")
    public ResponseEntity<MeterSnapshot> activateMeterData(
            @PathVariable UUID siteId,
            @RequestBody Double batteryCapacity) {

        log.info("received request to activate meter for siteId {} with batteryCapacity {}", siteId, batteryCapacity);

        try {
            MeterSnapshot savedMeter = meterManagementService.activateMeter(siteId, batteryCapacity);

            log.info("meter activated successfully for siteId {}", siteId);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMeter);

        } catch (Exception e) {
            log.error("error activating meter for siteId {}: {}", siteId, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "apply dispatch command", description = "apply a dispatch command received from envoy")
    @ApiResponse(responseCode = "202", description = "dispatch command accepted successfully")
    @PostMapping("/apply-command")
    public ResponseEntity<Map<UUID, ActiveControlState>> applyDispatch(
            @RequestBody @Valid Map<UUID, ActiveControlState> activeControls) {

        log.info("received dispatch command for {} sites", activeControls.size());

        try {
            meterManagementService.applyControl(activeControls);

            log.info("dispatch command applied successfully");

            return ResponseEntity.accepted().body(activeControls);

        } catch (Exception e) {
            log.error("error applying dispatch command: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "remove dispatch command", description = "remove active dispatch command for a site")
    @ApiResponse(responseCode = "204", description = "dispatch command removed successfully")
    @PutMapping("/remove-command/{siteId}")
    public ResponseEntity<Void> removeDispatchCommand(@PathVariable UUID siteId) {

        log.info("received request to remove dispatch command for siteId {}", siteId);

        try {
            meterManagementService.removeControl(siteId);

            log.info("dispatch command removed successfully for siteId {}", siteId);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("error removing dispatch command for siteId {}: {}", siteId, e.getMessage(), e);
            throw e;
        }
    }

}
