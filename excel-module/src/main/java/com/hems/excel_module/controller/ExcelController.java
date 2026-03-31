package com.hems.excel_module.controller;

import com.hems.excel_module.model.ExcelImportResult;
import com.hems.excel_module.service.ExcelExportService;
import com.hems.excel_module.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelExportService exportService;
    private final ExcelImportService importService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export() {

        List<String> headers = List.of("Name", "Age");

        List<Map<String, Object>> data = List.of(
                Map.of("Name", "John", "Age", 25),
                Map.of("Name", "Alice", "Age", 30)
        );

        byte[] file = exportService.exportToExcel("Users", headers, data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=test.xlsx")
                .body(file);
    }

    @PostMapping("/import")
    public ExcelImportResult importFile(@RequestParam("file") MultipartFile file) {
        return importService.importFromExcel(file);
    }
}