package com.example.document.exception;

/** Thrown when the request is semantically invalid; maps to HTTP 400. */
public class SearchValidationException extends RuntimeException {
    public SearchValidationException(String message) {
        super(message);
    }
}
