package com.github.daniellramos09.discordvagas.domain.Certificacoes;

import com.github.daniellramos09.discordvagas.entity.Certificacao;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.CertificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificacaoOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(CertificacaoOrchestrator.class);

    private final CertificaoScraper scraper;
    private final CertificacaoPromptProvider promptProvider;
    private final CertificacaoMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final CertificacaoRepository repository;

    @Value("${discord.webhook.url.certificacao}")
    private String webhookUrl;

    public CertificacaoOrchestrator(CertificaoScraper scraper,
                                    CertificacaoPromptProvider promptProvider,
                                    CertificacaoMessageFormatter formatter,
                                    GeminiClient geminiClient,
                                    DiscordClient discordClient,
                                    CertificacaoRepository repository) {
        this.scraper = scraper;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public void execute() {
        List<CertificaoRecord> records = scraper.scrape();
        int certificacoesEnviadasNaRodada = 0;
        int maximoPorRodada = 3;

        for (CertificaoRecord record : records) {
            if (certificacoesEnviadasNaRodada >= maximoPorRodada) {
                logger.info("Limite de {} certificações atingido nesta rodada.", maximoPorRodada);
                break;
            }

            if (repository.existsByUrl(record.link())) {
                logger.info("Certificação já enviada anteriormente: {}", record.titulo());
                continue;
            }

            String prompt = promptProvider.buildPrompt(
                    record.titulo(), record.descricao(), record.publicoAlvo());
            String resumoAi = geminiClient.generate(prompt);

            Certificacao novaCertificacao = new Certificacao();
            novaCertificacao.setTitulo(record.titulo());
            novaCertificacao.setUrl(record.link());
            novaCertificacao.setResumoAi(resumoAi);
            novaCertificacao.setDataPublicacao(LocalDateTime.now());
            novaCertificacao.setEnviado(true);

            repository.save(novaCertificacao);

            String message = formatter.format(
                    record.titulo(), resumoAi, record.link(),
                    record.documentacaoOficial(), record.reposEstudo());
            discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

            certificacoesEnviadasNaRodada++;
            logger.info("Nova Certificação enviada: {}", record.titulo());

            sleep(15000);
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
