package com.github.daniellramos09.discordvagas.domain.ferramentas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FerramentaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FerramentaScheduler.class);

    private final FerramentaOrchestrator orchestrator;

    public FerramentaScheduler(FerramentaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 9,15 * * *", zone = "America/Sao_Paulo")
    public void rotinaFerramentas() {
        logger.info("Iniciando rotina agendada: Busca de Ferramentas Open Source...");
        int encontradas = orchestrator.execute();
        logger.info("Rotina finalizada. Total de ferramentas enviadas: {}", encontradas);
    }
}
