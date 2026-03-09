package com.project.hems.envoy_manager_service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.netflix.discovery.converters.Auto;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.hems.envoy_manager_service.exception.DuplicateCommandException;
import com.project.hems.hems_api_contracts.contract.envoy.ActiveControlState;
import com.project.hems.hems_api_contracts.contract.EnergyPriority;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class ActiveControlStore {

    // siteId -> active control
    private final ConcurrentHashMap<UUID, ActiveControlState> activeControls = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    public static final List<EnergyPriority> loadEnergyPriorities = List.of(EnergyPriority.SOLAR, EnergyPriority.GRID,
            EnergyPriority.BATTERY);
    public static final List<EnergyPriority> surplusEnergyPriorities = List.of(EnergyPriority.BATTERY,
            EnergyPriority.GRID);

    private final SimulatorFeignClientService simulatorFeignClientService;

    @Autowired
    public ActiveControlStore(SimulatorFeignClientService simulatorFeignClientService) {
        this.simulatorFeignClientService = simulatorFeignClientService;
    }

    public void applyDispatch(UUID siteId, ActiveControlState control) {
        log.info("applyDispatch: applying dispatch command " + control + " for siteId " + siteId);

        Optional<ActiveControlState> activeControl = getActiveControl(siteId);
        if (activeControl.isPresent()) {
            log.debug("applyDispatch: active control state already present for particular siteId");
            if (activeControl.get().getEventId().equals(control.getEventId())) {
                log.error("applyDispatch: meter already in dispatch mode with dispatchId = {} and for siteId = {}",
                        activeControl.get().getEventId(), siteId);
                throw new DuplicateCommandException("meter already in dispatch mode with dispatchId "
                        + activeControl.get().getEventId() + " and for siteId " + siteId);
            }
        }

        if (activeControl.isEmpty()) {
            log.debug("applyDispatch: clean active control state for siteId " + siteId);
        }
        activeControls.put(siteId, control);

        if (control.getValidUntil() != null) {
            long delay = Duration.between(control.getValidUntil(), Instant.now()).toMillis();

            scheduledExecutorService.schedule(() -> {
                handleInactive(siteId);
            }, delay, TimeUnit.MINUTES);
        }
    }

    private void handleInactive(UUID siteId) {
        ActiveControlState state = activeControls.get(siteId);

        if (state != null && !state.isActive(Instant.now())) {
            activeControls.remove(siteId);
            onSiteDispatchEnded(siteId);
        }
    }

    private void onSiteDispatchEnded(UUID siteId) {
        log.debug("onSiteDispatchEnded: dispatch event ended for siteId " + siteId);
        simulatorFeignClientService.removeDispatchCommand(siteId);
    }

    public Optional<ActiveControlState> getActiveControl(UUID siteId) {
        ActiveControlState control = activeControls.get(siteId);

        if (control == null || !control.isActive(Instant.now())) {
            activeControls.remove(siteId);
            return Optional.empty();
        }

        return Optional.of(control);
    }
}
