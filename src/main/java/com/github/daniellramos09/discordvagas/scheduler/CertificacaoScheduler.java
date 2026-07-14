package com.github.daniellramos09.discordvagas.scheduler;

import com.github.daniellramos09.discordvagas.scraper.CertificacaoRssScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CertificacaoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CertificacaoScheduler.class);
    private final CertificacaoRssScraper certificacaoRssScraper;

    public CertificacaoScheduler(CertificacaoRssScraper certificacaoRssScraper) {
        this.certificacaoRssScraper = certificacaoRssScraper;
    }

    // Executa todos os dias às 10h da manhã e às 16h da tarde
    @Scheduled(cron = "0 0 10,16 * * *", zone = "America/Sao_Paulo")
    public void rotinaCertificacoes() {
        logger.info("Iniciando rotina agendada: Busca de Certificações e Descontos...");
        certificacaoRssScraper.buscarOportunidades();
        logger.info("Rotina de Certificações finalizada.");
    }
}