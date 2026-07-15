package com.github.daniellramos09.discordvagas.scheduler;

import com.github.daniellramos09.discordvagas.scraper.HackathonScraper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HackathonScheduler {

    private final HackathonScraper hackathonScraper;

    public HackathonScheduler(HackathonScraper hackathonScraper) {
        this.hackathonScraper = hackathonScraper;
    }

    // Executa às 11h e 17h, assim não concorre ao mesmo tempo com o bot de Cursos que roda às 12h/18h.
    @Scheduled(cron = "0 0 11,17 * * *", zone = "America/Sao_Paulo")
    public void rotinaHackathons() {
        hackathonScraper.buscarHackathons();
    }
}