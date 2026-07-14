package com.github.daniellramos09.discordvagas.scheduler;

import com.github.daniellramos09.discordvagas.scraper.GithubOpenSourceScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OpenSourceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OpenSourceScheduler.class);
    private final GithubOpenSourceScraper githubOpenSourceScraper;

    public OpenSourceScheduler(GithubOpenSourceScraper githubOpenSourceScraper) {
        this.githubOpenSourceScraper = githubOpenSourceScraper;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void agendarBuscaOpenSource() {
        logger.info("Iniciando rotina agendada: Busca de Projetos Open Source...");

        int encontradas = githubOpenSourceScraper.buscarEProcessarIssues();

        logger.info("Rotina finalizada. Total de novas issues enviadas: {}", encontradas);
    }
}