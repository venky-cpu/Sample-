package com.example.document.controller;

import com.example.document.model.DocumentSearchRequest;
import com.example.document.model.DocumentSearchResponse;
import com.example.document.service.DocumentSearchService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Document search endpoint.
 *
 * <pre>
 * GET /api/v1/documents/search
 *   ?identifierContains=abc
 *   &amp;exists=metadata.name
 *   &amp;pageNumber=1
 *   &amp;pageSize=20
 *   &amp;identificationOrder=asc
 *   &amp;nextCursor=eyJ...   (opaque, from a previous response)
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentSearchService searchService;

    public DocumentController(DocumentSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentSearchResponse> search(
            @Valid @ModelAttribute DocumentSearchRequest request) {

        DocumentSearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
}
