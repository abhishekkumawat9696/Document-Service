package com.document_service.service;

import com.document_service.Entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DocumentService {
    Document uploadDocument(MultipartFile file, Document document);
    CompletableFuture<List<Document>> uploadDocuments(List<MultipartFile> files, List<Document> documentDtos);
    Document getDocument(Long id) throws Exception;
    Page<Document> getDocuments(
            String author, String title, LocalDate startDate, LocalDate endDate,
            int page, int size, String[] sort
    );
    Page<Document> searchDocuments(String query, int page, int size);

    List<Document> getAllDocuments();

    Document saveDocument(Document document);

    Optional<Document> getDocumentById(Long  id);



}
