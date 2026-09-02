package com.github.daniellramos09.discordvagas.domain.opensource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenSourceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OpenSourceScheduler.class);

    private final OpenSourceOrchestrator orchestrator;

    public OpenSourceScheduler(OpenSourceOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Sao_Paulo")
    public void agendarBuscaOpenSource() {
        try {
            logger.info("Iniciando rotina agendada: Busca de Projetos Open Source...");
            int encontradas = orchestrator.execute();
            logger.info("Rotina finalizada. Total de novas issues enviadas: {}", encontradas);
        } catch (Exception e) {
            logger.error("Erro ao executar rotina de open source: {}", e.getMessage(), e);
        }
    }
}
