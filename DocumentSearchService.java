package com.example.document.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.document.exception.SearchBackendException;
import com.example.document.exception.SearchValidationException;
import com.example.document.model.DocumentSearchRequest;
import com.example.document.model.DocumentSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Executes cursor-paginated searches against a single Elasticsearch index.
 *
 * <p>Uses the {@code search_after} API for deep, stable pagination over millions of
 * documents. Sorting is done on a unique keyword field (the Mongo ObjectId carried in
 * {@code metadata._id.oid}) so page boundaries are deterministic.</p>
 */
@Service
public class DocumentSearchService {

    private static final Logger log = LoggerFactory.getLogger(DocumentSearchService.class);

    /**
     * Unique, sortable identifier field. Elasticsearch field names cannot contain '$',
     * so the Mongo {@code $oid} is assumed to be mapped as {@code metadata._id.oid} of
     * type {@code keyword}. Adjust this constant if your mapping differs.
     */
    private static final String SORT_FIELD = "metadata._id.oid";

    private final ElasticsearchClient esClient;
    private final CursorCodec cursorCodec;
    private final String indexName;

    public DocumentSearchService(ElasticsearchClient esClient,
                                 CursorCodec cursorCodec,
                                 @Value("${app.elasticsearch.index:elastic_index}") String indexName) {
        this.esClient = esClient;
        this.cursorCodec = cursorCodec;
        this.indexName = indexName;
    }

    public DocumentSearchResponse search(DocumentSearchRequest request) {

        final int pageSize = request.effectivePageSize();
        final SortOrder order = "desc".equals(request.effectiveOrder())
                ? SortOrder.Desc
                : SortOrder.Asc;

        // ----- build the bool query from the optional criteria -----
        BoolQuery.Builder bool = new BoolQuery.Builder();
        boolean hasCriteria = false;

        if (request.identifierContains() != null && !request.identifierContains().isBlank()) {
            String value = request.identifierContains().trim();
            bool.filter(Query.of(q -> q
                    .wildcard(w -> w
                            .field(SORT_FIELD)
                            .value("*" + value + "*")
                            .caseInsensitive(true))));
            hasCriteria = true;
        }

        if (request.exists() != null && !request.exists().isBlank()) {
            String field = request.exists().trim();
            bool.filter(Query.of(q -> q.exists(e -> e.field(field))));
            hasCriteria = true;
        }

        if (!hasCriteria) {
            // no filters -> match everything, still ordered + paginated
            bool.must(Query.of(q -> q.matchAll(m -> m)));
        }

        // ----- decode cursor (search_after) -----
        List<FieldValue> searchAfter = toFieldValues(cursorCodec.decode(request.nextCursor()));

        // ----- assemble the search request -----
        SearchRequest.Builder builder = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> q.bool(bool.build()))
                .size(pageSize)
                .trackTotalHits(t -> t.enabled(false)) // faster on huge indices
                .sort(s -> s.field(f -> f.field(SORT_FIELD).order(order)));

        if (!searchAfter.isEmpty()) {
            builder.searchAfter(searchAfter);
        }

        // ----- execute -----
        SearchResponse<Map> response;
        try {
            response = esClient.search(builder.build(), Map.class);
        } catch (IOException e) {
            log.error("Elasticsearch is unavailable", e);
            throw new SearchBackendException("Elasticsearch is unavailable", e);
        } catch (co.elastic.clients.elasticsearch._types.ElasticsearchException e) {
            // e.g. bad field mapping, illegal argument in query
            log.error("Elasticsearch rejected the query", e);
            throw new SearchBackendException("Elasticsearch failed to execute the query", e);
        }

        // ----- map hits -----
        List<Hit<Map>> hits = response.hits().hits();
        List<Map<String, Object>> docs = new ArrayList<>(hits.size());
        for (Hit<Map> hit : hits) {
            @SuppressWarnings("unchecked")
            Map<String, Object> source = hit.source();
            docs.add(source);
        }

        // ----- compute next cursor from the last hit's sort values -----
        String nextCursor = null;
        if (hits.size() == pageSize && !hits.isEmpty()) {
            List<FieldValue> lastSort = hits.get(hits.size() - 1).sort();
            nextCursor = cursorCodec.encode(fromFieldValues(lastSort));
        }

        long tookMs = response.took();
        String actionDate = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        return new DocumentSearchResponse(tookMs, docs, actionDate, nextCursor);
    }

    // --- helpers to bridge the opaque cursor <-> ES FieldValue objects ---

    private List<FieldValue> toFieldValues(List<Object> raw) {
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        List<FieldValue> values = new ArrayList<>(raw.size());
        for (Object o : raw) {
            values.add(toFieldValue(o));
        }
        return values;
    }

    private FieldValue toFieldValue(Object o) {
        if (o == null) {
            return FieldValue.NULL;
        }
        if (o instanceof String s) {
            return FieldValue.of(s);
        }
        if (o instanceof Boolean b) {
            return FieldValue.of(b);
        }
        if (o instanceof Integer i) {
            return FieldValue.of(i.longValue());
        }
        if (o instanceof Long l) {
            return FieldValue.of(l);
        }
        if (o instanceof Number n) {
            return FieldValue.of(n.doubleValue());
        }
        // Fallback: treat as string
        return FieldValue.of(o.toString());
    }

    private List<Object> fromFieldValues(List<FieldValue> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        List<Object> raw = new ArrayList<>(values.size());
        for (FieldValue v : values) {
            raw.add(v._get()); // underlying String/Long/Double/Boolean/null
        }
        return raw;
    }
}
