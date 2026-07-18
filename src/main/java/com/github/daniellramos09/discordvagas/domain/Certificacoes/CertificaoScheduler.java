package com.github.daniellramos09.discordvagas.domain.Certificacoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CertificaoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CertificaoScheduler.class);

    private final CertificacaoOrchestrator orchestrator;

    public CertificaoScheduler(CertificacaoOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 10,16 * * *", zone = "America/Sao_Paulo")
    public void rotinaCertificacoes() {
        logger.info("Iniciando rotina agendada: Envio de Certificações...");
        orchestrator.execute();
        logger.info("Rotina de certificações finalizada.");
    }
}
