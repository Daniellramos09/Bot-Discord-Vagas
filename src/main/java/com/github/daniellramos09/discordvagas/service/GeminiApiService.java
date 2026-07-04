package com.github.daniellramos09.discordvagas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
public class GeminiApiService {

    // A URL com o nome EXATO do modelo exigido pelo Google
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public GeminiApiService(RestTemplate restTemplate, @Value("${gemini.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String gerarResumo(String descricaoBruta) {
        String prompt = String.format(
                "Analise a descrição desta vaga de TI e gere um resumo altamente organizado para o Discord. " +
                        "Use estritamente o formato abaixo, mantendo as quebras de linha para o texto não ficar amontoado:\n\n" +
                        "º Região: [Liste as cidades/regiões principais. Se houver São Paulo/SP, destaque-a]\n" +
                        "º Cargo: [Título do cargo ou programa]\n" +
                        "º O que fará: [Resumo conciso das atividades]\n" +
                        "º Resumo sobre a empresa: [Breve descrição sobre quem é a empresa]\n" +
                        "º Missão, Visão e Valores: [Descreva brevemente a cultura ou propósito da empresa com base no texto]\n\n" +
                        "Seja direto e conciso. Descrição da vaga:\n%s",
                descricaoBruta
        );

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
            // Transformamos a String em um objeto URI blindado
            URI uri = URI.create(GEMINI_API_URL + "?key=" + apiKey);

            Map<String, Object> response = restTemplate.postForObject(uri, request, Map.class);

            if (response != null && response.containsKey("candidates")) {
                Map<String, Object> candidate = ((java.util.List<Map<String, Object>>) response.get("candidates")).get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                java.util.List<Map<String, Object>> parts = (java.util.List<Map<String, Object>>) content.get("parts");
                return (String) parts.get(0).get("text");
            }

            return "Erro ao gerar resumo: resposta inválida da API";

        } catch (Exception e) {
            System.err.println("Erro ao chamar API Gemini: " + e.getMessage());
            return "Erro ao gerar resumo: " + e.getMessage();
        }
    }
}