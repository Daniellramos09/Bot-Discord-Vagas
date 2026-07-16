package com.github.daniellramos09.discordvagas.domain.hackathon;

import com.github.daniellramos09.discordvagas.entity.Hackathon;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.HackathonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HackathonOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(HackathonOrchestrator.class);

    private final HackathonScraper scraper;
    private final HackathonPromptProvider promptProvider;
    private final HackathonMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final HackathonRepository repository;

    @Value("${discord.webhook.url.hackathon}")
    private String webhookUrl;

    public HackathonOrchestrator(HackathonScraper scraper,
                                 HackathonPromptProvider promptProvider,
                                 HackathonMessageFormatter formatter,
                                 GeminiClient geminiClient,
                                 DiscordClient discordClient,
                                 HackathonRepository repository) {
        this.scraper = scraper;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public void execute() {
        List<HackathonRecord> records = scraper.scrape();
        ZonedDateTime dataLimite = ZonedDateTime.now().minusDays(45);
        int eventosEnviadosNaRodada = 0;
        int maximoPorRodada = 3;

        for (HackathonRecord record : records) {
            if (eventosEnviadosNaRodada >= maximoPorRodada) {
                logger.info("Limite de {} hackathons atingido nesta rodada.", maximoPorRodada);
                break;
            }

            if (!isValidDate(record.dataPublicacaoStr(), dataLimite)) {
                continue;
            }

            if (repository.existsByUrl(record.link())) {
                continue;
            }

            String prompt = promptProvider.buildPrompt(record.titulo());
            String resumoAi = geminiClient.generate(prompt);

            if (isBlockedByAI(resumoAi)) {
                logger.info("Hackathon bloqueado pela IA (Fora de SP, Expirado ou Reportagem): {}", record.titulo());
                saveIgnored(record, resumoAi);
                continue;
            }

            Hackathon novoEvento = new Hackathon();
            novoEvento.setTitulo(record.titulo());
            novoEvento.setUrl(record.link());
            novoEvento.setResumoAi(resumoAi);
            novoEvento.setDataPublicacao(LocalDateTime.now());
            novoEvento.setEnviado(true);

            repository.save(novoEvento);

            String message = formatter.format(record.titulo(), resumoAi, record.link());
            discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

            eventosEnviadosNaRodada++;
            logger.info("Novo Hackathon enviado: {}", record.titulo());

            sleep(15000);
        }
    }

    private boolean isValidDate(String dataPublicacaoStr, ZonedDateTime dataLimite) {
        try {
            ZonedDateTime dataDaNoticia = ZonedDateTime.parse(dataPublicacaoStr, DateTimeFormatter.RFC_1123_DATE_TIME);
            return !dataDaNoticia.isBefore(dataLimite);
        } catch (Exception e) {
            logger.warn("Data inválida: {}", dataPublicacaoStr);
            return false;
        }
    }

    private boolean isBlockedByAI(String resumoAi) {
        if (resumoAi == null) return true;
        String trimmed = resumoAi.trim();
        return trimmed.equalsIgnoreCase("IGNORAR")
                || trimmed.equalsIgnoreCase("EXPIRADO")
                || resumoAi.startsWith("429");
    }

    private void saveIgnored(HackathonRecord record, String resumoAi) {
        Hackathon ignored = new Hackathon();
        ignored.setTitulo(record.titulo());
        ignored.setUrl(record.link());
        ignored.setResumoAi(resumoAi);
        ignored.setDataPublicacao(LocalDateTime.now());
        ignored.setEnviado(true);
        repository.save(ignored);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
