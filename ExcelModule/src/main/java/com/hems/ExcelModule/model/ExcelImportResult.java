package com.hems.ExcelModule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelImportResult {

    private boolean success;
    private String fileName;
    private int totalRows;

    private List<Map<String, Object>> data = new ArrayList<>();   // ✅ FIX
    private List<String> errors = new ArrayList<>();              // ✅ FIX
    private List<String> warnings = new ArrayList<>();            // ✅ FIX
}