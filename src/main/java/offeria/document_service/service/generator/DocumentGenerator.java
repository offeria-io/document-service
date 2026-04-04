package offeria.document_service.service.generator;

import java.util.Map;

/**
 * Interface for document generation strategies.
 * Design pattern: Strategy.
 */
public interface DocumentGenerator {
    /**
     * Generates a document based on a template and data.
     * @param templateContent content or path of the template
     * @param data key-value pairs for placeholders
     * @param images optional images to embed
     * @return byte array of the generated document
     */
    byte[] generate(String templateContent, Map<String, Object> data, Map<String, String> images);

    /**
     * Supported format.
     */
    String getSupportedFormat();
}
