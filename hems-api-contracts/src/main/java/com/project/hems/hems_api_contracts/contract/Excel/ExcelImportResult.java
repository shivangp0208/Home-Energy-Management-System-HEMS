package com.project.hems.hems_api_contracts.contract.Excel;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExcelImportResult {

    private boolean success;
    private List<Map<String, Object>> data;
    private List<String> errors;
    private List<String> warnings;
    private int totalRows;
    private String fileName;
}