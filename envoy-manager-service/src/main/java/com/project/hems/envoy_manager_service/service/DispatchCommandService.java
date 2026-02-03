package com.project.hems.envoy_manager_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.envoy.DispatchCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchCommandService {
    private final SimulatorFeignClientService simulatorFeignClientService;

    public void applyControlToSimulation(DispatchCommand command) {
        UUID siteId = command.getSiteId();

        log.info("Applying Dispatch Control to Meter {}", siteId);

        // Single Call to Update Simulation Settings
        simulatorFeignClientService.applyDispatch(command);

        log.info("Successfully reconfigured Simulation for Dispatch ID: {}", command.getDispatchId());
    }
}
