package com.example.document.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * Elasticsearch connection configuration.
 *
 * <p>Spring Boot 3.5.x ships Spring Data Elasticsearch 5.5.x, which uses the
 * Elasticsearch Java client v9. Extending {@link ElasticsearchConfiguration} makes
 * an {@code ElasticsearchClient} bean available for injection — this is the v9-correct
 * way (the old {@code RestClientTransport} manual wiring is no longer the default).</p>
 *
 * <p>Alternatively, if you only set {@code spring.elasticsearch.uris} in
 * {@code application.yml}, Spring Boot auto-configures the {@code ElasticsearchClient}
 * for you and this class can be deleted.</p>
 */
@Configuration
public class ElasticsearchClientConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                // .usingSsl()
                // .withBasicAuth("user", "password")
                .build();
    }
}
