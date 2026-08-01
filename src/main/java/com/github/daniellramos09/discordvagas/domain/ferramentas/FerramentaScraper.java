package com.github.daniellramos09.discordvagas.domain.ferramentas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class FerramentaScraper {

    private static final Logger logger = LoggerFactory.getLogger(FerramentaScraper.class);

    private static final String GITHUB_SEARCH_API = "https://api.github.com/search/repositories";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String searchQuery;
    private final int maxReposPerRun;

    public FerramentaScraper(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             @Value("${ferramentas.search-query:productivity+OR+%22self-hosted%22+OR+%22developer-tools%22+OR+ai+OR+llm+stars:%3E800}") String searchQuery,
                             @Value("${ferramentas.max-repos-per-run:30}") int maxReposPerRun) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.searchQuery = searchQuery.isBlank()
                ? "productivity+OR+%22self-hosted%22+OR+%22developer-tools%22+OR+ai+OR+llm+stars:%3E800"
                : searchQuery;
        this.maxReposPerRun = maxReposPerRun;
    }

    public List<FerramentaRecord> scrape() {
        List<FerramentaRecord> results = new ArrayList<>();

        try {
            String apiUrl = GITHUB_SEARCH_API
                    + "?q=" + searchQuery
                    + "&sort=updated&order=desc"
                    + "&per_page=" + maxReposPerRun;

            URI uri = URI.create(apiUrl);
            logger.info("Buscando repositórios via GitHub Search API: {}", uri);

            String responseJson = restTemplate.getForObject(uri, String.class);

            if (responseJson == null) {
                logger.warn("Resposta nula da GitHub Search API");
                return results;
            }

            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode items = root.path("items");

            if (!items.isArray()) {
                logger.warn("Resposta da GitHub Search API não contém 'items' array");
                return results;
            }

            int limite = Math.min(items.size(), maxReposPerRun);

            for (int i = 0; i < limite; i++) {
                JsonNode repo = items.get(i);

                String owner = repo.path("owner").path("login").asText();
                String name = repo.path("name").asText();
                String description = repo.path("description").asText("");
                String language = repo.path("language").asText("N/A");
                long stars = repo.path("stargazers_count").asLong(0);
                long forks = repo.path("forks_count").asLong(0);
                String urlRepo = repo.path("html_url").asText();

                results.add(new FerramentaRecord(owner, name, urlRepo,
                        description, language, stars, forks));

                logger.info("Repositório encontrado: {}/{} ({} stars)", owner, name, stars);
            }

            logger.info("Total de repositórios encontrados na busca: {}", results.size());
        } catch (Exception e) {
            logger.error("Erro ao buscar repositórios na GitHub Search API: {}", e.getMessage(), e);
        }

        return results;
    }
}
