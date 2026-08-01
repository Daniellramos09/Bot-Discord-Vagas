package com.github.daniellramos09.discordvagas.domain.evento;

import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(EventoOrchestrator.class);

    private final SerperEventoScraper scraper;
    private final EventoPromptProvider promptProvider;
    private final EventoMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final EventoRepository repository;

    @Value("${discord.webhook.url.evento}")
    private String webhookUrl;

    public EventoOrchestrator(SerperEventoScraper scraper,
                              EventoPromptProvider promptProvider,
                              EventoMessageFormatter formatter,
                              GeminiClient geminiClient,
                              DiscordClient discordClient,
                              EventoRepository repository) {
        this.scraper = scraper;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public void execute() {
        try {
            List<EventoRecord> records = scraper.scrape();
            int eventosEnviadosNaRodada = 0;
            int maximoPorRodada = 2;

        for (EventoRecord record : records) {
            if (eventosEnviadosNaRodada >= maximoPorRodada) {
                logger.info("Limite de {} eventos atingido nesta rodada.", maximoPorRodada);
                break;
            }

            if (repository.existsByUrl(record.url())) {
                logger.info("Evento já processado anteriormente: {}", record.titulo());
                continue;
            }

            String prompt = promptProvider.buildPrompt(record.titulo());
            String resumoAi = geminiClient.generate(prompt);

            if (isBlockedByAI(resumoAi)) {
                logger.info("Evento bloqueado pela IA (expirado, presencial fora de SP ou irrelevante): {}",
                        record.titulo());
                saveIgnored(record, resumoAi);
                continue;
            }

            Evento novoEvento = new Evento();
            novoEvento.setTitulo(record.titulo());
            novoEvento.setUrl(record.url());
            novoEvento.setResumoAi(resumoAi);
            novoEvento.setDataPublicacao(LocalDateTime.now());
            novoEvento.setEnviado(true);

            repository.save(novoEvento);

            String message = formatter.format(record.titulo(), resumoAi, record.url());
            discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

            eventosEnviadosNaRodada++;
            logger.info("Novo Evento enviado: {}", record.titulo());

            sleep(2000);
        }
        } catch (Exception e) {
            logger.error("Erro ao executar rotina de eventos: {}", e.getMessage(), e);
        }
    }

    private boolean isBlockedByAI(String resumoAi) {
        if (resumoAi == null) {
            return true;
        }
        String trimmed = resumoAi.trim();
        return trimmed.equalsIgnoreCase("IGNORAR")
                || trimmed.equalsIgnoreCase("EXPIRADO")
                || trimmed.startsWith("429");
    }

    private void saveIgnored(EventoRecord record, String resumoAi) {
        Evento ignored = new Evento();
        ignored.setTitulo(record.titulo());
        ignored.setUrl(record.url());
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
