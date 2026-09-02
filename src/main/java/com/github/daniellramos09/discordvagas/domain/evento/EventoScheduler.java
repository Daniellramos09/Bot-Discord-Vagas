package com.github.daniellramos09.discordvagas.domain.evento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EventoScheduler.class);

    private final EventoOrchestrator orchestrator;

    public EventoScheduler(EventoOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "America/Sao_Paulo")
    public void rotinaEventos() {
        try {
            logger.info("Iniciando rotina agendada: Eventos e Hackathons...");
            orchestrator.execute();
            logger.info("Rotina de eventos finalizada.");
        } catch (Exception e) {
            logger.error("Erro ao executar rotina de eventos: {}", e.getMessage(), e);
        }
    }
}
