package com.project.hems.envoy_manager_service.service;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.hems.envoy_manager_service.config.SimulatorFeignConfig;
import com.project.hems.hems_api_contracts.contract.envoy.DispatchCommand;
import com.project.hems.hems_api_contracts.contract.simulator.MeterSnapshot;

import jakarta.validation.Valid;

@FeignClient(name = "simulator-service", path = "/api/v1/simulation", configuration = SimulatorFeignConfig.class)
public interface SimulatorFeignClientService {

    @PostMapping("/activate-meter/{siteId}")
    public ResponseEntity<MeterSnapshot> activateMeterData(@PathVariable(name = "siteId", required = true) UUID siteId,
            @RequestBody Double batteryCapacity);

    @PostMapping("/dispatch")
    public ResponseEntity<DispatchCommand> applyDispatch(@RequestBody @Valid DispatchCommand command);

    @GetMapping("/get-meter-data/{siteId}")
    public ResponseEntity<MeterSnapshot> getMeterData(@PathVariable(name = "siteId", required = true) UUID siteId);
}
