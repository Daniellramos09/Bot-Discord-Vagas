package com.github.daniellramos09.discordvagas.domain.opensource;

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
public class OpenSourceScraper {

    private static final Logger logger = LoggerFactory.getLogger(OpenSourceScraper.class);

    private static final String GITHUB_API_URL =
            "https://api.github.com/search/issues?q=label:\"good first issue\"+language:%s+state:open&sort=created&order=desc";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String languageFilter;
    private final int maxIssuesPerRun;

    public OpenSourceScraper(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                                 @Value("${github.language-filter:java}") String languageFilter,
                             @Value("${github.max-issues-per-run:5}") int maxIssuesPerRun) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.languageFilter = languageFilter;
        this.maxIssuesPerRun = maxIssuesPerRun;
    }

    public List<IssueRecord> scrape() {
        List<IssueRecord> results = new ArrayList<>();

        try {
            String url = String.format(GITHUB_API_URL, languageFilter);
            String responseJson = restTemplate.getForObject(url, String.class);

            if (responseJson == null) {
                logger.warn("Resposta nula da GitHub Search API para URL: {}", url);
                return results;
            }

            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode items = root.path("items");

            int limite = Math.min(items.size(), maxIssuesPerRun);

            for (int i = 0; i < limite; i++) {
                JsonNode issue = items.get(i);

                results.add(new IssueRecord(
                        issue.path("id").asLong(),
                        issue.path("title").asText(),
                        issue.path("html_url").asText(),
                        issue.path("repository_url").asText(),
                        issue.path("body").asText()
                ));
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar issues do GitHub: {}", e.getMessage(), e);
        }

        return results;
    }
}
