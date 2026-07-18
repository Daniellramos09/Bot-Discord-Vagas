package com.github.daniellramos09.discordvagas.domain.curso;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CursoScheduler {

    private final CursoOrchestrator orchestrator;

    public CursoScheduler(CursoOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 12,18 * * *", zone = "America/Sao_Paulo")
    public void rotinaCursos() {
        orchestrator.execute();
    }
}
