package com.github.daniellramos09.discordvagas.domain.ferramentas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class FerramentaScraper {

    private static final Logger logger = LoggerFactory.getLogger(FerramentaScraper.class);

    // A URL BLINDADA: Já com %22 (aspas) e %3E (maior que).
    // Note que os dois pontos (:) do "stars:" NÃO estão codificados. É assim que o GitHub gosta!
    private static final String GITHUB_SEARCH_API = "https://api.github.com/search/repositories?q=productivity+OR+%22self-hosted%22+OR+%22developer-tools%22+OR+ai+OR+llm+stars:%3E800&sort=updated&order=desc&per_page=30";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Construtor limpo: Removemos o @Value e as variáveis problemáticas
    public FerramentaScraper(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<FerramentaRecord> scrape() {
        List<FerramentaRecord> results = new ArrayList<>();

        try {
            logger.info("Buscando repositórios via GitHub Search API (Bypass ativado)...");

            // O Segredo: Usar URI.create() garante que o Spring NÃO modifique nossos símbolos
            URI uri = URI.create(GITHUB_SEARCH_API);

            String responseJson = restTemplate.getForObject(uri, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode items = root.path("items");

            if (!items.isArray()) {
                logger.warn("A resposta da API não contém o array 'items'.");
                return results;
            }

            logger.info("Total de repositórios encontrados na busca: {}", items.size());

            for (JsonNode repo : items) {
                String owner = repo.path("owner").path("login").asText();
                String name = repo.path("name").asText();
                String url = repo.path("html_url").asText();
                String description = repo.path("description").asText("");
                String language = repo.path("language").asText("N/A");
                long stars = repo.path("stargazers_count").asLong(0);
                long forks = repo.path("forks_count").asLong(0);

                results.add(new FerramentaRecord(owner, name, url, description, language, stars, forks));
            }

        } catch (Exception e) {
            logger.error("Erro ao buscar repositórios na GitHub Search API: {}", e.getMessage());
        }

        return results;
    }
}