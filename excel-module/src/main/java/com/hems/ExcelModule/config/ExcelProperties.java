package com.hems.ExcelModule.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "excel")
public class ExcelProperties {

    private int maxRowsPerSheet = 65536;
    private boolean enableFormatting = true;
    private int maxFileSizeInMB = 50;
    private boolean validateHeaders = true;
    private String temporaryDirectory = "/tmp/hems-excel";
}