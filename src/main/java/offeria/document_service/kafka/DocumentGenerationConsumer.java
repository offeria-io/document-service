package offeria.document_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import offeria.document_service.dto.DocumentGenerationRequest;
import offeria.document_service.service.DocumentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for document generation events.
 * Enables event-driven document generation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentGenerationConsumer {

    private final DocumentService documentService;

    /**
     * Listens to the document-generation-topic and processes requests.
     * @param request The generation request payload
     */
    @KafkaListener(topics = "document-generation-topic", groupId = "document-service-group")
    public void consume(DocumentGenerationRequest request) {
        log.info("Received Kafka message to generate document: {}", request.getTemplateName());
        try {
            // In a real scenario, we might upload the result to S3 or send it back via another topic
            byte[] document = documentService.generateDocument(request);
            log.info("Successfully generated document of size {} bytes from Kafka event", document.length);
        } catch (Exception e) {
            log.error("Failed to process Kafka document generation event", e);
        }
    }
}
