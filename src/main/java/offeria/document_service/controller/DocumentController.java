package offeria.document_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import offeria.document_service.dto.DocumentGenerationRequest;
import offeria.document_service.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for document generation.
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * POST endpoint to generate a document.
     * @param request Document generation parameters
     * @return Generated file as byte array
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateDocument(@Valid @RequestBody DocumentGenerationRequest request) {
        byte[] document = documentService.generateDocument(request);

        String filename = "document-" + System.currentTimeMillis() + "." + request.getFormat().toLowerCase();
        
        MediaType mediaType = switch (request.getFormat().toUpperCase()) {
            case "PDF" -> MediaType.APPLICATION_PDF;
            case "EXCEL" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "DOCX" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(document);
    }
}
