package com.hems.excel_module.config;

import com.hems.excel_module.service.ExcelExportService;
import com.hems.excel_module.service.ExcelImportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.hems.excel_module")
@EnableConfigurationProperties(ExcelProperties.class)
public class ExcelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExcelExportService excelExportService(ExcelProperties excelProperties) {
        return new ExcelExportService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcelImportService excelImportService(ExcelProperties excelProperties) {
        return new ExcelImportService();
    }
}