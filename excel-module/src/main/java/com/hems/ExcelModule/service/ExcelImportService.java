package com.hems.excel_module.service;

import com.hems.excel_module.model.ExcelImportResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service for importing data from Excel files
 * Supports multiple sheets and data validation
 */
@Service
@Slf4j
public class ExcelImportService {

    private static final int MAX_FILE_SIZE = 52428800;  // 50MB
    private static final int MAX_ROWS = 100000;

    /**
     * Import Excel file and extract data
     *
     * @param file Multipart file
     * @return List of maps containing row data
     */
    public ExcelImportResult importFromExcel(MultipartFile file) {
        ExcelImportResult result = new ExcelImportResult();

        try {
            // Validate file
            validateFile(file);
            result.setFileName(file.getOriginalFilename());

            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);  // Get first sheet

            List<String> headers = extractHeaders(sheet);
            List<Map<String, Object>> data = new ArrayList<>();

            int rowCount = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                if (rowCount >= MAX_ROWS) {
                    result.getWarnings().add("Maximum row limit (" + MAX_ROWS + ") reached. Remaining rows ignored.");
                    break;
                }

                try {
                    Map<String, Object> rowData = extractRowData(row, headers);
                    if (!rowData.isEmpty()) {
                        data.add(rowData);
                        rowCount++;
                    }
                } catch (Exception e) {
                    result.getErrors().add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }

            result.setData(data);
            result.setTotalRows(rowCount);
            result.setSuccess(result.getErrors().isEmpty());

            log.info("Successfully imported {} rows from Excel file: {}", rowCount, file.getOriginalFilename());

        } catch (IOException e) {
            log.error("Error importing Excel file: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.getErrors().add("File read error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Import with template validation
     */
    public ExcelImportResult importFromExcelWithValidation(MultipartFile file, List<String> expectedHeaders) {
        ExcelImportResult result = importFromExcel(file);

        if (!result.isSuccess()) {
            return result;
        }

        // Validate headers
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            List<String> actualHeaders = extractHeaders(sheet);

            if (!actualHeaders.equals(expectedHeaders)) {
                result.setSuccess(false);
                result.getErrors().add("Template mismatch. Expected headers: " + expectedHeaders +
                        ", but got: " + actualHeaders);
            }
        } catch (IOException e) {
            result.setSuccess(false);
            result.getErrors().add("Error validating template: " + e.getMessage());
        }

        return result;
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds maximum limit of 50MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.contains("spreadsheet") && !contentType.contains("excel"))) {
            throw new IOException("Invalid file type. Please upload an Excel file.");
        }
    }

    private List<String> extractHeaders(Sheet sheet) {
        List<String> headers = new ArrayList<>();
        Row headerRow = sheet.getRow(0);

        if (headerRow != null) {
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headers.add(cell != null ? cell.getStringCellValue() : "Column_" + i);
            }
        }

        return headers;
    }

    private Map<String, Object> extractRowData(Row row, List<String> headers) {
        Map<String, Object> rowData = new LinkedHashMap<>();

        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.getCell(i);
            Object value = getCellValue(cell);

            if (value != null) {
                rowData.put(headers.get(i), value);
            }
        }

        return rowData;
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
