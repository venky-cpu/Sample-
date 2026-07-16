package com.example.document.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * Query parameters accepted by the document search endpoint.
 *
 * <p>Uses cursor-based pagination via Elasticsearch {@code search_after}. The
 * {@code nextCursor} carries the sort value(s) of the last document from the
 * previous page. When {@code nextCursor} is {@code null}/blank the first page is
 * returned.</p>
 *
 * @param identifierContains optional wildcard filter applied to the identifier field.
 * @param exists             optional field name; only documents where this field is present are returned.
 * @param pageNumber         optional passthrough page number (informational only; cursor drives paging).
 * @param pageSize           number of documents per page (1..1000).
 * @param identificationOrder sort direction: {@code asc} or {@code desc}. Defaults to {@code asc}.
 * @param nextCursor         opaque cursor for the next page (Base64-encoded sort values).
 */
public record DocumentSearchRequest(

        String identifierContains,

        String exists,

        @Min(value = 1, message = "pageNumber must be >= 1")
        Integer pageNumber,

        @Min(value = 1, message = "pageSize must be >= 1")
        @Max(value = 1000, message = "pageSize must be <= 1000")
        Integer pageSize,

        @Pattern(regexp = "(?i)asc|desc", message = "identificationOrder must be 'asc' or 'desc'")
        String identificationOrder,

        String nextCursor
) {

    /** Effective page size, defaulting to 20 when not supplied. */
    public int effectivePageSize() {
        return pageSize == null ? 20 : pageSize;
    }

    /** Effective sort direction, defaulting to ascending. */
    public String effectiveOrder() {
        return (identificationOrder == null || identificationOrder.isBlank())
                ? "asc"
                : identificationOrder.toLowerCase();
    }
}
