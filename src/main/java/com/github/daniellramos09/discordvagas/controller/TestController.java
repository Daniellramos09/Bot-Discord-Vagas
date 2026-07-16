package com.github.daniellramos09.discordvagas.controller;

import com.github.daniellramos09.discordvagas.domain.curso.CursoOrchestrator;
import com.github.daniellramos09.discordvagas.domain.hackathon.HackathonOrchestrator;
import com.github.daniellramos09.discordvagas.domain.opensource.OpenSourceOrchestrator;
import com.github.daniellramos09.discordvagas.domain.vaga.VagaOrchestrator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final VagaOrchestrator vagaOrchestrator;
    private final OpenSourceOrchestrator openSourceOrchestrator;
    private final CursoOrchestrator cursoOrchestrator;
    private final HackathonOrchestrator hackathonOrchestrator;

    public TestController(VagaOrchestrator vagaOrchestrator,
                          OpenSourceOrchestrator openSourceOrchestrator,
                          CursoOrchestrator cursoOrchestrator,
                          HackathonOrchestrator hackathonOrchestrator) {
        this.vagaOrchestrator = vagaOrchestrator;
        this.openSourceOrchestrator = openSourceOrchestrator;
        this.cursoOrchestrator = cursoOrchestrator;
        this.hackathonOrchestrator = hackathonOrchestrator;
    }

    @PostMapping("/run-scraper")
    public String runScraper() {
        try {
            vagaOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-opensource")
    public String runScraperOpenSource() {
        try {
            openSourceOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-curso")
    public String runScraperCurso() {
        try {
            cursoOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-hackathon")
    public String runScraperHackathon() {
        try {
            hackathonOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }
}
