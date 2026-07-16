package com.example.document.service;

import com.example.document.exception.SearchValidationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Encodes and decodes the opaque pagination cursor.
 *
 * <p>The cursor is simply the JSON-serialised list of {@code sort} values from the
 * last hit of the previous page, Base64-URL encoded so it is safe to pass as a
 * query parameter.</p>
 */
@Component
public class CursorCodec {

    private final ObjectMapper objectMapper;

    public CursorCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Serialise sort values into an opaque cursor string. */
    public String encode(List<Object> sortValues) {
        if (sortValues == null || sortValues.isEmpty()) {
            return null;
        }
        try {
            byte[] json = objectMapper.writeValueAsBytes(sortValues);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode pagination cursor", e);
        }
    }

    /** Parse an opaque cursor string back into sort values. */
    public List<Object> decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return List.of();
        }
        try {
            byte[] json = Base64.getUrlDecoder().decode(cursor);
            return objectMapper.readValue(
                    new String(json, StandardCharsets.UTF_8),
                    new TypeReference<List<Object>>() {});
        } catch (IllegalArgumentException | com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new SearchValidationException("Invalid 'nextCursor' value: cursor is malformed");
        }
    }
}
