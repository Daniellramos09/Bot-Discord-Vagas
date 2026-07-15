package com.github.daniellramos09.discordvagas.service;

import com.github.daniellramos09.discordvagas.entity.OpenSource;
import com.github.daniellramos09.discordvagas.entity.Vaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DiscordWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(DiscordWebhookService.class);

    private final RestTemplate restTemplate;
    private final String webhookUrlVaga;
    private final String webhookUrlOpenSource;
    private final String webhookUrlCurso;
    private final String webhookUrlHackathon;



    public DiscordWebhookService(RestTemplate restTemplate,
                                 @Value("${discord.webhook.url}") String webhookUrlVaga,
                                 @Value("${discord.webhook.url.openSource:}") String webhookUrlOpenSource,
                                 @Value("${discord.webhook.url.curso}") String webhookUrlCurso,
                                 @Value("${discord.webhook.url.hackathon}") String webhookUrlHackathon) {
        this.restTemplate = restTemplate;
        this.webhookUrlVaga = webhookUrlVaga;
        this.webhookUrlOpenSource = webhookUrlOpenSource;
        this.webhookUrlCurso = webhookUrlCurso;
        this.webhookUrlHackathon = webhookUrlHackathon;
    }

    public void enviarVaga(Vaga vaga) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        String content = String.format(
                "**%s**\n\n" +
                        "**Resumo:**\n%s\n\n" +
                        "**Link:** %s",
                vaga.getTitulo(),
                vaga.getResumoAi(),
                vaga.getUrl()
        );


        if (content.length() > 2000) {

            content = content.substring(0, 1995) + "...";
        }

        Map<String, Object> requestBody = Map.of(
                "content", content,
                "username", "Vagas de Estágio Bot"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(webhookUrlVaga, request, String.class);
            logger.info("Vaga enviada para Discord: {}", vaga.getTitulo());
        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao enviar vaga para Discord: {}", e.getMessage(), e);
        }
    }


    public void enviarOpenSource(OpenSource openSource) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Adicionando o bloco de "Resumo da IA" na formatação
        String content = String.format(
                "🌟 **Nova Oportunidade Open Source (Good First Issue)!**\n\n" +
                        "**Repositório:** `%s`\n" +
                        "**Título:** %s\n" +
                        "**Linguagem:** %s\n\n" +
                        "🤖 **Resumo da IA:**\n%s\n\n" +
                        "🔗 **Acesse aqui para contribuir:** %s",
                openSource.getRepositorio(),
                openSource.getTitulo(),
                openSource.getLinguagem(),
                openSource.getResumoAi(), // Aqui entra a mágica da IA
                openSource.getUrl()
        );

        if (content.length() > 2000) {
                content = content.substring(0, 1995) + "...";
        }

        Map<String, Object> requestBody = Map.of(
                "content", content,
                "username", "Open Source Bot"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(webhookUrlOpenSource, request, String.class);
            logger.info("Open Source enviada para Discord: {}", openSource.getTitulo());
        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao enviar Open Source para Discord: {}", e.getMessage(), e);
        }
    }

    public void enviarCurso(com.github.daniellramos09.discordvagas.entity.Curso curso) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String content = String.format(
                "🚀 **Novo Bootcamp / Curso Gratuito na área!**\n\n" +
                        "**Notícia:** %s\n\n" +
                        "🤖 **O que a IA achou:**\n%s\n\n" +
                        "🔗 **Inscreva-se aqui:** %s",
                curso.getTitulo(),
                curso.getResumoAi(),
                curso.getUrl()
        );

        if (content.length() > 2000) {
            content = content.substring(0, 1995) + "...";
        }

        Map<String, Object> requestBody = Map.of(
                "content", content,
                "username", "Cursos & Bootcamps"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(webhookUrlCurso, request, String.class);
            logger.info("Curso enviado para Discord: {}", curso.getTitulo());
        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao enviar Curso para Discord: {}", e.getMessage());
        }
    }


    public void enviarHackathon(com.github.daniellramos09.discordvagas.entity.Hackathon hackathon) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String content = String.format(
                "🏁 **Hora de Codar! Novo Hackathon no Radar!**\n\n" +
                        "**Oportunidade:** %s\n\n" +
                        "👾 **O que o nosso Bot descobriu:**\n%s\n\n" +
                        "🙋‍♂️ **Quem quer formar time? Levanta a mão aqui no chat!**\n" +
                        "🔗 **Link Oficial:** %s",
                hackathon.getTitulo(),
                hackathon.getResumoAi(),
                hackathon.getUrl()
        );

        if (content.length() > 2000) {
            content = content.substring(0, 1995) + "...";
        }

        Map<String, Object> requestBody = Map.of(
                "content", content,
                "username", "Radar de Hackathons"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForObject(webhookUrlHackathon, request, String.class);
        } catch (org.springframework.web.client.RestClientException e) {
            System.err.println("Erro ao enviar Hackathon para o Discord: " + e.getMessage());
        }
    }
}