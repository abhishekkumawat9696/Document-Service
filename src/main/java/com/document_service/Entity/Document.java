package com.document_service.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String author;

    @Column
    private String documentType;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    @Column(columnDefinition = "TSVECTOR")
    private String searchVector;

    @PrePersist
    @PreUpdate
    private void updateSearchVector() {
        // will Update PostgreSQL tsvector for full-text search
    }
}