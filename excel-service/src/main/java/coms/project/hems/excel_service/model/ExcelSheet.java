package coms.project.hems.excel_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents an Excel sheet with headers and data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelSheet {
    private String name;
    private List<String> headers;
    private List<Map<String, Object>> data;
}
