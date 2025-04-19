
import com.document_service.Entity.Document;
import com.document_service.Repository.DocumentRepository;
import com.document_service.service.imp.DocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void uploadDocument_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Test content".getBytes());
        Document documentDto = new Document();
        Document document = Document.builder()
                .id(1L)
                .author("Author")
                .content("Test content")
                .uploadDate(LocalDateTime.now())
                .build();

        when(documentRepository.save(any(Document.class))).thenReturn(document);

        // When
        Document response = documentService.uploadDocument(file, documentDto);

        // Then
        assertNotNull(response);
        assertEquals("Author", response.getAuthor());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void getDocument_Success() throws Exception {
        // Given
        Long documentId = 1L;
        Document document = Document.builder()
                .id(documentId)
                .author("Author")
                .content("Test content")
                .uploadDate(LocalDateTime.now())
                .build();

        when(documentRepository.findById(documentId));

        // When
        Document response = documentService.getDocument(documentId);

        // Then
        assertNotNull(response);
        assertEquals(documentId, response.getId());
        verify(documentRepository, times(1)).findById(documentId);
    }

    @Test
    void getDocuments_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Document document = Document.builder()
                .id(1L)
                .author("Author")
                .content("Test content")
                .uploadDate(LocalDateTime.now())
                .build();
        Page<Document> page = new PageImpl<>(Collections.singletonList(document));

        when(documentRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Document> response = documentService.getDocuments(
                null, null, null, null, 0, 10, new String[]{"uploadDate,desc"});

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(documentRepository, times(1)).findAll(pageable);
    }
}
