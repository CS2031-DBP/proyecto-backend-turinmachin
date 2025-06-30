package com.turinmachin.unilife.perspective.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PerspectiveService {

    private static final Logger logger = LoggerFactory.getLogger(PerspectiveService.class);

    private static final double TOXICITY_THRESHOLD = 0.7;

    @Value("${google.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getApiUrl() {
        return "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=" + apiKey;
    }

    @Cacheable(value = "toxic_cache", key = "content")
    public boolean isToxic(String content) {
        Map<String, Object> requestBody = Map.of(
                "comment", Map.of("text", content),
                "languages", List.of("es"),
                "requestedAttributes", Map.of("TOXICITY", Map.of()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(getApiUrl(), HttpMethod.POST, request,
                    new ParameterizedTypeReference<>() {
                    });
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null)
                return false;

            Map<String, Object> attributes = (Map<String, Object>) responseBody.get("attributeScores");
            Map<String, Object> toxicity = (Map<String, Object>) attributes.get("TOXICITY");
            Map<String, Object> summaryScore = (Map<String, Object>) toxicity.get("summaryScore");
            double score = ((Number) summaryScore.get("value")).doubleValue();

            return score >= TOXICITY_THRESHOLD;
        } catch (Exception e) {
            logger.error("Error al usar Perspective API: {}", e);
            return false;
        }
    }
}
