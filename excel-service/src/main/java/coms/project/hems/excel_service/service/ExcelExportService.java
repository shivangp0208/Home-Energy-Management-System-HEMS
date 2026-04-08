package coms.project.hems.excel_service.service;

import coms.project.hems.excel_service.model.ExcelSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for exporting data to Excel files using Apache POI
 * Supports multiple sheets, styling, and efficient streaming
 */
@Service
@Slf4j
public class ExcelExportService {

    /**
     * Export list of data to Excel workbook
     * @param sheetName Name of the sheet
     * @param headers Column headers
     * @param data List of maps containing row data
     * @return Byte array of Excel file
     */
    public byte[] exportToExcel(String sheetName, List<String> headers, List<Map<String, Object>> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256);  // Set column width
            }

            // Create data rows
            CellStyle dateStyle = createDateStyle(workbook);
            int rowNum = 1;
            for (Map<String, Object> row : data) {
                Row dataRow = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = dataRow.createCell(i);
                    String header = headers.get(i);
                    Object value = row.get(header);

                    if (value != null) {
                        setCellValue(cell, value, dateStyle);
                    }
                }
            }

            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            log.info("Excel export completed for sheet: {} with {} rows", sheetName, data.size());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error exporting to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export data to Excel", e);
        }
    }
    public byte[] export(String sheetName, List<Map<String, Object>> data) throws IOException {

        if (data == null || data.isEmpty()) {
            throw new RuntimeException("Excel data is empty");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // headers
        Row headerRow = sheet.createRow(0);
        List<String> headers = new ArrayList<>(data.get(0).keySet());

        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        // data rows
        int rowIndex = 1;
        for (Map<String, Object> rowData : data) {
            Row row = sheet.createRow(rowIndex++);
            int colIndex = 0;

            for (String key : headers) {
                Object value = rowData.get(key);
                row.createCell(colIndex++)
                        .setCellValue(value != null ? value.toString() : "");
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    /**
     * Export data to multiple sheets in one workbook
     */
    public byte[] exportMultipleSheets(Map<String, ExcelSheet> sheets) {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            for (Map.Entry<String, ExcelSheet> entry : sheets.entrySet()) {
                createSheet(workbook, entry.getKey(), entry.getValue(), headerStyle, dateStyle);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            log.info("Multi-sheet Excel export completed with {} sheets", sheets.size());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error exporting multiple sheets to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export data to Excel", e);
        }
    }

    private void createSheet(Workbook workbook, String sheetName, ExcelSheet excelSheet,
                             CellStyle headerStyle, CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < excelSheet.getHeaders().size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(excelSheet.getHeaders().get(i));
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        // Create data rows
        int rowNum = 1;
        for (Map<String, Object> row : excelSheet.getData()) {
            Row dataRow = sheet.createRow(rowNum++);
            for (int i = 0; i < excelSheet.getHeaders().size(); i++) {
                Cell cell = dataRow.createCell(i);
                Object value = row.get(excelSheet.getHeaders().get(i));

                if (value != null) {
                    setCellValue(cell, value, dateStyle);
                }
            }
        }
    }

    private void setCellValue(Cell cell, Object value, CellStyle dateStyle) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            cell.setCellStyle(dateStyle);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper helper = workbook.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }
}
