package com.project.hems.simulator_service.web.controller;

import com.project.hems.hems_api_contracts.contract.envoy.DispatchCommand;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;
import com.project.hems.simulator_service.config.ActiveControlStore;
import com.project.hems.simulator_service.model.ActiveControlState;
import com.project.hems.simulator_service.service.MeterManagementService;
import com.project.hems.simulator_service.web.exception.MeterStatusNotFoudException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
public class MeterController {

    private final MeterManagementService meterManagementService;
    private final Map<String, MeterSnapshot> meterReadings;
    private final ActiveControlStore activeControlStore;

    @GetMapping("/get-meter-data/{siteId}")
    public ResponseEntity<MeterSnapshot> getMeterData(@PathVariable(name = "siteId", required = true) UUID siteId) {
        log.info("get meter data for siteId: {}", siteId);
        MeterSnapshot meterData = meterManagementService.getMeterData(siteId);
        if (meterData == null) {
            throw new MeterStatusNotFoudException("unable to find the meter detail with given site id " + siteId);
        }
        return new ResponseEntity<>(meterData, HttpStatus.OK);
    }

    @GetMapping("/get-all-meter-data")
    public ResponseEntity<Map<String, MeterSnapshot>> getAllMeterData() {
        log.info("getAllMeterData: GET req for retreiving all meter details");
        return new ResponseEntity<>(meterReadings, HttpStatus.OK);
    }

    @PostMapping("/activate-meter/{siteId}")
    public ResponseEntity<MeterSnapshot> activateMeterData(@PathVariable(name = "siteId", required = true) UUID siteId,
            @RequestBody Double batteryCapacity) {
        log.info("activateMeterData: POST req for activate meter: {}", siteId, batteryCapacity);
        MeterSnapshot savedMeter = meterManagementService.activateMeter(siteId, batteryCapacity);

        return new ResponseEntity<>(savedMeter, HttpStatus.CREATED);
    }

    @PostMapping("/dispatch")
    public ResponseEntity<DispatchCommand> applyDispatch(@RequestBody @Valid DispatchCommand command) {
        log.info("applyDispatch: POST req for applying dispatch command received from envoy " + command);
        ActiveControlState control = new ActiveControlState(
                command.getDispatchId(),
                command.getBatteryControl(),
                command.getGridControl(),
                command.getEnergyPriority(),
                command.getValidUntil());

        activeControlStore.applyDispatch(command.getSiteId(), control);

        return new ResponseEntity<>(command, HttpStatus.ACCEPTED);
    }

}
