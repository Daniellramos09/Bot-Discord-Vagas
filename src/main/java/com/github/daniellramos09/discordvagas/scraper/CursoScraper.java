package com.github.daniellramos09.discordvagas.scraper;

import com.github.daniellramos09.discordvagas.entity.Curso;
import com.github.daniellramos09.discordvagas.repository.CursoRepository;
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
public class CursoScraper {

    private static final Logger logger = LoggerFactory.getLogger(CursoScraper.class);
    private final CursoRepository cursoRepository;
    private final DiscordWebhookService discordWebhookService;
    private final GeminiApiService geminiApiService;

    // URL super restrita: Exige "São Paulo" OU "SP" + Palavras de TI + Gratuito
    // URL Equilibrada: Busca vagas em SP OU formato Online/EAD/Brasil + Tecnologias + Gratuidade
    // URL Ultra Otimizada: Expande termos de TI e bloqueia ativamente locais físicos fora de SP
    private static final String GOOGLE_NEWS_RSS =
            "https://news.google.com/rss/search?q=(\"São+Paulo\"+OR+\"SP\"+OR+\"online\"+OR+\"EAD\"+OR+\"remoto\"+OR+\"imersão\"+OR+\"virtual\"+OR+\"EaD\")+(\"tecnologia\"+OR+\"programação\"+OR+\"dados\"+OR+\"inteligência+artificial\"+OR+\"segurança\"+OR+\"TI\"+OR+\"desenvolvimento\"+OR+\"software\")+(\"curso+gratuito\"+OR+\"bootcamp\"+OR+\"bolsa\")+when:45d&hl=pt-BR&gl=BR&ceid=BR:pt-419";
    public CursoScraper(CursoRepository cursoRepository, DiscordWebhookService discordWebhookService, GeminiApiService geminiApiService) {
        this.cursoRepository = cursoRepository;
        this.discordWebhookService = discordWebhookService;
        this.geminiApiService = geminiApiService;
    }

    public void buscarCursosBrasileiros() {
        try {
            logger.info("-> Buscando Cursos e Bootcamps de Tech em SP...");
            Document doc = Jsoup.connect(GOOGLE_NEWS_RSS)
                    .userAgent("Mozilla/5.0")
                    .parser(Parser.xmlParser())
                    .get();

            Elements items = doc.select("item");
            ZonedDateTime dataLimite = ZonedDateTime.now().minusDays(45);

            // TRAVA DE SPAM: Máximo de mensagens enviadas por vez
            int cursosEnviadosNaRodada = 0;
            int maximoPorRodada = 3;

            for (Element item : items) {
                // Se já enviou o máximo permitido, para o loop
                if (cursosEnviadosNaRodada >= maximoPorRodada) {
                    logger.info("Limite de {} cursos atingido nesta rodada. O resto fica para depois.", maximoPorRodada);
                    break;
                }

                String titulo = item.select("title").text();
                String link = item.select("link").text();
                String dataPublicacaoStr = item.select("pubDate").text();

                try {
                    ZonedDateTime dataDaNoticia = ZonedDateTime.parse(dataPublicacaoStr, DateTimeFormatter.RFC_1123_DATE_TIME);
                    if (dataDaNoticia.isBefore(dataLimite)) {
                        continue; // Passou de 45 dias, ignora
                    }
                } catch (Exception e) {
                    logger.warn("Data inválida para o curso: {}", titulo);
                }

                String tituloLower = titulo.toLowerCase();

                // 1. Ampliação de palavras-chave de Tecnologia (IA, Dados, Segurança, Cloud, etc.)
                boolean isTech = tituloLower.contains("tecnologia") ||
                        tituloLower.contains("programação") ||
                        tituloLower.contains("programacao") ||
                        tituloLower.contains("ti ") ||
                        tituloLower.contains("dados") ||
                        tituloLower.contains("dev") ||
                        tituloLower.contains("inteligência artificial") ||
                        tituloLower.contains("inteligencia artificial") ||
                        tituloLower.contains("ia") ||
                        tituloLower.contains(" ai ") || // espaço para não bater em "mais", "pais"
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

                // 2. Barreira rápida contra público infantil
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

                if (isTech && isGratuito && !isParaCriancas){

                    if (!cursoRepository.existsByUrl(link)) {

                        // Filtro nível 2 (Gemini)
                        String resumoAi = geminiApiService.gerarResumoCurso(titulo);

                        if (resumoAi != null && (resumoAi.trim().equalsIgnoreCase("EXPIRADO") || resumoAi.trim().equalsIgnoreCase("IGNORAR"))) {
                            logger.info("Curso bloqueado pela IA (Não é TI ou expirou): {}", titulo);

                            // Salva no banco para não processar de novo, mas não envia
                            Curso cursoIgnorado = Curso.builder()
                                    .titulo(titulo).url(link).resumoAi(resumoAi).dataPublicacao(LocalDateTime.now()).enviado(true).build();
                            cursoRepository.save(cursoIgnorado);
                            continue;
                        }

                        Curso novoCurso = Curso.builder()
                                .titulo(titulo)
                                .url(link)
                                .resumoAi(resumoAi)
                                .dataPublicacao(LocalDateTime.now())
                                .enviado(true)
                                .build();

                        cursoRepository.save(novoCurso);
                        discordWebhookService.enviarCurso(novoCurso);

                        cursosEnviadosNaRodada++; // Conta que enviou um
                        logger.info("✅ Novo Curso Tech SP enviado: {}", titulo);

                        Thread.sleep(2000); // Pausa para a API do Gemini respirar
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar cursos: {}", e.getMessage());
        }
    }
}