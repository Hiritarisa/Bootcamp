package com.hiri.crediya.infrastructure.drivenadapters.httpclient;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for HttpClientConfig.
 * Tests the WebClient bean configuration without external dependencies.
 */
class HttpClientConfigTest {

    @Test
    void shouldCreateWebClientBean() {
        // Given
        HttpClientConfig config = new HttpClientConfig();

        // When
        WebClient webClient = config.webClient();

        // Then
        assertNotNull(webClient);
    }
}

