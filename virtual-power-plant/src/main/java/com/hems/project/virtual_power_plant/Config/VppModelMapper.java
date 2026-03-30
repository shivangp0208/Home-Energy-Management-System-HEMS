package com.hems.project.virtual_power_plant.Config;

import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;
import org.springframework.stereotype.Component;

import com.hems.project.virtual_power_plant.entity.VppSnapshotEntity;

@Component
public class VppModelMapper {

    public VppSnapshotEntity toEntity(VppSnapshot dto) {
        if (dto == null) return null;

        return VppSnapshotEntity.builder()
                .vppId(dto.getVppId())
                .timestamp(dto.getTimestamp())

                .solarW(dto.getSolarW())
                .coalW(dto.getCoalW())
                .nuclearW(dto.getNuclearW())
                .thermalW(dto.getThermalW())

                .totalGenerationW(dto.getTotalGenerationW())

                .batteryPowerW(dto.getBatteryPowerW())
                .gridPowerW(dto.getGridPowerW())

                .batteryCapacityWh(dto.getBatteryCapacityWh())
                .batteryRemainingWh(dto.getBatteryRemainingWh())
                .batterySoc(dto.getBatterySoc())

                .targetExportW(dto.getTargetExportW())
                .mode(dto.getMode())
                .build();
    }

    public VppSnapshot toDto(VppSnapshotEntity entity) {
        if (entity == null) return null;

        return VppSnapshot.builder()
                .vppId(entity.getVppId())
                .timestamp(entity.getTimestamp())

                .solarW(entity.getSolarW())
                .coalW(entity.getCoalW())
                .nuclearW(entity.getNuclearW())
                .thermalW(entity.getThermalW())

                .totalGenerationW(entity.getTotalGenerationW())

                .batteryPowerW(entity.getBatteryPowerW())
                .gridPowerW(entity.getGridPowerW())

                .batteryCapacityWh(entity.getBatteryCapacityWh())
                .batteryRemainingWh(entity.getBatteryRemainingWh())
                .batterySoc(entity.getBatterySoc())

                .targetExportW(entity.getTargetExportW())
                .mode(entity.getMode())
                .build();
    }
}
