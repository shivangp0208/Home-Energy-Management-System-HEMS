package com.hems.project.Virtual_Power_Plant.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;
import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class VppGenerationSimulator {

    private static final double DELTA_SECONDS = 5.0;
    private static final double SECONDS_TO_HOURS = 1.0 / 3600.0;

    /**
     * ✅ MAIN ENTRY POINT (called by VppSimulationService every 5 seconds)
     */
    public VppSnapshot nextSnapshot(VppSnapshot current, double maxCapacityW) {
        if (current == null) return null;

        final double dtHours = DELTA_SECONDS / 3600.0;

        // If no capacity, still accumulate import/export based on current gridPower (optional)
        if (maxCapacityW <= 0) {
            Double value = current.getGridPowerW();
            double gridW = (value != null) ? value : 0.0;
            double exportW = Math.max(0.0, -gridW);
            double importW = Math.max(0.0, gridW);

            double totalGeneratedKwh = safe(current.getTotalGeneratedKwh());
            double totalExportKwh = safe(current.getTotalExportKwh()) + (exportW * dtHours) / 1000.0;
            double totalImportKwh = safe(current.getTotalImportKwh()) + (importW * dtHours) / 1000.0;

            return current.toBuilder()
                    .timestamp(LocalDateTime.now())
                    .autoMode(current.isAutoMode())

                    .solarW(0).coalW(0).nuclearW(0).thermalW(0)
                    .totalGenerationW(0)

                    .batteryPowerW(0)
                    .gridPowerW(0)

                    .totalGeneratedKwh(totalGeneratedKwh) // stays same (no generation)
                    .totalExportKwh(totalExportKwh)
                    .totalImportKwh(totalImportKwh)
                    .build();
        }

        GenerationMode mode = current.getMode();

        // 1) generation breakdown
        GenerationBreakdown g = computeGenerationBreakdown(mode, maxCapacityW);
        double totalGenW = g.solarW + g.coalW + g.nuclearW + g.thermalW;

        // 2) battery + grid flow
        BatteryGridFlow f = computeBatteryAndGridFlow(current, totalGenW);

        // 3) accumulate ENERGY counters (kWh)
        double exportW = Math.max(0.0, -f.gridPowerW);
        double importW = Math.max(0.0,  f.gridPowerW);

        double totalGeneratedKwh = safe(current.getTotalGeneratedKwh()) + (totalGenW * dtHours) / 1000.0;
        double totalExportKwh    = safe(current.getTotalExportKwh())    + (exportW   * dtHours) / 1000.0;
        double totalImportKwh    = safe(current.getTotalImportKwh())    + (importW   * dtHours) / 1000.0;

        VppSnapshot updated = current.toBuilder()
                .timestamp(LocalDateTime.now())
                .autoMode(current.isAutoMode()) // keep whatever your flag is

                .solarW(g.solarW)
                .coalW(g.coalW)
                .nuclearW(g.nuclearW)
                .thermalW(g.thermalW)
                .totalGenerationW(totalGenW)

                .batteryPowerW(f.batteryPowerW)
                .gridPowerW(f.gridPowerW)
                .batteryRemainingWh(f.batteryRemainingWh)
                .batterySoc(f.batterySoc)

                .totalGeneratedKwh(totalGeneratedKwh)
                .totalExportKwh(totalExportKwh)
                .totalImportKwh(totalImportKwh)
                .build();

        // Helpful debug: how much export came from battery (only when batteryPowerW is negative)
        double batteryExportW = Math.max(0.0, -f.batteryPowerW);
        log.debug("VPP SNAPSHOT | vppId={} mode={} gen={}W export={}W import={}W batt={}W battExport={}W soc={}%, totals: gen={}kWh exp={}kWh imp={}kWh",
                updated.getVppId(),
                updated.getMode(),
                updated.getTotalGenerationW(),
                exportW,
                importW,
                updated.getBatteryPowerW(),
                batteryExportW,
                updated.getBatterySoc(),
                updated.getTotalGeneratedKwh(),
                updated.getTotalExportKwh(),
                updated.getTotalImportKwh());

        return updated;
    }

    private double safe(Double v) {
        return v == null ? 0.0 : v;
    }


    // -------------------------
    // GENERATION BREAKDOWN LOGIC
    // -------------------------

    private GenerationBreakdown computeGenerationBreakdown(GenerationMode mode, double maxCapacityW) {
        //each per thread enu personal random madse ene and we bound value to 0.95<=value<1.05
        double noise = ThreadLocalRandom.current().nextDouble(0.95, 1.05);

        return switch (mode) {
            case SOLAR_ONLY -> {
                double solar = solarCurve(maxCapacityW) * noise;
                yield normalizeToCapacity(new GenerationBreakdown(solar, 0, 0, 0), maxCapacityW);
            }
            case COAL_ONLY -> {
                double coal = maxCapacityW * 0.75 * noise;
                yield normalizeToCapacity(new GenerationBreakdown(0, coal, 0, 0), maxCapacityW);
            }
            case NUCLEAR_ONLY -> {
                double nuclear = maxCapacityW * 0.90 * noise;
                yield normalizeToCapacity(new GenerationBreakdown(0, 0, nuclear, 0), maxCapacityW);
            }
            case THERMAL_ONLY -> {
                double thermal = maxCapacityW * 0.65 * noise;
                yield normalizeToCapacity(new GenerationBreakdown(0, 0, 0, thermal), maxCapacityW);
            }
            case AUTO -> autoMix(maxCapacityW, noise);
            default -> throw new IllegalArgumentException("Unexpected value: " + mode);
        };
    }

    /**
     * AUTO: Day -> solar + coal, Night -> nuclear + thermal
     */
    private GenerationBreakdown autoMix(double maxCapacityW, double noise) {
        int hour = LocalTime.now().getHour();

        if (hour >= 6 && hour < 18) {
            // day
            double solar = solarCurve(maxCapacityW) * noise;
            double coal = maxCapacityW * 0.35 * noise;
            return normalizeToCapacity(new GenerationBreakdown(solar, coal, 0, 0), maxCapacityW);
        } else {
            // night
            double nuclear = maxCapacityW * 0.75 * noise;
            double thermal = maxCapacityW * 0.20 * noise;
            return normalizeToCapacity(new GenerationBreakdown(0, 0, nuclear, thermal), maxCapacityW);
        }
    }

    private double solarCurve(double maxCapacityW) {
        int hour = LocalTime.now().getHour();
        if (hour < 6 || hour >= 18) return 0.0;

        double peakW = maxCapacityW * 0.95;
        double radians = Math.PI * (hour - 6) / 12.0;
        return peakW * Math.sin(radians);
    }

    /**
     * Ensure breakdown never exceeds maxCapacityW
     */
    private GenerationBreakdown normalizeToCapacity(GenerationBreakdown g, double maxCapacityW) {
        double total = g.solarW + g.coalW + g.nuclearW + g.thermalW;
        if (total <= 0) return g;
        if (total <= maxCapacityW) return g;//already max ni najik j che

        //if max ni najik na hoy toh ene banavo max ni najik total ne..
        double scale = maxCapacityW / total;
        return new GenerationBreakdown(
                g.solarW * scale,
                g.coalW * scale,
                g.nuclearW * scale,
                g.thermalW * scale
        );
    }

    // -------------------------
    // BATTERY + GRID LOGIC
    // -------------------------

    private BatteryGridFlow computeBatteryAndGridFlow(VppSnapshot current, double totalGenW) {

        double targetExportW = Math.max(0, current.getTargetExportW());

        double batteryCapacityWh = Math.max(0, current.getBatteryCapacityWh());
        double batteryRemainingWh = clamp(current.getBatteryRemainingWh(), 0, batteryCapacityWh);

        // Convention:
        // gridPowerW < 0 => export
        // gridPowerW > 0 => import
        // batteryPowerW > 0 => charging
        // batteryPowerW < 0 => discharging

        double gridPowerW = 0.0;
        double batteryPowerW = 0.0;

        // 1) Meet export target using generation first
        double exportFromGenW = Math.min(totalGenW, targetExportW);
        gridPowerW = -exportFromGenW;

        double remainingGenAfterExportW = totalGenW - exportFromGenW;
        double exportStillNeededW = targetExportW - exportFromGenW;

        // 2) If still need export, discharge battery
        if (exportStillNeededW > 0 && batteryCapacityWh > 0 && batteryRemainingWh > 0) {
            double dischargeW = Math.min(3000.0, exportStillNeededW); // limit discharge rate
            double dischargeWh = dischargeW * DELTA_SECONDS * SECONDS_TO_HOURS;

            // if battery doesn't have enough energy
            dischargeWh = Math.min(dischargeWh, batteryRemainingWh);

            double actualDischargeW = dischargeWh / (DELTA_SECONDS * SECONDS_TO_HOURS);

            batteryPowerW = -actualDischargeW;
            batteryRemainingWh -= dischargeWh;

            // export increases
            gridPowerW = -(exportFromGenW + actualDischargeW);
        }

        // 3) If surplus generation exists after export, charge battery
        if (remainingGenAfterExportW > 0 && batteryCapacityWh > 0 && batteryRemainingWh < batteryCapacityWh) {
            double chargeW = Math.min(3000.0, remainingGenAfterExportW); // limit charge rate
            double chargeWh = chargeW * DELTA_SECONDS * SECONDS_TO_HOURS;

            double freeWh = batteryCapacityWh - batteryRemainingWh;
            chargeWh = Math.min(chargeWh, freeWh);

            double actualChargeW = chargeWh / (DELTA_SECONDS * SECONDS_TO_HOURS);

            batteryPowerW = actualChargeW; // charging overrides discharge in surplus scenario
            batteryRemainingWh += chargeWh;
        }

        int soc = (batteryCapacityWh <= 0) ? 0 : (int) Math.round((batteryRemainingWh / batteryCapacityWh) * 100);

        return new BatteryGridFlow(batteryPowerW, gridPowerW, batteryRemainingWh, clampInt(soc, 0, 100));
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private int clampInt(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    // Small records for internal logic
    private record GenerationBreakdown(double solarW, double coalW, double nuclearW, double thermalW) {}

    private record BatteryGridFlow(double batteryPowerW, double gridPowerW,
                                   double batteryRemainingWh, int batterySoc) {}
}
