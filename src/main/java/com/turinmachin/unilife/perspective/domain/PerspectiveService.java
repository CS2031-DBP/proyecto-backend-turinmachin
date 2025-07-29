package com.turinmachin.unilife.perspective.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerspectiveService {

    private static final Logger logger = LoggerFactory.getLogger(PerspectiveService.class);

    @Value("${perspective.api-key}")
    private String apiKey;

    @Value("${perspective.toxicity-threshold}")
    private double toxicityThreshold;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getApiUrl() {
        return "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;
    }

    @Cacheable("ToxicCache")
    public boolean isToxic(final String content) {
        final Map<String, Object> requestBody = Map.of(
                "comment", Map.of("text", content),
                "languages", List.of("es"),
                "requestedAttributes", Map.of("TOXICITY", Map.of()));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            final ResponseEntity<Map<String, Object>> response = restTemplate.exchange(getApiUrl(), HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {
                    });
            final Map<String, Object> responseBody = response.getBody();
            if (responseBody == null)
                return false;

            final Map<String, Object> attributes = (Map<String, Object>) responseBody.get("attributeScores");
            final Map<String, Object> toxicity = (Map<String, Object>) attributes.get("TOXICITY");
            final Map<String, Object> summaryScore = (Map<String, Object>) toxicity.get("summaryScore");
            final double score = ((Number) summaryScore.get("value")).doubleValue();

            return score >= toxicityThreshold;
        } catch (final Exception e) {
            logger.error("Error al usar Perspective API: {}", e);
            return false;
        }
    }
}
