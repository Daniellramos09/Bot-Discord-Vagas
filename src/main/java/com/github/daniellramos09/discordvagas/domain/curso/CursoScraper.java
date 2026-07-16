package com.github.daniellramos09.discordvagas.domain.curso;

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
public class CursoScraper {

    private static final Logger logger = LoggerFactory.getLogger(CursoScraper.class);

    private static final String GOOGLE_NEWS_RSS =
            "https://news.google.com/rss/search?q=(" +
                    "\"São Paulo\" OR \"SP\" OR online OR EAD OR remoto OR imersão OR virtual OR \"free course\" OR \"online course\" OR \"international\"" +
                    ")+(" +
                    "tecnologia OR programação OR dados OR \"inteligência artificial\" OR segurança OR TI OR desenvolvimento OR software OR coding OR \"computer science\"" +
                    ")+(" +
                    "\"curso gratuito\" OR bootcamp OR bolsa OR \"free course\" OR scholarship OR \"free training\"" +
                    ")+when:45d&hl=pt-BR&gl=BR&ceid=BR:pt-419";


    public List<CursoRecord> scrape() {
        List<CursoRecord> results = new ArrayList<>();

        try {
            logger.info("-> Buscando Cursos e Bootcamps de Tech em SP...");
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

                boolean isTech = tituloLower.contains("tecnologia") ||
                        tituloLower.contains("programação") ||
                        tituloLower.contains("programacao") ||
                        tituloLower.contains("ti ") ||
                        tituloLower.contains("dados") ||
                        tituloLower.contains("dev") ||
                        tituloLower.contains("inteligência artificial") ||
                        tituloLower.contains("inteligencia artificial") ||
                        tituloLower.contains("ia") ||
                        tituloLower.contains(" ai ") ||
                        tituloLower.contains("segurança") ||
                        tituloLower.contains("seguranca") ||
                        tituloLower.contains("cyber") ||
                        tituloLower.contains("security") ||
                        tituloLower.contains("nuvem") ||
                        tituloLower.contains("cloud") ||
                        tituloLower.contains("redes") ||
                        tituloLower.contains("computação") ||
                        tituloLower.contains("computacao");

                boolean isGratuito = tituloLower.contains("gratuito") ||
                        tituloLower.contains("bootcamp") ||
                        tituloLower.contains("bolsa") ||
                        tituloLower.contains("vagas") ||
                        tituloLower.contains("grátis") ||
                        tituloLower.contains("gratis");

                boolean isParaCriancas = tituloLower.contains("kids") ||
                        tituloLower.contains("criança") ||
                        tituloLower.contains("crianca") ||
                        tituloLower.contains("infantil") ||
                        tituloLower.contains("mirim") ||
                        tituloLower.contains("escola de programação para crianças") ||
                        tituloLower.contains("fundamental i") ||
                        tituloLower.contains("fundamental 1") ||
                        tituloLower.contains("fundamental ii") ||
                        tituloLower.contains("fundamental 2");

                if (isTech && isGratuito && !isParaCriancas) {
                    results.add(new CursoRecord(titulo, link, dataPublicacaoStr));
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar cursos: {}", e.getMessage());
        }

        return results;
    }
}
