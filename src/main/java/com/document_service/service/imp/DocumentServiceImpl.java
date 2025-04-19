package com.document_service.service.imp;

import com.document_service.Entity.Document;
import com.document_service.Repository.DocumentRepository;
import com.document_service.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final Tika tika = new Tika();


    @Override
    public Document uploadDocument(MultipartFile file, Document documentDTO) {
        Document savedDocument = null;
        try {
            Document document = Document.builder()
                    .author(documentDTO.getAuthor())
                    .content(file.getContentType())
                    .uploadDate(LocalDateTime.now())
                    .build();

            savedDocument = documentRepository.save(document);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedDocument;
    }

    @Override
    public CompletableFuture<List<Document>> uploadDocuments(List<MultipartFile> files, List<Document> documentDtos) {
        List<Document> responses = IntStream.range(0, files.size())
                .mapToObj(i -> {
                    try {
                        return uploadDocument(files.get(i), documentDtos.get(i));
                    } catch (Exception e) {
                        // Log error and continue with next document
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(responses);
    }

    @Override
    public Document getDocument(Long id) throws Exception {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new Exception("Document not found with id: " + id));
        return document;
    }

    @Override
    public Page<Document> getDocuments(String author, String title, LocalDate startDate, LocalDate endDate, int page, int size, String[] sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        if (author != null && title != null) {
            return documentRepository.findByAuthorAndTitle(author, title, (java.awt.print.Pageable) pageable);
        } else if (author != null) {
            return documentRepository.findByAuthor(author, (java.awt.print.Pageable) pageable);
        } else if (title != null) {
            return documentRepository.findByTitleContaining(title, (java.awt.print.Pageable) pageable);
        } else if (startDate != null && endDate != null) {
            return documentRepository.findByUploadDateBetween(
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59),
                    (java.awt.print.Pageable) pageable);
        } else {
            return documentRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Document> searchDocuments(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.searchDocuments(query, pageable);
    }

    private Sort parseSort(String[] sort) {
        if (sort.length >= 2) {
            return Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        }
        return Sort.unsorted();
    }
    private String extractContent(MultipartFile file) throws IOException {
        return tika.parseToString(file.getInputStream());
    }

    private Document mapToDocumentResponse(Document document) {
        return Document.builder()
                .id(document.getId())
                .author(document.getAuthor())
                .uploadDate(document.getUploadDate())
                .build();
    }
}