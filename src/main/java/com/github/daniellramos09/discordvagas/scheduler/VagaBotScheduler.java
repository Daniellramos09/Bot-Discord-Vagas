package com.github.daniellramos09.discordvagas.scheduler;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.repository.VagaRepository;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import com.github.daniellramos09.discordvagas.service.DiscordWebhookService;
import com.github.daniellramos09.discordvagas.service.GeminiApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VagaBotScheduler {

    private static final Logger logger = LoggerFactory.getLogger(VagaBotScheduler.class);


    private final List<VagaScraper> vagaScrapers;
    private final VagaRepository vagaRepository;
    private final GeminiApiService geminiApiService;
    private final DiscordWebhookService discordWebhookService;

    public VagaBotScheduler(List<VagaScraper> vagaScrapers,
                            VagaRepository vagaRepository,
                            GeminiApiService geminiApiService,
                            DiscordWebhookService discordWebhookService) {
        this.vagaScrapers = vagaScrapers;
        this.vagaRepository = vagaRepository;
        this.geminiApiService = geminiApiService;
        this.discordWebhookService = discordWebhookService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void buscarEProcessarVagas() {
        logger.info("Iniciando busca de vagas...");

        for (VagaScraper scraper : vagaScrapers) {
            List<Vaga> vagasEncontradas = scraper.buscarVagas();
            logger.info("Encontradas {} vagas pelo scraper: {}", vagasEncontradas.size(), scraper.getClass().getSimpleName());

            for (Vaga vaga : vagasEncontradas) {
                if (!vagaRepository.existsByUrl(vaga.getUrl())) {
                    logger.info("Nova vaga encontrada: {}", vaga.getTitulo());

                    String resumo = geminiApiService.gerarResumo(vaga.getDescricaoBruta());
                    vaga.setResumoAi(resumo);

                    Vaga vagaSalva = vagaRepository.save(vaga);
                    discordWebhookService.enviarVaga(vagaSalva);

                    logger.info("Vaga salva e enviada: {}", vagaSalva.getTitulo());
                } else {
                    logger.debug("Vaga já existe no banco: {}", vaga.getUrl());
                }
            }
        }

        logger.info("Processamento de vagas concluído.");
    }
}
