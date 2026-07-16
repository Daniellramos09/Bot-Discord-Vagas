package com.github.daniellramos09.discordvagas.domain.vaga;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.VagaRepository;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VagaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(VagaOrchestrator.class);

    private final List<VagaScraper> scrapers;
    private final VagaPromptProvider promptProvider;
    private final VagaMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final VagaRepository repository;

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    public VagaOrchestrator(List<VagaScraper> scrapers,
                            VagaPromptProvider promptProvider,
                            VagaMessageFormatter formatter,
                            GeminiClient geminiClient,
                            DiscordClient discordClient,
                            VagaRepository repository) {
        this.scrapers = scrapers;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public void execute() {
        logger.info("Iniciando busca de vagas...");

        for (VagaScraper scraper : scrapers) {
            List<Vaga> vagasEncontradas = scraper.buscarVagas();
            logger.info("Encontradas {} vagas pelo scraper: {}",
                    vagasEncontradas.size(), scraper.getClass().getSimpleName());

            for (Vaga vaga : vagasEncontradas) {
                if (repository.existsByUrl(vaga.getUrl())) {
                    logger.debug("Vaga já existe no banco: {}", vaga.getUrl());
                    continue;
                }

                logger.info("Nova vaga encontrada: {}", vaga.getTitulo());

                String prompt = promptProvider.buildPrompt(vaga.getDescricaoBruta());
                String resumo = geminiClient.generate(prompt);
                vaga.setResumoAi(resumo);
                vaga.setEnviado(true);

                Vaga vagaSalva = repository.save(vaga);

                String message = formatter.format(vagaSalva.getTitulo(), vagaSalva.getResumoAi(), vagaSalva.getUrl());
                discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

                logger.info("Vaga salva e enviada: {}", vagaSalva.getTitulo());
            }
        }

        logger.info("Processamento de vagas concluído.");
    }
}
