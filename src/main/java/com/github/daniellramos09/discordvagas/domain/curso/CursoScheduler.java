package com.github.daniellramos09.discordvagas.domain.curso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CursoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CursoScheduler.class);

    private final CursoOrchestrator orchestrator;

    public CursoScheduler(CursoOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 4,5 * * *", zone = "America/Sao_Paulo")
    public void rotinaCursos() {
        try {
            orchestrator.execute();
        } catch (Exception e) {
            logger.error("Erro ao executar rotina de cursos: {}", e.getMessage(), e);
        }
    }
}
