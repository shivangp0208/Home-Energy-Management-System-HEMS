package com.project.hems.simulator_service.controller;

import com.project.hems.hems_api_contracts.contract.dispatch.DeviceCommand;
import com.project.hems.hems_api_contracts.contract.envoy.ActiveControlState;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;
import com.project.hems.simulator_service.model.DeviceCommandStore;
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
    @ApiResponse(responseCode = "404", description = "meter data not found for given siteId")
    @GetMapping("/get-meter-data/{siteId}")
    public ResponseEntity<MeterSnapshot> getMeterData(@PathVariable(name = "siteId", required = true) UUID siteId) {
        log.info("get meter data for siteId: {}", siteId);
        MeterSnapshot meterData = meterManagementService.getMeterData(siteId);
        if (meterData == null) {
            throw new MeterStatusNotFoudException("unable to find the meter detail with given site id " + siteId);
        }
        return new ResponseEntity<>(meterData, HttpStatus.OK);
    }

    @Operation(summary = "get all meter data", description = "retrieve all available meter readings")
    @ApiResponse(responseCode = "200", description = "all meter data fetched successfully")
    @GetMapping("/get-all-meter-data")
    public ResponseEntity<Map<String, MeterSnapshot>> getAllMeterData() {
        log.info("getAllMeterData: GET req for retreiving all meter details");
        return new ResponseEntity<>(meterReadings, HttpStatus.OK);
    }


    @Operation(summary = "activate meter", description = "activate meter for a site with given battery capacity")
    @ApiResponse(responseCode = "201", description = "meter activated successfully")
    @PostMapping("/activate-meter/{siteId}")
    public ResponseEntity<MeterSnapshot> activateMeterData(@PathVariable(name = "siteId", required = true) UUID siteId,
                                                           @RequestBody Double batteryCapacity) {
        log.info("activateMeterData: POST req for activate meter: {}", siteId, batteryCapacity);
        MeterSnapshot savedMeter = meterManagementService.activateMeter(siteId, batteryCapacity);
        return new ResponseEntity<>(savedMeter, HttpStatus.CREATED);
    }

    @Operation(summary = "apply dispatch command", description = "apply a dispatch command received from envoy to active control store")
    @ApiResponse(responseCode = "202", description = "dispatch command accepted successfully")
    @PostMapping("/apply-command")
    public ResponseEntity<Map<UUID, ActiveControlState>> applyDispatch(@RequestBody @Valid Map<UUID, ActiveControlState> activeControls) {
        log.info("applyDispatch: POST req for applying dispatch command received from envoy " + activeControls);
        meterManagementService.applyControl(activeControls);
        return new ResponseEntity<>(activeControls, HttpStatus.ACCEPTED);
    }

    @PutMapping("/remove-command/{siteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDispatchCommand(@PathVariable(name = "siteId", required = true) UUID siteId) {
        log.info("PUT req to end the dispatch event for siteId {}", siteId);
        meterManagementService.removeControl(siteId);
    }

}
