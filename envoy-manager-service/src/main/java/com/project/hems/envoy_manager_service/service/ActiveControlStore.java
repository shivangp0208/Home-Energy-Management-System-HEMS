package com.project.hems.envoy_manager_service.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.project.hems.envoy_manager_service.exception.DuplicateCommandException;
import com.project.hems.envoy_manager_service.model.ActiveControlState;
import com.project.hems.hems_api_contracts.contract.EnergyPriority;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActiveControlStore {

    // siteId -> active control
    private final ConcurrentHashMap<UUID, ActiveControlState> activeControls = new ConcurrentHashMap<>();
    public static final List<EnergyPriority> loadEnergyPriorities = List.of(EnergyPriority.SOLAR, EnergyPriority.GRID,
            EnergyPriority.BATTERY);
    public static final List<EnergyPriority> surplusEnergyPriorities = List.of(EnergyPriority.BATTERY,
            EnergyPriority.GRID);

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
