package com.github.daniellramos09.discordvagas.domain.hackathon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HackathonScraper {

    private static final Logger logger = LoggerFactory.getLogger(HackathonScraper.class);

    private static final String GOOGLE_NEWS_RSS =
            "https://news.google.com/rss/search?q=(hackathon+OR+\"maratona+de+programação\")+(\"inscrições+abertas\"+OR+\"inscreva-se\"+OR+\"vagas+abertas\"+OR+\"inscrições+prorrogadas\")+when:45d&hl=pt-BR&gl=BR&ceid=BR:pt-419";

    public List<HackathonRecord> scrape() {
        List<HackathonRecord> results = new ArrayList<>();

        try {
            logger.info("-> Buscando Hackathons e Maratonas de Programação...");
            Document doc = Jsoup.connect(GOOGLE_NEWS_RSS)
                    .userAgent("Mozilla/5.0")
                    .parser(Parser.xmlParser())
                    .get();

            Elements items = doc.select("item");

            for (Element item : items) {
                String titulo = item.select("title").text();
                String link = item.select("link").text();
                String dataPublicacaoStr = item.select("pubDate").text();

                String tituloLower = titulo.toLowerCase();
                boolean isHackathon = tituloLower.contains("hackathon") ||
                        tituloLower.contains("maratona de");

                if (isHackathon) {
                    results.add(new HackathonRecord(titulo, link, dataPublicacaoStr));
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar Hackathons: {}", e.getMessage());
        }

        return results;
    }
}
