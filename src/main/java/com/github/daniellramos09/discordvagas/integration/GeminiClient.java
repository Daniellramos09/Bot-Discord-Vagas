package com.github.daniellramos09.discordvagas.integration;

import com.github.daniellramos09.discordvagas.core.exception.IntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final RestTemplate restTemplate;
    private final String apiKey;

    public GeminiClient(RestTemplate restTemplate,
                        @Value("${gemini.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String generate(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            URI uri = URI.create(GEMINI_API_URL + "?key=" + apiKey);
            Map<String, Object> response = restTemplate.postForObject(uri, request, Map.class);

            if (response != null && response.containsKey("candidates")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates =
                        (List<Map<String, Object>>) response.get("candidates");
                Map<String, Object> candidate = candidates.get(0);
                @SuppressWarnings("unchecked")
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> parts =
                        (List<Map<String, Object>>) content.get("parts");
                return (String) parts.get(0).get("text");
            }

            throw new IntegrationException("Resposta inválida da API Gemini: sem candidates");

        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao chamar API Gemini: {}", e.getMessage(), e);
            throw new IntegrationException("Falha ao comunicar com a API Gemini", e);
        }
    }
}
