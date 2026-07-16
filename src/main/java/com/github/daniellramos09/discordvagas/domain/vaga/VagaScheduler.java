package com.github.daniellramos09.discordvagas.domain.vaga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VagaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(VagaScheduler.class);

    private final VagaOrchestrator orchestrator;

    public VagaScheduler(VagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void buscarEProcessarVagas() {
        orchestrator.execute();
    }
}
