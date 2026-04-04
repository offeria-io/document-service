package offeria.document_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base custom exception for the Document Service.
 */
@Getter
public class DocumentServiceException extends RuntimeException {
    private final HttpStatus status;

    public DocumentServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
