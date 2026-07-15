package com.github.daniellramos09.discordvagas.scraper;

import com.github.daniellramos09.discordvagas.entity.Hackathon;
import com.github.daniellramos09.discordvagas.repository.HackathonRepository;
import com.github.daniellramos09.discordvagas.service.DiscordWebhookService;
import com.github.daniellramos09.discordvagas.service.GeminiApiService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HackathonScraper {

    private static final Logger logger = LoggerFactory.getLogger(HackathonScraper.class);
    private final HackathonRepository hackathonRepository;
    private final DiscordWebhookService discordWebhookService;
    private final GeminiApiService geminiApiService;

    private static final String GOOGLE_NEWS_RSS =
            "https://news.google.com/rss/search?q=(hackathon+OR+\"maratona+de+programação\")+(\"inscrições+abertas\"+OR+\"inscreva-se\"+OR+\"vagas+abertas\"+OR+\"inscrições+prorrogadas\")+when:45d&hl=pt-BR&gl=BR&ceid=BR:pt-419";


    public HackathonScraper(HackathonRepository hackathonRepository, DiscordWebhookService discordWebhookService, GeminiApiService geminiApiService) {
        this.hackathonRepository = hackathonRepository;
        this.discordWebhookService = discordWebhookService;
        this.geminiApiService = geminiApiService;
    }

    public void buscarHackathons() {
        try {
            logger.info("-> Buscando Hackathons e Maratonas de Programação...");
            Document doc = Jsoup.connect(GOOGLE_NEWS_RSS)
                    .userAgent("Mozilla/5.0")
                    .parser(Parser.xmlParser())
                    .get();

            Elements items = doc.select("item");
            ZonedDateTime dataLimite = ZonedDateTime.now().minusDays(45);

            // Limite de envios para evitar consumir toda a taxa do Gemini e poupar os usuários
            int eventosEnviadosNaRodada = 0;
            int maximoPorRodada = 3;

            for (Element item : items) {
                if (eventosEnviadosNaRodada >= maximoPorRodada) {
                    logger.info("Limite de {} hackathons atingido nesta rodada.", maximoPorRodada);
                    break;
                }

                String titulo = item.select("title").text();
                String link = item.select("link").text();
                String dataPublicacaoStr = item.select("pubDate").text();

                try {
                    ZonedDateTime dataDaNoticia = ZonedDateTime.parse(dataPublicacaoStr, DateTimeFormatter.RFC_1123_DATE_TIME);
                    if (dataDaNoticia.isBefore(dataLimite)) {
                        continue;
                    }
                } catch (Exception e) {
                    logger.warn("Data inválida para o hackathon: {}", titulo);
                }

                String tituloLower = titulo.toLowerCase();

                // Filtro rápido no Java para ter certeza de que o termo alvo está presente
                boolean isHackathon = tituloLower.contains("hackathon") ||
                        tituloLower.contains("maratona de");

                if (isHackathon) {
                    if (!hackathonRepository.existsByUrl(link)) {

                        // Passa pelo curador IA (O Juiz Geográfico)
                        String resumoAi = geminiApiService.gerarResumoHackathon(titulo);

                        if (resumoAi != null && (
                                resumoAi.trim().equalsIgnoreCase("IGNORAR") ||
                                        resumoAi.trim().equalsIgnoreCase("EXPIRADO") ||
                                        resumoAi.startsWith("Erro") || // Impede que mensagens de erro vão pro Discord
                                        resumoAi.startsWith("429"))) {

                            logger.info("Hackathon bloqueado pela IA (Fora de SP, Expirado ou Reportagem): {}", titulo);

                            Hackathon eventoIgnorado = Hackathon.builder()
                                    .titulo(titulo).url(link).resumoAi(resumoAi).dataPublicacao(LocalDateTime.now()).enviado(true).build();
                            hackathonRepository.save(eventoIgnorado);
                            continue;
                        }

                        Hackathon novoEvento = Hackathon.builder()
                                .titulo(titulo)
                                .url(link)
                                .resumoAi(resumoAi)
                                .dataPublicacao(LocalDateTime.now())
                                .enviado(true)
                                .build();

                        hackathonRepository.save(novoEvento);
                        discordWebhookService.enviarHackathon(novoEvento);

                        eventosEnviadosNaRodada++;
                        logger.info("✅ Novo Hackathon enviado: {}", titulo);

                        // Delay super seguro de 15 segundos para não estourar a cota da API Free
                        Thread.sleep(15000);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar Hackathons: {}", e.getMessage());
        }
    }
}