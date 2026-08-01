package com.github.daniellramos09.discordvagas.domain.evento;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SerperEventoScraper {

    private static final Logger logger = LoggerFactory.getLogger(SerperEventoScraper.class);
    private static final String SERPER_API_URL = "https://google.serper.dev/search";

    // Substituímos a String única por uma Lista com as 3 queries
    private static final List<String> SEARCH_QUERIES = List.of(
            "site:meetup.com (\"inteligência artificial\" OR \"dados\" OR \"software\") \"São Paulo\"",
            "site:meetup.com (\"tecnologia\" OR \"TI\" OR \"tech\" OR \"developer\") \"São Paulo\"",
            "site:meetup.com (\"tech\" OR \"tecnologia\" OR \"dados\" OR \"IA\") \"São Paulo\""
    );

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public SerperEventoScraper(RestTemplate restTemplate,
                               ObjectMapper objectMapper,
                               @Value("${buscador.vaga.serperapi}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public List<EventoRecord> scrape() {
        List<EventoRecord> results = new ArrayList<>();
        // Set é uma lista especial que bloqueia repetições automaticamente
        Set<String> linksProcessados = new HashSet<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", apiKey);

            logger.info("-> Buscando Eventos no Meetup via Serper.dev...");

            // Fazemos um loop (laço) para rodar as 3 pesquisas uma após a outra
            for (String query : SEARCH_QUERIES) {
                Map<String, Object> body = Map.of(
                        "q", query,
                        "gl", "br",
                        "hl", "pt",
                        "tbs", "qdr:w" // Resultados da última semana (se quiser 24h, mude para "qdr:d")
                );

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

                String responseJson = restTemplate.postForObject(SERPER_API_URL, request, String.class);

                if (responseJson == null) {
                    logger.warn("Resposta nula da API do Serper para a query: {}", query);
                    continue; // Pula para a próxima pesquisa se der erro
                }

                JsonNode root = objectMapper.readTree(responseJson);
                JsonNode organicResults = root.path("organic");

                if (organicResults.isArray()) {
                    for (JsonNode result : organicResults) {
                        String titulo = result.path("title").asText("Sem título");
                        String link = result.path("link").asText("");

                        // Se a URL estiver vazia OU já estiver no nosso Set (já pegamos hoje), ignoramos
                        if (link.isBlank() || linksProcessados.contains(link)) {
                            continue;
                        }

                        // Adiciona no Set para marcar que já processamos e adiciona no resultado final
                        linksProcessados.add(link);
                        results.add(new EventoRecord(titulo, link));
                    }
                }
            }

            logger.info("Serper retornou {} eventos brutos (sem repetições).", results.size());

        } catch (Exception e) {
            logger.error("Erro ao buscar eventos no Serper: {}", e.getMessage(), e);
        }

        return results;
    }
}