package com.github.daniellramos09.discordvagas.controller;

import com.github.daniellramos09.discordvagas.domain.Certificacoes.CertificacaoOrchestrator;
import com.github.daniellramos09.discordvagas.domain.curso.CursoOrchestrator;
import com.github.daniellramos09.discordvagas.domain.evento.EventoOrchestrator;
import com.github.daniellramos09.discordvagas.domain.ferramentas.FerramentaOrchestrator;
import com.github.daniellramos09.discordvagas.domain.opensource.OpenSourceOrchestrator;
import com.github.daniellramos09.discordvagas.domain.vaga.VagaOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/test")
@Profile("dev")
public class TestController {

    public static final Logger logger = LoggerFactory.getLogger(TestController.class);

    private final VagaOrchestrator vagaOrchestrator;
    private final OpenSourceOrchestrator openSourceOrchestrator;
    private final CursoOrchestrator cursoOrchestrator;
    private final EventoOrchestrator eventoOrchestrator;
    private final FerramentaOrchestrator ferramentaOrchestrator;
    private final CertificacaoOrchestrator certificacaoOrchestrator;
    private final String expectedApiKey;

    public TestController(VagaOrchestrator vagaOrchestrator,
                          OpenSourceOrchestrator openSourceOrchestrator,
                          CursoOrchestrator cursoOrchestrator,
                          EventoOrchestrator eventoOrchestrator,
                          FerramentaOrchestrator ferramentaOrchestrator,
                          CertificacaoOrchestrator certificacaoOrchestrator,
                          @Value("${app.security.api-key:}") String expectedApiKey) {
        this.vagaOrchestrator = vagaOrchestrator;
        this.openSourceOrchestrator = openSourceOrchestrator;
        this.cursoOrchestrator = cursoOrchestrator;
        this.eventoOrchestrator = eventoOrchestrator;
        this.ferramentaOrchestrator = ferramentaOrchestrator;
        this.certificacaoOrchestrator = certificacaoOrchestrator;
        this.expectedApiKey = expectedApiKey;

        if (expectedApiKey == null || expectedApiKey.isBlank()) {
            logger.warn("APP_SECURITY_API_KEY nao configurada. Endpoints de teste permanecem bloqueados.");
        } else {
            logger.info("APP_SECURITY_API_KEY configurada. Endpoints de teste protegidos.");
        }
    }

    private boolean isAuthorized(String apiKey) {
        return expectedApiKey != null
                && !expectedApiKey.isBlank()
                && apiKey != null
                && MessageDigest.isEqual(expectedApiKey.getBytes(StandardCharsets.UTF_8),
                apiKey.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/run-scraper")
    public String runScraper(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (!isAuthorized(apiKey)) {
            return "Unauthorized";
        }
        try {
            vagaOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-opensource")
    public String runScraperOpenSource(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (!isAuthorized(apiKey)) {
            return "Unauthorized";
        }
        try {
            openSourceOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-curso")
    public String runScraperCurso(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (!isAuthorized(apiKey)) {
            return "Unauthorized";
        }
        try {
            cursoOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-evento")
    public String runScraperEvento(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (!isAuthorized(apiKey)) {
            return "Unauthorized";
        }
        try {
            eventoOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }


    @PostMapping("/run-scraper-certificacao")
    public String runScraperCertificacao(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (!isAuthorized(apiKey)) {
            return "Unauthorized";
        }
        try {
            certificacaoOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }

    @PostMapping("/run-scraper-ferramenta")
    public String runScraperFerramenta(@RequestHeader(value = "X-Api-Key", required = false) String apiKey) {
        if (!isAuthorized(apiKey)) {
            return "Unauthorized";
        }
        try {
            ferramentaOrchestrator.execute();
            return "Scraper executado com sucesso! Verifique o console para detalhes.";
        } catch (Exception e) {
            return "Erro ao executar scraper: " + e.getMessage();
        }
    }
}
