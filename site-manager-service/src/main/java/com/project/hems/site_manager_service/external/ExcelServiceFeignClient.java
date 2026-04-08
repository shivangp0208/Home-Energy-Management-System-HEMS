package com.project.hems.site_manager_service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "excel-service", path = "/api/excel")
public interface ExcelServiceFeignClient {

    @PostMapping("/export")
    ResponseEntity<byte[]> export(
            @RequestParam String sheetName,
            @RequestBody List<Map<String, Object>> data
    );
}