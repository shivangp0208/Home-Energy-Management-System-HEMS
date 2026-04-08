package com.project.hems.program_enrollment_manager.external;

import com.project.hems.hems_api_contracts.contract.Excel.ExcelImportResult;
import com.project.hems.program_enrollment_manager.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "excel-service",
        path = "/api/excel",
        configuration = FeignConfig.class
)public interface ExcelServiceFeignClient {

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ExcelImportResult importFile(
            @RequestPart("file") MultipartFile file
    );
}