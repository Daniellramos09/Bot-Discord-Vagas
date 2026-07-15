package com.github.daniellramos09.discordvagas.controller;

import com.github.daniellramos09.discordvagas.scheduler.CursoScheduler;
import com.github.daniellramos09.discordvagas.scheduler.HackathonScheduler;
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
    private final CursoScheduler cursoScheduler;
    private final HackathonScheduler hackathonScheduler;

    public TestController(VagaBotScheduler vagaBotScheduler, OpenSourceScheduler openSourceScheduler, CursoScheduler cursoScheduler, HackathonScheduler hackathonScheduler) {
        this.vagaBotScheduler = vagaBotScheduler;
        this.openSourceScheduler = openSourceScheduler;
        this.cursoScheduler = cursoScheduler;
        this.hackathonScheduler = hackathonScheduler;
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

    @PostMapping("/run-scraper-curso")
    public String runScraperCurso() {
        try {
            cursoScheduler.rotinaCursos();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-hackathon")
    public String runScraperHackathon() {
        try {
            hackathonScheduler.rotinaHackathons();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }
}
