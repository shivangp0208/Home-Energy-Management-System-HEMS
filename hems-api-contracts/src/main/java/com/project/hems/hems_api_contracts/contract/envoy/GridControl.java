package com.project.hems.hems_api_contracts.contract.envoy;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridControl {

    private boolean allowImport;
    private boolean allowExport;
    private double maxImportW;
    private double maxExportW;
}
