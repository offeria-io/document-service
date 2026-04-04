package offeria.document_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for generating a document.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentGenerationRequest {

    @NotBlank(message = "Template name is required")
    private String templateName;

    @NotBlank(message = "Output format is required (PDF, EXCEL, DOCX)")
    private String format;

    @NotNull(message = "Data for placeholders is required")
    private Map<String, Object> data;

    // Optional: image URLs or base64 to embed
    private Map<String, String> images;
}
