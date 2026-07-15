package com.github.daniellramos09.discordvagas.scheduler;

import com.github.daniellramos09.discordvagas.scraper.CursoScraper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CursoScheduler {

    private final CursoScraper cursoScraper;

    public CursoScheduler(CursoScraper cursoScraper) {
        this.cursoScraper = cursoScraper;
    }

    // Executa ao meio-dia e às 18h
    @Scheduled(cron = "0 0 12,18 * * *", zone = "America/Sao_Paulo")
    public void rotinaCursos() {
        cursoScraper.buscarCursosBrasileiros();
    }
}