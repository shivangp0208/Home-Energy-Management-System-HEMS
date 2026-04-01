package com.hems.project.virtual_power_plant.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import com.project.hems.hems_api_contracts.contract.vpp.GenerationMode;
import com.project.hems.hems_api_contracts.contract.vpp.VppSnapshot;
import com.project.hems.hems_api_contracts.contract.vpp.VppStrategyMode;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class
VppGenerationSimulator {


    private static final double DELTA_SECONDS = 5.0;
    private static final double DT_HOURS = DELTA_SECONDS / 3600.0;

    // Battery power limits (W)
    private static final double MAX_CHARGE_W = 3000.0;
    private static final double MAX_DISCHARGE_W = 3000.0;

    // Arbitrage thresholds (simple)
    private static final int CHEAP_START_HOUR = 0;   // 00:00
    private static final int CHEAP_END_HOUR = 6;     // 06:00
    private static final int EXPENSIVE_START_HOUR = 18; // 18:00
    private static final int EXPENSIVE_END_HOUR = 23;   // 23:00

    // SOC guardrails for arbitrage
    private static final int ARB_MIN_SOC = 20; // don't drain below 20%
    private static final int ARB_TARGET_SOC = 90; // stop charging near 90%

    public VppSnapshot nextSnapshot(VppSnapshot current, double maxCapacityW) {
        if (current == null) return null;


        VppStrategyMode strategyMode =
                current.getStrategyMode() != null ? current.getStrategyMode() : VppStrategyMode.EXPORT_FOCUS;

        // If no capacity -> no generation; still allow import/export accumulation if you want
        if (maxCapacityW <= 0) {
            double gridW = safe(current.getGridPowerW());
            double exportW = Math.max(0.0, -gridW);
            double importW = Math.max(0.0, gridW);

            return current.toBuilder()
                    .timestamp(LocalDateTime.now())
                    .solarW(0).coalW(0).nuclearW(0).thermalW(0)
                    .totalGenerationW(0)
                    .batteryPowerW(0)
                    .gridPowerW(0)
                    .totalExportKwh(safe(current.getTotalExportKwh()) + (exportW * DT_HOURS) / 1000.0)
                    .totalImportKwh(safe(current.getTotalImportKwh()) + (importW * DT_HOURS) / 1000.0)
                    .strategyMode(strategyMode)
                    .build();
        }

        // 1) Generate power based on GenerationMode
        GenerationBreakdown g = computeGenerationBreakdown(current.getMode(), maxCapacityW);
        double totalGenW = g.solarW + g.coalW + g.nuclearW + g.thermalW;

        // 2) Route power based on StrategyMode
        BatteryGridFlow flow = switch (strategyMode) {
            case EXPORT_FOCUS -> exportFocusFlow(current, totalGenW);
            case SELF_CONSUMPTION -> selfConsumptionFlow(current, totalGenW);
            case ARBITRAGE -> arbitrageFlow(current, totalGenW);
        };

        // 3) Accumulate kWh counters
        double exportW = Math.max(0.0, -flow.gridPowerW);
        double importW = Math.max(0.0, flow.gridPowerW);

        double totalGeneratedKwh = safe(current.getTotalGeneratedKwh()) + (totalGenW * DT_HOURS) / 1000.0;
        double totalExportKwh = safe(current.getTotalExportKwh()) + (exportW * DT_HOURS) / 1000.0;
        double totalImportKwh = safe(current.getTotalImportKwh()) + (importW * DT_HOURS) / 1000.0;

        VppSnapshot updated = current.toBuilder()
                .timestamp(LocalDateTime.now())
                .strategyMode(strategyMode)

                .solarW(g.solarW)
                .coalW(g.coalW)
                .nuclearW(g.nuclearW)
                .thermalW(g.thermalW)
                .totalGenerationW(totalGenW)

                .batteryPowerW(flow.batteryPowerW)
                .gridPowerW(flow.gridPowerW)
                .batteryRemainingWh(flow.batteryRemainingWh)
                .batterySoc(flow.batterySoc)

                .totalGeneratedKwh(totalGeneratedKwh)
                .totalExportKwh(totalExportKwh)
                .totalImportKwh(totalImportKwh)
                .build();


        return updated;
    }

    // =========================================================
    //  STRATEGY 1: EXPORT_FOCUS (meet targetExportW first)
    // =========================================================
    private BatteryGridFlow exportFocusFlow(VppSnapshot current, double totalGenW) {

        double targetExportW = Math.max(0.0, safe(current.getTargetExportW()));

        BatteryState b = batteryState(current);

        double gridPowerW = 0.0;     // + import / - export
        double batteryPowerW = 0.0;  // + charge / - discharge

        // Step 1: export from generation
        double exportFromGenW = Math.min(totalGenW, targetExportW);
        gridPowerW = -exportFromGenW;

        double remainingGenW = totalGenW - exportFromGenW;
        double exportStillNeededW = targetExportW - exportFromGenW;

        // Step 2: if still need export -> discharge battery
        if (exportStillNeededW > 0.0 && b.canDischarge()) {
            BatteryTransfer discharge = dischargeBattery(b, Math.min(MAX_DISCHARGE_W, exportStillNeededW));
            batteryPowerW = -discharge.actualW;
            b.remainingWh = discharge.newRemainingWh;

            // export increases
            gridPowerW = -(exportFromGenW + discharge.actualW);
        }

        // Step 3: if generation surplus -> charge battery
        if (remainingGenW > 0.0 && b.canCharge()) {
            BatteryTransfer charge = chargeBattery(b, Math.min(MAX_CHARGE_W, remainingGenW));
            batteryPowerW = charge.actualW; // charging
            b.remainingWh = charge.newRemainingWh;
        }

        int soc = computeSoc(b.capacityWh, b.remainingWh);
        return new BatteryGridFlow(batteryPowerW, gridPowerW, b.remainingWh, soc);
    }

    // =========================================================
    //  STRATEGY 2: SELF_CONSUMPTION (serve siteDemandW first)
    // =========================================================
    private BatteryGridFlow selfConsumptionFlow(VppSnapshot current, double totalGenW) {

        double siteDemandW = Math.max(0.0, safe(current.getSiteDemandW()));
        double targetExportW = Math.max(0.0, safe(current.getTargetExportW())); // optional export goal after demand

        BatteryState b = batteryState(current);

        double gridPowerW = 0.0;
        double batteryPowerW = 0.0;

        // Step 1: use generation to serve demand
        double servedByGenW = Math.min(totalGenW, siteDemandW);
        double remainingDemandW = siteDemandW - servedByGenW;
        double remainingGenW = totalGenW - servedByGenW;

        // Step 2: if demand still remains -> discharge battery to serve demand (avoid import)
        if (remainingDemandW > 0.0 && b.canDischarge()) {
            BatteryTransfer discharge = dischargeBattery(b, Math.min(MAX_DISCHARGE_W, remainingDemandW));
            batteryPowerW = -discharge.actualW;
            b.remainingWh = discharge.newRemainingWh;

            remainingDemandW -= discharge.actualW;
        }

        // Step 3: if demand still remains -> import from grid
        if (remainingDemandW > 0.0) {
            gridPowerW += remainingDemandW; // import positive
            remainingDemandW = 0.0;
        }

        // Step 4: after demand met, optionally export up to targetExportW using remaining generation
        if (targetExportW > 0.0) {
            double exportFromGenW = Math.min(remainingGenW, targetExportW);
            gridPowerW -= exportFromGenW; // export is negative
            remainingGenW -= exportFromGenW;

            // If still need export after remainingGen, can discharge battery (optional)
            double exportStillNeededW = targetExportW - exportFromGenW;
            if (exportStillNeededW > 0.0 && b.canDischarge()) {
                BatteryTransfer discharge = dischargeBattery(b, Math.min(MAX_DISCHARGE_W, exportStillNeededW));
                // if batteryPowerW already charging/discharging earlier, merge carefully:
                batteryPowerW = mergeBatteryPower(batteryPowerW, -discharge.actualW);
                b.remainingWh = discharge.newRemainingWh;

                gridPowerW -= discharge.actualW;
            }
        }

        // Step 5: if generation still surplus -> charge battery
        if (remainingGenW > 0.0 && b.canCharge()) {
            BatteryTransfer charge = chargeBattery(b, Math.min(MAX_CHARGE_W, remainingGenW));
            batteryPowerW = mergeBatteryPower(batteryPowerW, charge.actualW);
            b.remainingWh = charge.newRemainingWh;
        }

        int soc = computeSoc(b.capacityWh, b.remainingWh);
        return new BatteryGridFlow(batteryPowerW, gridPowerW, b.remainingWh, soc);
    }

    // =========================================================
    //  STRATEGY 3: ARBITRAGE (charge cheap, discharge expensive)
    // =========================================================
    private BatteryGridFlow arbitrageFlow(VppSnapshot current, double totalGenW) {

        BatteryState b = batteryState(current);

        // Simulate “price” windows using hour; replace with real grid price signal later
        int hour = LocalTime.now().getHour();
        boolean cheap = hour >= CHEAP_START_HOUR && hour < CHEAP_END_HOUR;
        boolean expensive = hour >= EXPENSIVE_START_HOUR && hour <= EXPENSIVE_END_HOUR;

        double siteDemandW = Math.max(0.0, safe(current.getSiteDemandW()));
        double targetExportW = Math.max(0.0, safe(current.getTargetExportW()));

        double gridPowerW = 0.0;
        double batteryPowerW = 0.0;

        // Base: serve site demand first (same as self-consumption)
        double servedByGenW = Math.min(totalGenW, siteDemandW);
        double remainingDemandW = siteDemandW - servedByGenW;
        double remainingGenW = totalGenW - servedByGenW;

        // If demand remains, discharge battery (but don’t go below ARB_MIN_SOC)
        int socNow = computeSoc(b.capacityWh, b.remainingWh);

        if (remainingDemandW > 0.0 && b.canDischarge() && socNow > ARB_MIN_SOC) {
            BatteryTransfer discharge = dischargeBattery(b, Math.min(MAX_DISCHARGE_W, remainingDemandW));
            batteryPowerW = -discharge.actualW;
            b.remainingWh = discharge.newRemainingWh;

            remainingDemandW -= discharge.actualW;
        }

        // If still demand -> import
        if (remainingDemandW > 0.0) {
            gridPowerW += remainingDemandW;
            remainingDemandW = 0.0;
        }

        // Cheap window: prefer charging battery (even importing to charge if enabled)
        if (cheap && current.getStrategyMode() == VppStrategyMode.ARBITRAGE) {
            int soc = computeSoc(b.capacityWh, b.remainingWh);

            // First, charge from remaining generation surplus
            if (remainingGenW > 0.0 && b.canCharge() && soc < ARB_TARGET_SOC) {
                BatteryTransfer charge = chargeBattery(b, Math.min(MAX_CHARGE_W, remainingGenW));
                batteryPowerW = mergeBatteryPower(batteryPowerW, charge.actualW);
                b.remainingWh = charge.newRemainingWh;
                remainingGenW -= charge.actualW;
            }

            // If still want charging and no surplus, import to charge (optional)
            soc = computeSoc(b.capacityWh, b.remainingWh);
            if (b.canCharge() && soc < ARB_TARGET_SOC) {
                double wantedChargeW = MAX_CHARGE_W;
                BatteryTransfer charge = chargeBattery(b, wantedChargeW);
                // If battery was already charging from gen, merge; else charge from import
                batteryPowerW = mergeBatteryPower(batteryPowerW, charge.actualW);
                b.remainingWh = charge.newRemainingWh;

                // Import power for charging (if not fully covered by gen)
                // Here we assume this charge comes from grid.
                gridPowerW += charge.actualW;
            }
        }

        // Expensive window: prefer discharging/exporting (after meeting demand)
        if (expensive) {
            int soc = computeSoc(b.capacityWh, b.remainingWh);

            // export from remaining generation first up to targetExportW
            double exportFromGenW = Math.min(remainingGenW, targetExportW);
            gridPowerW -= exportFromGenW;
            remainingGenW -= exportFromGenW;

            double exportStillNeededW = targetExportW - exportFromGenW;

            // If still need export, discharge battery (only if SOC above min)
            if (exportStillNeededW > 0.0 && b.canDischarge() && soc > ARB_MIN_SOC) {
                BatteryTransfer discharge = dischargeBattery(b, Math.min(MAX_DISCHARGE_W, exportStillNeededW));
                batteryPowerW = mergeBatteryPower(batteryPowerW, -discharge.actualW);
                b.remainingWh = discharge.newRemainingWh;

                gridPowerW -= discharge.actualW;
            }

            // If still surplus gen after export -> you can export it all (or charge battery; your choice)
            // Here: export all remaining gen (sell high).
            if (remainingGenW > 0.0) {
                gridPowerW -= remainingGenW;
                remainingGenW = 0.0;
            }

        } else {
            // Normal hours: after meeting demand, charge battery with surplus
            if (remainingGenW > 0.0 && b.canCharge()) {
                BatteryTransfer charge = chargeBattery(b, Math.min(MAX_CHARGE_W, remainingGenW));
                batteryPowerW = mergeBatteryPower(batteryPowerW, charge.actualW);
                b.remainingWh = charge.newRemainingWh;
                remainingGenW -= charge.actualW;
            }

            // Optionally export remaining surplus if you want (here we export only if targetExportW set)
            if (targetExportW > 0.0 && remainingGenW > 0.0) {
                double export = Math.min(remainingGenW, targetExportW);
                gridPowerW -= export;
                remainingGenW -= export;
            }
        }

        int soc = computeSoc(b.capacityWh, b.remainingWh);

        return new BatteryGridFlow(batteryPowerW, gridPowerW, b.remainingWh, soc);
    }

    // =========================================================
    // Battery helper logic
    // =========================================================

    private BatteryState batteryState(VppSnapshot current) {
        double capacityWh = Math.max(0.0, safe(current.getBatteryCapacityWh()));
        double remainingWh = clamp(safe(current.getBatteryRemainingWh()), 0.0, capacityWh);
        return new BatteryState(capacityWh, remainingWh);
    }

    private BatteryTransfer chargeBattery(BatteryState b, double requestedW) {
        if (requestedW <= 0 || b.capacityWh <= 0) return new BatteryTransfer(0, b.remainingWh);

        double freeWh = b.capacityWh - b.remainingWh;
        if (freeWh <= 0) return new BatteryTransfer(0, b.remainingWh);

        double requestedWh = (requestedW * DT_HOURS); // W * h = Wh
        double actualWh = Math.min(requestedWh, freeWh);
        double actualW = actualWh / DT_HOURS;

        return new BatteryTransfer(actualW, b.remainingWh + actualWh);
    }

    private BatteryTransfer dischargeBattery(BatteryState b, double requestedW) {
        if (requestedW <= 0 || b.capacityWh <= 0) return new BatteryTransfer(0, b.remainingWh);
        if (b.remainingWh <= 0) return new BatteryTransfer(0, b.remainingWh);

        double requestedWh = (requestedW * DT_HOURS);
        double actualWh = Math.min(requestedWh, b.remainingWh);
        double actualW = actualWh / DT_HOURS;

        return new BatteryTransfer(actualW, b.remainingWh - actualWh);
    }


    private boolean nearlyZero(double v) {

        return Math.abs(v) < 1e-9;
    }

    /**
     * If you previously discharged and now want to charge (or opposite),
     * we choose the later operation to dominate. This keeps numbers stable.
     */
    private double mergeBatteryPower(double existingW, double nextW) {
        if (nearlyZero(existingW)) return nextW;
        // If both same direction, add them; if opposite direction, keep nextW (last action wins)
        if ((existingW > 0 && nextW > 0) || (existingW < 0 && nextW < 0)) {
            return existingW + nextW;
        }
        return nextW;
    }

    private int computeSoc(double capWh, double remWh) {
        if (capWh <= 0) return 0;
        return (int) Math.round((remWh / capWh) * 100.0);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double safe(Double v) {
        return v == null ? 0.0 : v;
    }

    // =========================================================
    // GENERATION BREAKDOWN (your existing logic)
    // =========================================================

    private GenerationBreakdown computeGenerationBreakdown(GenerationMode mode, double maxCapacityW) {
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
            default -> throw new IllegalArgumentException("Unexpected mode: " + mode);
        };
    }

    private GenerationBreakdown autoMix(double maxCapacityW, double noise) {
        int hour = LocalTime.now().getHour();
        if (hour >= 6 && hour < 18) {
            double solar = solarCurve(maxCapacityW) * noise;
            double coal = maxCapacityW * 0.35 * noise;
            return normalizeToCapacity(new GenerationBreakdown(solar, coal, 0, 0), maxCapacityW);
        } else {
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

    private GenerationBreakdown normalizeToCapacity(GenerationBreakdown g, double maxCapacityW) {
        double total = g.solarW + g.coalW + g.nuclearW + g.thermalW;
        if (total <= 0) return g;
        if (total <= maxCapacityW) return g;

        double scale = maxCapacityW / total;
        return new GenerationBreakdown(
                g.solarW * scale,
                g.coalW * scale,
                g.nuclearW * scale,
                g.thermalW * scale
        );
    }

    // =========================================================
    // small records
    // =========================================================
    private record GenerationBreakdown(double solarW, double coalW, double nuclearW, double thermalW) {}
    private record BatteryGridFlow(double batteryPowerW, double gridPowerW, double batteryRemainingWh, int batterySoc) {}
    private static class BatteryState {
        final double capacityWh;
        double remainingWh;

        BatteryState(double capacityWh, double remainingWh) {
            this.capacityWh = capacityWh;
            this.remainingWh = remainingWh;
        }

        boolean canCharge() {
            return capacityWh > 0 && remainingWh < capacityWh;
        }

        boolean canDischarge() {
            return capacityWh > 0 && remainingWh > 0;
        }
    }

    private record BatteryTransfer(double actualW, double newRemainingWh) {}
}
