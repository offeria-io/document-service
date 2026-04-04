package offeria.document_service.service.generator;

import lombok.extern.slf4j.Slf4j;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Word Document Generator using docx4j.
 */
@Slf4j
@Component
public class DocxDocumentGenerator implements DocumentGenerator {

    @Override
    public byte[] generate(String templateContent, Map<String, Object> data, Map<String, String> images) {
        log.info("Generating Word document...");
        
        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
            
            mainDocumentPart.addStyledParagraphOfText("Title", "Generated Word Document");
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                mainDocumentPart.addParagraphOfText(entry.getKey() + ": " + entry.getValue());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            wordPackage.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating Word document: ", e);
            throw new RuntimeException("Word generation failed", e);
        }
    }

    @Override
    public String getSupportedFormat() {
        return "DOCX";
    }
}
