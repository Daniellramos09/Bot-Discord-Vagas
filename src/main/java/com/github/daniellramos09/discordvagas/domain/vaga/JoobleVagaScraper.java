package com.github.daniellramos09.discordvagas.domain.vaga;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JoobleVagaScraper implements VagaScraper {

    private static final Logger logger = LoggerFactory.getLogger(JoobleVagaScraper.class);

    private static final String JOOBLE_API_URL = "https://br.jooble.org/api/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public JoobleVagaScraper(RestTemplate restTemplate,
                              ObjectMapper objectMapper,
                              @Value("${buscardo.vagas.jooble}") String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Override
    public List<Vaga> buscarVagas() {
        List<Vaga> vagas = new ArrayList<>();

        try {
            String url = JOOBLE_API_URL + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "keywords", "estágio OR junior tecnologia",
                    "location", "São Paulo"
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            logger.info("-> Buscando vagas via Jooble...");

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode jobs = root.path("jobs");

                if (jobs.isArray()) {
                    for (JsonNode job : jobs) {
                        String titulo = job.path("title").asText("Sem título");
                        String company = job.path("company").asText("Empresa não informada");
                        String location = job.path("location").asText("Localização não informada");
                        String link = job.path("link").asText("");

                        String descricaoBruta = String.format(
                                "Cargo: %s\nEmpresa: %s\nLocalização: %s",
                                titulo, company, location);

                        vagas.add(new Vaga(titulo + " - " + company, link, descricaoBruta));
                    }
                }

                logger.info("Jooble: {} vagas encontradas.", vagas.size());
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar vagas via Jooble: {}", e.getMessage(), e);
        }

        return vagas;
    }
}
