package offeria.document_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import offeria.document_service.dto.DocumentGenerationRequest;
import offeria.document_service.entity.DocumentTemplate;
import offeria.document_service.exception.DocumentServiceException;
import offeria.document_service.repository.DocumentTemplateRepository;
import offeria.document_service.service.generator.DocumentGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Core service for document operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentTemplateRepository templateRepository;
    private final List<DocumentGenerator> generators;

    /**
     * Map of generators by their supported format.
     */
    private Map<String, DocumentGenerator> generatorMap;

    /**
     * Lazy initialization of generator map.
     */
    private Map<String, DocumentGenerator> getGeneratorMap() {
        if (generatorMap == null) {
            generatorMap = generators.stream()
                    .collect(Collectors.toMap(DocumentGenerator::getSupportedFormat, Function.identity()));
        }
        return generatorMap;
    }

    /**
     * Generates a document based on the request.
     * @param request Document generation parameters
     * @return Byte array of the generated document
     */
    public byte[] generateDocument(DocumentGenerationRequest request) {
        log.info("Processing document generation request for template: {} with format: {}", 
                request.getTemplateName(), request.getFormat());

        // 1. Find template (In a real app, we might fallback to a default or DB)
        DocumentTemplate template = templateRepository.findByName(request.getTemplateName())
                .orElse(DocumentTemplate.builder()
                        .name(request.getTemplateName())
                        .type(request.getFormat())
                        .content("Default Template Content")
                        .build());

        // 2. Select appropriate generator
        DocumentGenerator generator = getGeneratorMap().get(request.getFormat().toUpperCase());
        if (generator == null) {
            log.error("Unsupported format: {}", request.getFormat());
            throw new DocumentServiceException("Unsupported format: " + request.getFormat(), HttpStatus.BAD_REQUEST);
        }

        // 3. Generate
        try {
            return generator.generate(template.getContent(), request.getData(), request.getImages());
        } catch (Exception e) {
            log.error("Failed to generate document", e);
            throw new DocumentServiceException("Document generation failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
