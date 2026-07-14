package com.github.daniellramos09.discordvagas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(GeminiApiService.class);

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

        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao chamar API Gemini: {}", e.getMessage(), e);
            return "Erro ao gerar resumo: " + e.getMessage();
        }
    }


    public String gerarResumoOpenSource(String titulo, String descricaoBruta) {
        String prompt = String.format(
                "Você é um desenvolvedor sênior ajudando profissionais juniores. Leia o título e a descrição desta issue " +
                        "do GitHub (marcada como 'good first issue') e faça um resumo direto, encorajador e conciso de no máximo 3 linhas " +
                        "em português, explicando claramente qual é o problema e o que precisa ser feito no código para resolvê-lo.\n\n" +
                        "Título da Issue: %s\n" +
                        "Descrição da Issue:\n%s",
                titulo, descricaoBruta
        );

        return executarRequisicaoGemini(prompt);
    }

    // Método privado auxiliar para evitar a repetição de código de montagem de payload JSON
    private String executarRequisicaoGemini(String prompt) {
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
                Map<String, Object> candidate = ((java.util.List<Map<String, Object>>) response.get("candidates")).get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                java.util.List<Map<String, Object>> parts = (java.util.List<Map<String, Object>>) content.get("parts");
                return (String) parts.get(0).get("text");
            }

            return "Erro ao gerar resumo: resposta inválida da API do Gemini";

        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao chamar API Gemini: {}", e.getMessage(), e);
            return "Erro ao gerar resumo através da IA: " + e.getMessage();
        }
    }

}