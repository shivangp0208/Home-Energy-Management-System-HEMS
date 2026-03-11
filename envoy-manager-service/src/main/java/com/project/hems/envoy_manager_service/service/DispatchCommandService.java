package com.project.hems.envoy_manager_service.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.project.hems.envoy_manager_service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.project.hems.hems_api_contracts.contract.envoy.ActiveControlState;
import com.project.hems.hems_api_contracts.contract.dispatch.DeviceCommand;
import com.project.hems.hems_api_contracts.contract.envoy.BatteryControl;
import com.project.hems.hems_api_contracts.contract.envoy.GridControl;
import com.project.hems.hems_api_contracts.contract.program.Program;
import com.project.hems.hems_api_contracts.contract.program.ProgramDescription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchCommandService {

    private final ProgramFeignClientService programFeignClientService;
    private final SimulatorFeignClientService simulatorFeignClientService;
    private final ActiveControlStore activeControlStore; // Inject the store directly

    public void applyControlToSimulation(DeviceCommand command) {

        Program program = programFeignClientService.getOneProgram(false, command.getProgramId()).getBody();

        if (program == null || program.getProgramDescription() == null) {
            log.error("Unable to find program rules for ID: {}", command.getProgramId());
            throw new ResourceNotFoundException("Unable to find program rules for ID: " + command.getProgramId());
        }

        ProgramDescription rules = program.getProgramDescription();
        BatteryControl bc = rules.getBatteryControl();
        GridControl gc = rules.getGridControl();

        // 1. Initialize State with Common Fields
        ActiveControlState state = new ActiveControlState();
        state.setEventId(command.getEventId());
        state.setMode(command.getMode());
        state.setGridControl(gc);
        state.setBatteryControl(bc);
        state.setLoadEnergyPriorities(rules.getLoadEnergyOrder());
        state.setSurplusEnergyPriorities(rules.getSurplusEnergyOrder());

        if (command.getDurationMinutes() != null) {
            state.setValidUntil(Instant.now().plus(command.getDurationMinutes(), ChronoUnit.MINUTES));
        }

        // 2. PRE-CALCULATE LIMITS BASED ON MODE
        switch (command.getMode()) {

            case DISCHARGE:
                // Clamp Power: Cannot exceed hardware max discharge
                long safeDischarge = Math.min(command.getTargetPowerW(), bc.getMaxDischargeW());

                // Further clamp against Grid limits (Don't export more than grid allows)
                safeDischarge = (long) Math.min(safeDischarge, gc.getMaxExportW());
                state.setTargetPowerW(safeDischarge);

                // Clamp SoC: Cannot drain below program's absolute minimum (e.g., keep 20% for
                // backup)
                int safeMinSoc = Math.max(command.getTargetSoc(), (int) bc.getMinSocPercent());
                state.setTargetSoc(safeMinSoc);
                break;

            case CHARGE:
                // Clamp Power: Cannot exceed hardware max charge
                long safeCharge = Math.min(command.getTargetPowerW(), bc.getMaxChargeW());

                // Further clamp against Grid limits (Don't import more than grid allows)
                safeCharge = (long) Math.min(safeCharge, gc.getMaxImportW());
                state.setTargetPowerW(safeCharge);

                // Clamp SoC: Cannot charge past program's absolute maximum
                int safeMaxSoc = (int) Math.min(command.getTargetSoc(), bc.getMaxSocPercent());
                state.setTargetSoc(safeMaxSoc);
                break;

            case HOLD:
                // Ignore Admin's requested power, force it to 0
                state.setTargetPowerW(0L);
                state.setTargetSoc(command.getTargetSoc());
                break;

            default:
                log.error("Unknown mode received: {}", command.getMode());
                break;
        }

        // 3. Save to In-Memory Store
        activeControlStore.applyDispatch(command.getSiteId(), state);

        log.debug("Dispatch command sended to simulator successfully");
        simulatorFeignClientService.applyDispatch(activeControlStore.getActiveControls());

        log.info("Reconfigured Site: {}. Pre-calculated Safe Target Power: {}W, Target SoC: {}%",
                command.getSiteId(), state.getTargetPowerW(), state.getTargetSoc());
    }
}
