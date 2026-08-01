package com.github.daniellramos09.discordvagas.domain.curso;

import com.github.daniellramos09.discordvagas.entity.Curso;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.CursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CursoOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(CursoOrchestrator.class);

    private final CursoScraper scraper;
    private final CursoPromptProvider promptProvider;
    private final CursoMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final CursoRepository repository;

    @Value("${discord.webhook.url.curso}")
    private String webhookUrl;

    public CursoOrchestrator(CursoScraper scraper,
                             CursoPromptProvider promptProvider,
                             CursoMessageFormatter formatter,
                             GeminiClient geminiClient,
                             DiscordClient discordClient,
                             CursoRepository repository) {
        this.scraper = scraper;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public void execute() {
        try {
            List<CursoRecord> records = scraper.scrape();
            ZonedDateTime dataLimite = ZonedDateTime.now().minusDays(45);
            int cursosEnviadosNaRodada = 0;
            int maximoPorRodada = 3;

        for (CursoRecord record : records) {
            if (cursosEnviadosNaRodada >= maximoPorRodada) {
                logger.info("Limite de {} cursos atingido nesta rodada. O resto fica para depois.", maximoPorRodada);
                break;
            }

           /* if (!isValidDate(record.dataPublicacaoStr(), dataLimite)) {
                continue;
            }*/

            if (repository.existsByUrl(record.link())) {
                continue;
            }

            String prompt = promptProvider.buildPrompt(record.titulo());
            String resumoAi = geminiClient.generate(prompt);

            if (isBlockedByAI(resumoAi)) {
                logger.info("Curso bloqueado pela IA (Não é TI ou expirou): {}", record.titulo());
                saveIgnored(record, resumoAi);
                continue;
            }

            Curso novoCurso = new Curso();
            novoCurso.setTitulo(record.titulo());
            novoCurso.setUrl(record.link());
            novoCurso.setResumoAi(resumoAi);
            novoCurso.setDataPublicacao(LocalDateTime.now());
            novoCurso.setEnviado(true);

            repository.save(novoCurso);

            String message = formatter.format(record.titulo(), resumoAi, record.link());
            discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

            cursosEnviadosNaRodada++;
            logger.info("Novo Curso Tech SP enviado: {}", record.titulo());

            sleep(2000);
        }
        } catch (Exception e) {
            logger.error("Erro ao executar rotina de cursos: {}", e.getMessage(), e);
        }
    }

    /*private boolean isValidDate(String dataPublicacaoStr, ZonedDateTime dataLimite) {
        try {
            ZonedDateTime dataDaNoticia = ZonedDateTime.parse(dataPublicacaoStr, DateTimeFormatter.RFC_1123_DATE_TIME);
            return !dataDaNoticia.isBefore(dataLimite);
        } catch (Exception e) {
            logger.warn("Data inválida: {}", dataPublicacaoStr);
            return false;
        }
    }*/

    private boolean isBlockedByAI(String resumoAi) {
        if (resumoAi == null) return true;
        String trimmed = resumoAi.trim();
        return trimmed.equalsIgnoreCase("EXPIRADO") || trimmed.equalsIgnoreCase("IGNORAR");
    }

    private void saveIgnored(CursoRecord record, String resumoAi) {
        Curso ignored = new Curso();
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
