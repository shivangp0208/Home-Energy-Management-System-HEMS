package com.project.hems.envoy_manager_service.service;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.hems.envoy_manager_service.model.BatteryControl;
import com.project.hems.envoy_manager_service.model.GridControl;
import com.project.hems.envoy_manager_service.model.GridControl.GridControlBuilder;
import com.project.hems.envoy_manager_service.model.BatteryControl.BatteryControlBuilder;
import com.project.hems.envoy_manager_service.model.SiteControlCommand;
import com.project.hems.envoy_manager_service.model.SiteControlCommand.SiteControlCommandBuilder;
import com.project.hems.envoy_manager_service.model.dispatch.DispatchEvent;
import com.project.hems.envoy_manager_service.model.simulator.BatteryMode;
import com.project.hems.envoy_manager_service.model.simulator.MeterSnapshot;
import com.project.hems.envoy_manager_service.web.exception.MeterStatusNotFoudException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandTranslatorService {

    private final SimulatorFeignClientService simulatorFeignClientService;

    public SiteControlCommand translateDispatchEvent(DispatchEvent dispatchEvent) {

        log.info("Translating DispatchEvent. dispatchId={}, siteId={}, eventType={}, powerReqW={}",
                dispatchEvent.getDispatchId(),
                dispatchEvent.getSiteId(),
                dispatchEvent.getEventType(),
                dispatchEvent.getPowerReqW());

        // 1. Basic metadata mapping
        SiteControlCommandBuilder commandBuilder = SiteControlCommand.builder();
        commandBuilder.dispatchId(dispatchEvent.getDispatchId());
        commandBuilder.siteId(dispatchEvent.getSiteId());

        // 2. Retrieve the meterId from received siteId
        log.debug("Fetching meter data for siteId={}", dispatchEvent.getSiteId());
        ResponseEntity<MeterSnapshot> meterData = simulatorFeignClientService.getMeterData(dispatchEvent.getSiteId());

        if (meterData.getBody() == null) {
            log.error("Meter data not found for siteId={}", dispatchEvent.getSiteId());
            throw new MeterStatusNotFoudException(
                    "unable to find the meter detail with given site id " + dispatchEvent.getSiteId());
        }

        log.debug("Meter data found. meterId={}", meterData.getBody().getMeterId());
        commandBuilder.meterId(meterData.getBody().getMeterId());

        // 3. Calculate Expiry (validUntil = Now + durationSec)
        Instant now = Instant.now();
        Instant validUntil = now.plusSeconds(dispatchEvent.getDurationSec());

        commandBuilder.timestamp(now);
        commandBuilder.validUntil(validUntil);

        log.debug("Command validity set. timestamp={}, validUntil={}", now, validUntil);

        // 4. Map Priorities directly
        commandBuilder.energyPriority(dispatchEvent.getEnergyPriority());
        commandBuilder.reason(dispatchEvent.getReason());

        log.debug("Energy priority={}, reason={}",
                dispatchEvent.getEnergyPriority(),
                dispatchEvent.getReason());

        // 5. Initialize Default Controls (Safety First!)
        BatteryControlBuilder batteryControlBuilder = BatteryControl.builder();
        GridControlBuilder gridControlBuilder = GridControl.builder();

        // set default values
        batteryControlBuilder.minSocPercent(20.0);
        batteryControlBuilder.maxSocPercent(100.0);
        gridControlBuilder.allowImport(true);

        log.debug("Initialized default controls: minSoc=20%, maxSoc=100%, gridImportAllowed=true");

        // 6. THE LOGIC SWITCH (The "Brain" of the translation)
        switch (dispatchEvent.getEventType()) {

            case EXPORT_POWER:
                log.info("Processing EXPORT_POWER event");

                batteryControlBuilder.mode(BatteryMode.FORCE_DISCHARGE);
                batteryControlBuilder.maxDischargeW(dispatchEvent.getPowerReqW());
                batteryControlBuilder.maxChargeW(0L);

                gridControlBuilder.allowExport(true);
                gridControlBuilder.maxExportW(dispatchEvent.getPowerReqW() * 1.1);
                gridControlBuilder.maxImportW(10000.00);

                log.debug("EXPORT_POWER config: maxDischargeW={}, maxExportW={}",
                        dispatchEvent.getPowerReqW(),
                        dispatchEvent.getPowerReqW() * 1.1);
                break;

            case IMPORT_POWER:
                log.info("Processing IMPORT_POWER event");

                batteryControlBuilder.mode(BatteryMode.FORCE_CHARGE);
                batteryControlBuilder.maxChargeW(dispatchEvent.getPowerReqW());

                gridControlBuilder.allowExport(false);
                gridControlBuilder.maxImportW(dispatchEvent.getPowerReqW() * 1.1);

                log.debug("IMPORT_POWER config: maxChargeW={}, maxImportW={}",
                        dispatchEvent.getPowerReqW(),
                        dispatchEvent.getPowerReqW() * 1.1);
                break;

            case PEAK_SAVING:
                log.info("Processing PEAK_SAVING event");

                batteryControlBuilder.mode(BatteryMode.AUTO);

                gridControlBuilder.allowExport(true);
                gridControlBuilder.maxImportW(100.0);

                log.debug("PEAK_SAVING config: grid maxImportW=100");
                break;

            default:
                log.warn("Unknown eventType={}, falling back to AUTO mode",
                        dispatchEvent.getEventType());

                batteryControlBuilder.mode(BatteryMode.AUTO);
                gridControlBuilder.allowExport(true);
                gridControlBuilder.maxExportW(5000.00);
                gridControlBuilder.maxImportW(5000.00);
                break;
        }

        batteryControlBuilder.targetPowerW(dispatchEvent.getPowerReqW());

        commandBuilder.batteryControl(batteryControlBuilder.build());
        commandBuilder.gridControl(gridControlBuilder.build());

        SiteControlCommand command = commandBuilder.build();

        log.info("Command translation completed. dispatchId={}, batteryMode={}, targetPowerW={}",
                command.getDispatchId(),
                command.getBatteryControl().getMode(),
                command.getBatteryControl().getTargetPowerW());

        return command;
    }
}
