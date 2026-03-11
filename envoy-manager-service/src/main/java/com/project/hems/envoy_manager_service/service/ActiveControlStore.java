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
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    public static final List<EnergyPriority> loadEnergyPriorities = List.of(EnergyPriority.SOLAR, EnergyPriority.GRID,
            EnergyPriority.BATTERY);
    public static final List<EnergyPriority> surplusEnergyPriorities = List.of(EnergyPriority.BATTERY,
            EnergyPriority.GRID);

    private final SimulatorFeignClientService simulatorFeignClientService;

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

        log.debug("applyDispatch: new active control state adding for siteId " + siteId);
        activeControls.put(siteId, control);

        if (control.getValidUntil() != null) {
            log.debug("applyDispatch: scheduling a stopping dispatch command for site id " + siteId);

            long delay = Duration.between(Instant.now(), control.getValidUntil()).toSeconds();
            log.trace("applyDispatch: delay to be added to given site's dispatch command " + delay);

            scheduledExecutorService.schedule(() -> {
                handleInactive(siteId);
            }, delay + 1, TimeUnit.SECONDS);
        }
    }

    private void handleInactive(UUID siteId) {
        log.debug("handleInactive: inactive dispatch command started for site {}", siteId);

        ActiveControlState state = activeControls.get(siteId);
        log.trace("handleInactive: cuurent active state for given site {}", state);

        if (state != null && !state.isActive(Instant.now())) {
            log.debug("handleInactive: removing dispatch state for the given site");
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
