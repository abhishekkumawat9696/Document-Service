package com.document_service.Repository;

import com.document_service.Entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "SELECT * FROM documents WHERE search_vector @@ to_tsquery(:query)",
            nativeQuery = true)
    List<Document> fullTextSearch(@Param("query") String query);

    @Query(value = "SELECT * FROM documents WHERE search_vector @@ to_tsquery(:query) ORDER BY ts_rank(search_vector, to_tsquery(:query)) DESC LIMIT :limit",
            nativeQuery = true)
    List<Document> fullTextSearchWithLimit(@Param("query") String query, @Param("limit") int limit);

    Page<Document> findByAuthorAndTitle(String author, String title, Pageable pageable);

    Page<Document> findByAuthor(String author, Pageable pageable);

    Optional<Document> findById(Long id);

    Page<Document> findByTitleContaining(String title, Pageable pageable);

    Page<Document> findByUploadDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Document> searchDocuments(String query, Pageable pageable);
}
