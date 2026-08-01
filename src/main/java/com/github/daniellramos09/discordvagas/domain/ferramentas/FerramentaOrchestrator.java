package com.github.daniellramos09.discordvagas.domain.ferramentas;

import com.github.daniellramos09.discordvagas.entity.FerramentaOpenSource;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.FerramentaOpenSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FerramentaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(FerramentaOrchestrator.class);

    private final FerramentaScraper scraper;
    private final FerramentaPromptProvider promptProvider;
    private final FerramentaMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final FerramentaOpenSourceRepository repository;

    @Value("${discord.webhook.url.ferramentas:}")
    private String webhookUrl;

    public FerramentaOrchestrator(FerramentaScraper scraper,
                                  FerramentaPromptProvider promptProvider,
                                  FerramentaMessageFormatter formatter,
                                  GeminiClient geminiClient,
                                  DiscordClient discordClient,
                                  FerramentaOpenSourceRepository repository) {
        this.scraper = scraper;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public int execute() {
        try {
            List<FerramentaRecord> records = scraper.scrape();
            int ferramentasEnviadas = 0;

        for (FerramentaRecord record : records) {
            if (repository.existsByUrl(record.url())) {
                logger.info("Ferramenta já enviada anteriormente: {}/{}", record.owner(), record.name());
                continue;
            }

            String nomeRepositorio = record.owner() + "/" + record.name();

            String prompt = promptProvider.buildPrompt(
                    nomeRepositorio, record.descricao(), record.linguagem(),
                    record.stars(), record.forks());
            String resumoAi = geminiClient.generate(prompt);

            FerramentaOpenSource novaFerramenta = new FerramentaOpenSource();
            novaFerramenta.setTitulo(nomeRepositorio);
            novaFerramenta.setUrl(record.url());
            novaFerramenta.setRepositorio(nomeRepositorio);
            novaFerramenta.setLinguagem(record.linguagem());
            novaFerramenta.setStars(record.stars());
            novaFerramenta.setForks(record.forks());
            novaFerramenta.setResumoAi(resumoAi);
            novaFerramenta.setDataPublicacao(LocalDateTime.now());
            novaFerramenta.setEnviado(true);

            repository.save(novaFerramenta);

            String message = formatter.format(
                    record.owner(), record.name(), record.linguagem(),
                    record.stars(), record.forks(), resumoAi, record.url());
            discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

            ferramentasEnviadas++;
            logger.info("Nova ferramenta Open Source enviada: {}", nomeRepositorio);

            sleep(15000);
        }

        return ferramentasEnviadas;
        } catch (Exception e) {
            logger.error("Erro ao executar rotina de ferramentas: {}", e.getMessage(), e);
            return 0;
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
