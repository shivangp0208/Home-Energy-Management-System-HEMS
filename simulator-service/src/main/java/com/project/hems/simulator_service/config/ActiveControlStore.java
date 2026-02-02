package com.project.hems.simulator_service.config;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.project.hems.simulator_service.model.ActiveControlState;
import com.project.hems.simulator_service.model.envoy.EnergyPriority;
import com.project.hems.simulator_service.web.exception.MeterAlreadyDispatchedException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActiveControlStore {

    // siteId -> active control
    private final ConcurrentHashMap<UUID, ActiveControlState> activeControls = new ConcurrentHashMap<>();
    public static final List<EnergyPriority> energyPriorities = List.of(EnergyPriority.SOLAR, EnergyPriority.GRID,
            EnergyPriority.BATTERY);

    public void applyDispatch(UUID siteId, ActiveControlState control) {
        log.info("applyDispatch: applying dispatch command " + control + " for siteId " + siteId);

        Optional<ActiveControlState> activeControl = getActiveControl(siteId);
        if (activeControl.isPresent()) {
            log.debug("applyDispatch: active control state already present for particular siteId");
            if (activeControl.get().getDispatchId().equals(control.getDispatchId())) {
                log.error("applyDispatch: meter already in dispatch mode with dispatchId = {} and for siteId = {}",
                        activeControl.get().getDispatchId(), siteId);
                throw new MeterAlreadyDispatchedException("meter already in dispatch mode with dispatchId "
                        + activeControl.get().getDispatchId() + " and for siteId " + siteId);
            }
        }

        if (activeControl.isEmpty()) {
            log.debug("applyDispatch: clean active control state for siteId " + siteId);
        }
        activeControls.put(siteId, control);
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
