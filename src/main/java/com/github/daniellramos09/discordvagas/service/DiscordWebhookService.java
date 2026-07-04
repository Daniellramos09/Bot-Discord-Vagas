package com.github.daniellramos09.discordvagas.service;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DiscordWebhookService {

    private final RestTemplate restTemplate;
    private final String webhookUrl;

    public DiscordWebhookService(RestTemplate restTemplate, @Value("${discord.webhook.url}") String webhookUrl) {
        this.restTemplate = restTemplate;
        this.webhookUrl = webhookUrl;
    }

    public void enviarVaga(Vaga vaga) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Monta o texto completo com o resumo do Gemini
        String content = String.format(
                "**%s**\n\n" +
                        "**Resumo:**\n%s\n\n" +
                        "**Link:** %s",
                vaga.getTitulo(),
                vaga.getResumoAi(),
                vaga.getUrl()
        );

        // 2. --- A TRAVA DE SEGURANÇA DO DISCORD ---
        if (content != null && content.length() > 2000) {
            // Se passar do limite, corta no caractere 1995 e adiciona "..."
            content = content.substring(0, 1995) + "...";
        }
        // ------------------------------------------

        // 3. Coloca o texto já validado e cortado no corpo da requisição
        Map<String, Object> requestBody = Map.of(
                "content", content,
                "username", "Vagas de Estágio Bot"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(webhookUrl, request, String.class);
            System.out.println("Vaga enviada para Discord: " + vaga.getTitulo());
        } catch (Exception e) {
            System.err.println("Erro ao enviar vaga para Discord: " + e.getMessage());
        }
    }
}