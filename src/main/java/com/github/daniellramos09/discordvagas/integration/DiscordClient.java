package com.github.daniellramos09.discordvagas.integration;

import com.github.daniellramos09.discordvagas.core.exception.IntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class DiscordClient {

    private static final Logger logger = LoggerFactory.getLogger(DiscordClient.class);

    private final RestTemplate restTemplate;

    public DiscordClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendWebhook(String webhookUrl, String content, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String truncated = content.length() > 2000
                ? content.substring(0, 1995) + "..."
                : content;

        Map<String, Object> requestBody = Map.of(
                "content", truncated,
                "username", username
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            String response = restTemplate.postForObject(webhookUrl, request, String.class);
            if (response == null) {
                logger.warn("Discord retornou resposta nula para webhook '{}'", webhookUrl);
            } else {
                logger.info("Mensagem enviada para Discord via '{}': {} caracteres", username, truncated.length());
            }
        } catch (Exception e) {
            throw new IntegrationException("Erro ao enviar mensagem para Discord: " + e.getMessage(), e);
        }
    }
}
