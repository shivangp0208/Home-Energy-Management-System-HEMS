package com.project.hems.envoy_manager_service.service;

import java.util.Map;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.envoy.ActiveControlState;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.hems.envoy_manager_service.config.SimulatorFeignConfig;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;

import jakarta.validation.Valid;

@FeignClient(name = "simulator-service", path = "/api/v1/simulation", configuration = SimulatorFeignConfig.class)
public interface SimulatorFeignClientService {

    @PostMapping("/activate-meter/{siteId}")
    public ResponseEntity<MeterSnapshot> activateMeterData(@PathVariable(name = "siteId", required = true) UUID siteId,
                                                           @RequestBody Double batteryCapacity);

    // @PostMapping("/dispatch")
    // public ResponseEntity<DeviceCommand> applyDispatch(@RequestBody @Valid DeviceCommand command);

    @GetMapping("/get-meter-data/{siteId}")
    public ResponseEntity<MeterSnapshot> getMeterData(@PathVariable(name = "siteId", required = true) UUID siteId);

    @PostMapping("/apply-command")
    public ResponseEntity<Map<UUID, ActiveControlState>> applyDispatch(@RequestBody @Valid Map<UUID, ActiveControlState> activeControls);

    @PutMapping("/remove-command/{siteId}")
    public void removeDispatchCommand(@PathVariable(name = "siteId", required = true) UUID siteId);
}
