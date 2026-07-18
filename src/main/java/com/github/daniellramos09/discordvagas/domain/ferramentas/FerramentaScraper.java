package com.github.daniellramos09.discordvagas.domain.ferramentas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class FerramentaScraper {

    private static final Logger logger = LoggerFactory.getLogger(FerramentaScraper.class);

    private static final String GITHUB_API_BASE = "https://api.github.com/repos/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final List<String> repoUrls;

    public FerramentaScraper(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             @Value("${ferramentas.repo-urls:https://github.com/yt-dlp/yt-dlp,https://github.com/ollama/ollama,https://github.com/lllyasviel/Fooocus,https://github.com/openai/whisper}") String repoUrls) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.repoUrls = List.of(repoUrls.split(","));
    }

    public List<FerramentaRecord> scrape() {
        List<FerramentaRecord> results = new ArrayList<>();

        for (String repoUrl : repoUrls) {
            try {
                String apiUrl = GITHUB_API_BASE + extractOwnerRepo(repoUrl.trim());
                String responseJson = restTemplate.getForObject(apiUrl, String.class);
                JsonNode repo = objectMapper.readTree(responseJson);

                String owner = repo.path("owner").path("login").asText();
                String name = repo.path("name").asText();
                String description = repo.path("description").asText("");
                String language = repo.path("language").asText("N/A");
                long stars = repo.path("stargazers_count").asLong(0);
                long forks = repo.path("forks_count").asLong(0);

                results.add(new FerramentaRecord(owner, name, repoUrl.trim(),
                        description, language, stars, forks));

                logger.info("Repositório carregado: {}/{} ({} stars)", owner, name, stars);

                Thread.sleep(1000);
            } catch (Exception e) {
                logger.error("Erro ao buscar repositório {}: {}", repoUrl, e.getMessage());
            }
        }

        return results;
    }

    private String extractOwnerRepo(String githubUrl) {
        String path = githubUrl.replace("https://github.com/", "").replace("http://github.com/", "");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
