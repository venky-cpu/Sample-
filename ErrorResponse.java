package com.example.document.model;

import java.util.List;

/**
 * Uniform error payload returned for 4xx/5xx responses.
 *
 * @param status    HTTP status code.
 * @param error     short error label.
 * @param messages  one or more human-readable validation/error messages.
 * @param timestamp ISO-8601 time the error was produced.
 */
public record ErrorResponse(
        int status,
        String error,
        List<String> messages,
        String timestamp
) {
}
