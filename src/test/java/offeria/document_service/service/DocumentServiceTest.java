package offeria.document_service.service;

import offeria.document_service.dto.DocumentGenerationRequest;
import offeria.document_service.entity.DocumentTemplate;
import offeria.document_service.repository.DocumentTemplateRepository;
import offeria.document_service.service.generator.DocumentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentTemplateRepository templateRepository;

    @Mock
    private DocumentGenerator generator;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        when(generator.getSupportedFormat()).thenReturn("PDF");
        documentService = new DocumentService(templateRepository, List.of(generator));
    }

    @Test
    void generateDocument_Success() {
        // Arrange
        DocumentGenerationRequest request = DocumentGenerationRequest.builder()
                .templateName("TestTemplate")
                .format("PDF")
                .data(new HashMap<>())
                .build();

        DocumentTemplate template = DocumentTemplate.builder()
                .name("TestTemplate")
                .content("Template Content")
                .build();

        when(templateRepository.findByName("TestTemplate")).thenReturn(Optional.of(template));
        when(generator.generate(anyString(), any(), any())).thenReturn("PDF Content".getBytes());

        // Act
        byte[] result = documentService.generateDocument(request);

        // Assert
        assertNotNull(result);
        assertArrayEquals("PDF Content".getBytes(), result);
        verify(generator).generate(eq("Template Content"), any(), any());
    }

    @Test
    void generateDocument_UnsupportedFormat_ThrowsException() {
        // Arrange
        DocumentGenerationRequest request = DocumentGenerationRequest.builder()
                .templateName("TestTemplate")
                .format("UNSUPPORTED")
                .data(new HashMap<>())
                .build();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> documentService.generateDocument(request));
    }
}
