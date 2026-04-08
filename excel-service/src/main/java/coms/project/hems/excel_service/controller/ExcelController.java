package coms.project.hems.excel_service.controller;

import com.project.hems.hems_api_contracts.contract.Excel.ExcelImportResult;
import coms.project.hems.excel_service.service.ExcelExportService;
import coms.project.hems.excel_service.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelExportService exportService;
    private final ExcelImportService importService;

//    @PostMapping("/export")
//    public ResponseEntity<byte[]> export(
//            @RequestParam String sheetName,
//            @RequestBody List<Map<String, Object>> data) {
//
//        List<String> headers = List.of("Name", "Age");
//
//        List<Map<String, Object>> data = List.of(
//                Map.of("Name", "John", "Age", 25),
//                Map.of("Name", "Alice", "Age", 30)
//        );
//
//        byte[] file = exportService.exportToExcel("Users", headers, data);
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=test.xlsx")
//                .body(file);
//    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String sheetName,
            @RequestBody List<Map<String, Object>> data) {

        // Generate headers dynamically from data
        List<String> headers = data.isEmpty()
                ? List.of()
                : new ArrayList<>(data.get(0).keySet());

        byte[] file = exportService.exportToExcel(sheetName, headers, data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + sheetName + ".xlsx")
                .body(file);
    }

    @PostMapping("/import")
    public ExcelImportResult importFile(@RequestParam("file") MultipartFile file) {
        return importService.importFromExcel(file);
    }
}