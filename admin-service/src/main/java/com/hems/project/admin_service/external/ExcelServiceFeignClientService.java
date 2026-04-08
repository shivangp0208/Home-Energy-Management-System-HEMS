package com.hems.project.admin_service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "excel-service", path = "/api/excel")
public interface ExcelServiceFeignClientService {

    @PostMapping("/export")
    ResponseEntity<byte[]> export(
            @RequestParam String sheetName,
            @RequestBody List<Map<String, Object>> data
    );
}