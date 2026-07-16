package com.example.document.model;

import java.util.List;
import java.util.Map;

/**
 * Response body for the document search endpoint.
 *
 * @param queryTimeMs  time Elasticsearch took to execute the query, in milliseconds.
 * @param docs         the matched documents (raw {@code _source} maps).
 * @param actionDate   the current server timestamp (ISO-8601) when the request was served.
 * @param nextCursor   cursor to fetch the next page, or {@code null} when there are no more pages.
 */
public record DocumentSearchResponse(
        long queryTimeMs,
        List<Map<String, Object>> docs,
        String actionDate,
        String nextCursor
) {
}
