package com.github.daniellramos09.discordvagas.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.daniellramos09.discordvagas.entity.OpenSource;
import com.github.daniellramos09.discordvagas.repository.OpenSourceRepository;
import com.github.daniellramos09.discordvagas.service.DiscordWebhookService;
import com.github.daniellramos09.discordvagas.service.GeminiApiService; // IMPORT NOVO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class GithubOpenSourceScraper {

    private static final Logger logger = LoggerFactory.getLogger(GithubOpenSourceScraper.class);
    private static final String GITHUB_API_URL = "https://api.github.com/search/issues?q=label:\"good first issue\"+language:java+state:open&sort=created&order=desc";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OpenSourceRepository openSourceRepository;
    private final DiscordWebhookService discordWebhookService;
    private final GeminiApiService geminiApiService; // INJEÇÃO NOVA

    public GithubOpenSourceScraper(RestTemplate restTemplate, ObjectMapper objectMapper,
                                   OpenSourceRepository openSourceRepository,
                                   DiscordWebhookService discordWebhookService,
                                   GeminiApiService geminiApiService) { // INJEÇÃO NOVA
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.openSourceRepository = openSourceRepository;
        this.discordWebhookService = discordWebhookService;
        this.geminiApiService = geminiApiService;
    }

    public int buscarEProcessarIssues() {
        int novasIssuesEncontradas = 0;

        try {
            String responseJson = restTemplate.getForObject(GITHUB_API_URL, String.class);
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode items = root.path("items");

            int limite = Math.min(items.size(), 5);

            for (int i = 0; i < limite; i++) {
                JsonNode issue = items.get(i);
                Long githubId = issue.path("id").asLong();

                if (!openSourceRepository.existsByGithubId(githubId)) {

                    String titulo = issue.path("title").asText();
                    String url = issue.path("html_url").asText();
                    String repositorioUrl = issue.path("repository_url").asText();
                    String nomeRepositorio = repositorioUrl.replace("https://api.github.com/repos/", "");

                    // EXTRAINDO O CORPO DA ISSUE DO JSON DO GITHUB
                    String descricaoOriginal = issue.path("body").asText();

                    // CHAMANDO O GEMINI
                    String prompt = String.format(
                            "Você é um desenvolvedor sênior ajudando juniores. Leia o título e a descrição desta issue do GitHub e faça um resumo direto de no máximo 3 linhas em português explicando o que precisa ser feito. " +
                                    "Título: %s. Descrição: %s", titulo, descricaoOriginal
                    );

                    String resumoAi = geminiApiService.gerarResumoOpenSource(prompt, descricaoOriginal); // Adapte para o nome exato do seu método no GeminiApiService

                    // 1. Salva no Banco de Dados com o resumo da IA!
                    OpenSource novaIssue = OpenSource.builder()
                            .githubId(githubId)
                            .titulo(titulo)
                            .url(url)
                            .repositorio(nomeRepositorio)
                            .linguagem("Java")
                            .resumoAi(resumoAi) // SETANDO O RESUMO
                            .dataPublicacao(LocalDateTime.now())
                            .enviado(true)
                            .build();

                    openSourceRepository.save(novaIssue);
                    novasIssuesEncontradas++;

                    // 2. Envia para o Discord
                    discordWebhookService.enviarOpenSource(novaIssue);

                    logger.info("Nova issue Open Source enviada com resumo IA: {}", titulo);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar e processar dados do GitHub: {}", e.getMessage(), e);
        }

        return novasIssuesEncontradas;
    }
}