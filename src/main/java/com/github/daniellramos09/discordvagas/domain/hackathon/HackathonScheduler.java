package com.github.daniellramos09.discordvagas.domain.hackathon;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HackathonScheduler {

    private final HackathonOrchestrator orchestrator;

    public HackathonScheduler(HackathonOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Scheduled(cron = "0 0 11,17 * * *", zone = "America/Sao_Paulo")
    public void rotinaHackathons() {
        orchestrator.execute();
    }
}
