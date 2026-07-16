package com.example.search.exception;

/**
 * Thrown when the search backend (Elasticsearch) is unreachable or errors out.
 * Handled as HTTP 500 Internal Server Error.
 */
public class SearchBackendException extends RuntimeException {
    public SearchBackendException(String message, Throwable cause) {
        super(message, cause);
    }
}
