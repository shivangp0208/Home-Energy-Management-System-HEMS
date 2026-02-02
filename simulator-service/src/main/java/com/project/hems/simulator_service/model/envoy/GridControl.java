package com.project.hems.simulator_service.model.envoy;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class GridControl {

    private boolean allowImport;
    private boolean allowExport;
    private double maxImportW;
    private double maxExportW;
}
