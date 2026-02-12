package com.project.hems.hems_api_contracts.contract.vpp;

public enum VppStrategyMode {
    /** Prioritize meeting targetExportW (export contract / grid sell). */
    EXPORT_FOCUS,

    /** Prioritize serving site/load demand first, export only surplus. */
    SELF_CONSUMPTION,

    /**
     * Simple arbitrage: charge battery when price is cheap, discharge/export when expensive.
     * (We simulate price windows using time-of-day; replace with real price signal later.)
     */
    ARBITRAGE
}
