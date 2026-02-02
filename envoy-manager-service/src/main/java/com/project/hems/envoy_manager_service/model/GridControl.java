package com.project.hems.envoy_manager_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridControl {

    private boolean allowImport;
    private boolean allowExport;
    private double maxImportW;
    private double maxExportW;
}
