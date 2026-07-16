package com.github.daniellramos09.discordvagas.domain.opensource;

import com.github.daniellramos09.discordvagas.entity.OpenSource;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.OpenSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OpenSourceOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(OpenSourceOrchestrator.class);

    private final OpenSourceScraper scraper;
    private final OpenSourcePromptProvider promptProvider;
    private final OpenSourceMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final OpenSourceRepository repository;

    @Value("${discord.webhook.url.openSource:}")
    private String webhookUrl;

    @Value("${github.language-filter:java}")
    private String languageFilter;

    public OpenSourceOrchestrator(OpenSourceScraper scraper,
                                 OpenSourcePromptProvider promptProvider,
                                 OpenSourceMessageFormatter formatter,
                                 GeminiClient geminiClient,
                                 DiscordClient discordClient,
                                 OpenSourceRepository repository) {
        this.scraper = scraper;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public int execute() {
        List<IssueRecord> records = scraper.scrape();
        int novasIssuesEncontradas = 0;

        for (IssueRecord record : records) {
            if (repository.existsByGithubId(record.githubId())) {
                continue;
            }

            String nomeRepositorio = record.repositorioUrl()
                    .replace("https://api.github.com/repos/", "");

            String prompt = promptProvider.buildPrompt(record.titulo(), record.descricaoOriginal());
            String resumoAi = geminiClient.generate(prompt);

            OpenSource novaIssue = new OpenSource();
            novaIssue.setGithubId(record.githubId());
            novaIssue.setTitulo(record.titulo());
            novaIssue.setUrl(record.url());
            novaIssue.setRepositorio(nomeRepositorio);
            novaIssue.setLinguagem(languageFilter);
            novaIssue.setResumoAi(resumoAi);
            novaIssue.setDataPublicacao(LocalDateTime.now());
            novaIssue.setEnviado(true);

            repository.save(novaIssue);

            String message = formatter.format(
                    nomeRepositorio, record.titulo(), languageFilter, resumoAi, record.url());
            discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

            novasIssuesEncontradas++;
            logger.info("Nova issue Open Source enviada com resumo IA: {}", record.titulo());
        }

        return novasIssuesEncontradas;
    }
}
