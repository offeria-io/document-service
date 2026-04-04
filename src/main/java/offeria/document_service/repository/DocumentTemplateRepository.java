package offeria.document_service.repository;

import offeria.document_service.entity.DocumentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DocumentTemplate entity.
 */
@Repository
public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, UUID> {
    Optional<DocumentTemplate> findByName(String name);
}
