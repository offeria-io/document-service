package offeria.document_service.service.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Excel Document Generator using Apache POI.
 */
@Slf4j
@Component
public class ExcelDocumentGenerator implements DocumentGenerator {

    @Override
    @SuppressWarnings("unchecked")
    public byte[] generate(String templateContent, Map<String, Object> data, Map<String, String> images) {
        log.info("Generating Excel document...");
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("RFQ");
            int rowCount = 0;
            
            // RFQ Header info
            Row headerRow = sheet.createRow(rowCount++);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("RFQ Details");
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerCell.setCellStyle(headerStyle);

            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!(entry.getValue() instanceof List)) {
                    Row row = sheet.createRow(rowCount++);
                    row.createCell(0).setCellValue(entry.getKey());
                    row.createCell(1).setCellValue(String.valueOf(entry.getValue()));
                }
            }

            // RFQ Items
            if (data.containsKey("items") && data.get("items") instanceof List) {
                rowCount++; // Empty row
                Row itemsHeaderRow = sheet.createRow(rowCount++);
                String[] columns = {"Item", "Description", "Qty", "Unit", "Unit Price", "Remark"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = itemsHeaderRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
                for (Map<String, Object> item : items) {
                    Row row = sheet.createRow(rowCount++);
                    row.createCell(0).setCellValue(String.valueOf(item.getOrDefault("name", "")));
                    row.createCell(1).setCellValue(String.valueOf(item.getOrDefault("description", "")));
                    row.createCell(2).setCellValue(String.valueOf(item.getOrDefault("quantity", "0")));
                    row.createCell(3).setCellValue(String.valueOf(item.getOrDefault("unit", "")));
                    row.createCell(4).setCellValue(String.valueOf(item.getOrDefault("unitPrice", "0")));
                    row.createCell(5).setCellValue(String.valueOf(item.getOrDefault("remark", "")));
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error generating Excel: ", e);
            throw new RuntimeException("Excel generation failed", e);
        }
    }

    @Override
    public String getSupportedFormat() {
        return "EXCEL";
    }
}
