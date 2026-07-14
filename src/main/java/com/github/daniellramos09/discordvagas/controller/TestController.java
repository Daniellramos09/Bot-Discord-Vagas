package com.github.daniellramos09.discordvagas.controller;

import com.github.daniellramos09.discordvagas.scheduler.OpenSourceScheduler;
import com.github.daniellramos09.discordvagas.scheduler.VagaBotScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final VagaBotScheduler vagaBotScheduler;
    private final OpenSourceScheduler openSourceScheduler;

    public TestController(VagaBotScheduler vagaBotScheduler, OpenSourceScheduler openSourceScheduler) {
        this.vagaBotScheduler = vagaBotScheduler;
        this.openSourceScheduler = openSourceScheduler;
    }

    @PostMapping("/run-scraper")
    public String runScraper() {
        try {
            vagaBotScheduler.buscarEProcessarVagas();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-opensource")
    public String runScraperOpenSource() {
        try {
            openSourceScheduler.agendarBuscaOpenSource();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }
}
