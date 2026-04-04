package offeria.document_service.service.generator;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * PDF Document Generator using iText 7.
 */
@Slf4j
@Component
public class PdfDocumentGenerator implements DocumentGenerator {

    @Override
    @SuppressWarnings("unchecked")
    public byte[] generate(String templateContent, Map<String, Object> data, Map<String, String> images) {
        log.info("Generating PDF document...");
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Company Header (Simplified "Company Form")
            document.add(new Paragraph("COMPANY NAME").setBold().setFontSize(20));
            document.add(new Paragraph("Company Address, Contact Info"));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("RFQ / Quotation").setBold().setFontSize(16));
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!(entry.getValue() instanceof List)) {
                    document.add(new Paragraph(entry.getKey() + ": " + entry.getValue()));
                }
            }
            
            if (data.containsKey("items") && data.get("items") instanceof List) {
                document.add(new Paragraph("\nItems:").setBold());
                Table table = new Table(UnitValue.createPointArray(new float[]{100, 150, 50, 50, 70, 100}));
                table.widthPercent(100);
                table.addHeaderCell("Item");
                table.addHeaderCell("Description");
                table.addHeaderCell("Qty");
                table.addHeaderCell("Unit");
                table.addHeaderCell("Price");
                table.addHeaderCell("Remark");

                List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
                for (Map<String, Object> item : items) {
                    table.addCell(String.valueOf(item.getOrDefault("name", "")));
                    table.addCell(String.valueOf(item.getOrDefault("description", "")));
                    table.addCell(String.valueOf(item.getOrDefault("quantity", "0")));
                    table.addCell(String.valueOf(item.getOrDefault("unit", "")));
                    table.addCell(String.valueOf(item.getOrDefault("unitPrice", "0")));
                    table.addCell(String.valueOf(item.getOrDefault("remark", "")));
                }
                document.add(table);
            }
            
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF: ", e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    @Override
    public String getSupportedFormat() {
        return "PDF";
    }
}
