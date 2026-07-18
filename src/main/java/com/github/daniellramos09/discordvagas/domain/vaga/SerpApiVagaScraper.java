package com.github.daniellramos09.discordvagas.domain.vaga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class SerpApiVagaScraper implements VagaScraper {

    private static final Logger logger = LoggerFactory.getLogger(SerpApiVagaScraper.class);

    private static final String SERP_API_URL = "https://serpapi.com/search.json";

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
            String url = UriComponentsBuilder.fromHttpUrl(SERP_API_URL)
                    .queryParam("engine", "google_jobs")
                    .queryParam("q", "(estágio OR junior)+tecnologia")
                    .queryParam("location", "São Paulo, State of São Paulo, Brazil")
                    .queryParam("hl", "pt")
                    .queryParam("gl", "br")
                    .queryParam("api_key", apiKey)
                    .toUriString();

            logger.info("-> Buscando vagas via SerpApi (Google Jobs)...");

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode jobsResults = root.path("jobs_results");

                if (jobsResults.isArray()) {
                    for (JsonNode job : jobsResults) {
                        String titulo = job.path("title").asText("Sem título");
                        String company = job.path("company_name").asText("Empresa não informada");
                        String location = job.path("location").asText("Localização não informada");
                        String link = job.path("share_link").asText("");

                        String descricaoBruta = String.format(
                                "Cargo: %s\nEmpresa: %s\nLocalização: %s",
                                titulo, company, location);

                        vagas.add(new Vaga(titulo + " - " + company, link, descricaoBruta));
                    }
                }

                logger.info("SerpApi: {} vagas encontradas.", vagas.size());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar vagas via SerpApi: {}", e.getMessage(), e);
        }

        return vagas;
    }
}
