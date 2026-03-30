package com.hems.project.virtual_power_plant.dto;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VppStateStore {

    private final Map<UUID, VppSnapshot> vppStateStore;

    public Optional<VppSnapshot> get(UUID vppId) {
        return Optional.ofNullable(vppStateStore.get(vppId));
    }

    public void put(UUID vppId, VppSnapshot snapshot) {
        vppStateStore.put(vppId, snapshot);
    }

    public boolean exists(UUID vppId) {
        return vppStateStore.containsKey(vppId);
    }

    public void remove(UUID vppId) {
        vppStateStore.remove(vppId);
    }

    public Map<UUID, VppSnapshot> getAll() {
        return vppStateStore;
    }
}
