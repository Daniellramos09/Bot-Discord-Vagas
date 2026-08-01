package com.github.daniellramos09.discordvagas.domain.vaga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SerpApiVagaScraper implements VagaScraper {

    private static final Logger logger = LoggerFactory.getLogger(SerpApiVagaScraper.class);

    // O Serper usa o endpoint de busca padrão, não existe um "/jobs" isolado nele
    private static final String SERPER_API_URL = "https://google.serper.dev/search";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public SerpApiVagaScraper(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             @Value("${buscador.vaga.serpapi}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Override
    public List<Vaga> buscarVagas() {
        List<Vaga> vagas = new ArrayList<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ATENÇÃO: O Serper usa este header específico para a chave!
            headers.set("X-API-KEY", apiKey);

            Map<String, Object> body = Map.of(
                    "q", "estágio em tecnologia São Paulo capital",
                    "gl", "br",       // Country: Brasil
                    "hl", "pt",       // Language: Português
                    "tbs", "qdr:w"    // Date Range: Past week (Última semana)
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            logger.info("-> Buscando vagas no Google via Serper.dev...");

            String responseJson = restTemplate.postForObject(SERPER_API_URL, request, String.class);

            if (responseJson == null) {
                logger.warn("Resposta nula da API do Serper.");
                return vagas;
            }

            JsonNode root = objectMapper.readTree(responseJson);

            // O Serper devolve os links de sites como Gupy, Catho e Vagas.com dentro do array "organic"
            JsonNode organicResults = root.path("organic");

            if (organicResults.isArray()) {
                for (JsonNode result : organicResults) {
                    String titulo = result.path("title").asText("Sem título");
                    String link = result.path("link").asText("");
                    String snippet = result.path("snippet").asText("");

                    // IMPORTANTE: Como é uma busca orgânica, não temos a "empresa" isolada
                    // O seu Orchestrator/PromptProvider vai mandar o 'snippet' para o Gemini ler e entender.

                    // Exemplo de adição (adapte para os campos reais da sua classe Vaga):
                    // vagas.add(new Vaga(titulo, "Empresa no Link", "São Paulo", link, snippet));
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao buscar vagas no Serper: {}", e.getMessage(), e);
        }

        return vagas;
    }
}
