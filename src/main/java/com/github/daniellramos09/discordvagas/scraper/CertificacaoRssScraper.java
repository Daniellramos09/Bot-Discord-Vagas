package com.github.daniellramos09.discordvagas.scraper;

import com.github.daniellramos09.discordvagas.entity.Certificacao;
import com.github.daniellramos09.discordvagas.repository.CertificacaoRepository;
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
import java.util.List;
import java.util.Map;

@Service
public class CertificacaoRssScraper {

    private static final Logger logger = LoggerFactory.getLogger(CertificacaoRssScraper.class);

    private final CertificacaoRepository certificacaoRepository;
    private final DiscordWebhookService discordWebhookService;
    private final GeminiApiService geminiApiService;

    private static final Map<String, String> FONTES_RSS = Map.ofEntries(
            Map.entry("AWS", "https://aws.amazon.com/blogs/training-and-certification/feed/"),
            Map.entry("Microsoft Learn", "https://techcommunity.microsoft.com/api/blog/rss/blogboard/MicrosoftLearnBlog"),
            Map.entry("GitHub Blog", "https://github.blog/feed/"),
            Map.entry("Google Cloud", "https://cloudblog.withgoogle.com/rss/"),
            Map.entry("Oracle", "https://blogs.oracle.com/oci/rss"),
            Map.entry("Linux Foundation", "https://training.linuxfoundation.org/feed/"),
            Map.entry("HashiCorp", "https://www.hashicorp.com/blog/rss.xml"),
            Map.entry("freeCodeCamp", "https://www.freecodecamp.org/news/rss/")
    );

    // Filtros pesados: só queremos notícias que falem de coisas gratuitas ou desafios
    private static final List<String> PALAVRAS_CHAVE = List.of(
            "voucher", "free", "discount", "desconto", "challenge", "grátis", "scholarship", "bolsa"
    );

    public CertificacaoRssScraper(CertificacaoRepository certificacaoRepository,
                                  DiscordWebhookService discordWebhookService,
                                  GeminiApiService geminiApiService) {
        this.certificacaoRepository = certificacaoRepository;
        this.discordWebhookService = discordWebhookService;
        this.geminiApiService = geminiApiService;
    }

    public void buscarOportunidades() {
        for (Map.Entry<String, String> fonte : FONTES_RSS.entrySet()) {
            logger.info("-> Lendo Feed RSS da fonte: {}", fonte.getKey());
            processarFeed(fonte.getKey(), fonte.getValue());
        }
    }

    private void processarFeed(String nomeFonte, String urlRss) {
        try {
            // Usa o Jsoup com o Parser de XML nativo
            Document doc = Jsoup.connect(urlRss).parser(Parser.xmlParser()).get();
            Elements items = doc.select("item");

            for (Element item : items) {
                String titulo = item.select("title").text();
                String link = item.select("link").text();
                String tituloMinusc = titulo.toLowerCase();

                // Verifica se alguma palavra-chave existe no título
                boolean isOportunidade = PALAVRAS_CHAVE.stream().anyMatch(tituloMinusc::contains);

                if (isOportunidade) {
                    // Verifica se já processamos este link antes
                    if (!certificacaoRepository.existsByUrl(link)) {

                        logger.info("Nova oportunidade de certificação encontrada: {}", titulo);

                        // Chama o Gemini para o resumo
                        String resumoAi = geminiApiService.gerarResumoCertificacao(titulo, nomeFonte);

                        // Monta o objeto e salva no banco
                        Certificacao novaCert = Certificacao.builder()
                                .titulo(titulo)
                                .url(link)
                                .fonte(nomeFonte)
                                .resumoAi(resumoAi)
                                .dataPublicacao(LocalDateTime.now())
                                .enviado(true)
                                .build();

                        certificacaoRepository.save(novaCert);

                        // Dispara para o Discord
                        discordWebhookService.enviarCertificacao(novaCert);

                        // Pausa de 2 segundos para não sobrecarregar a API do Gemini
                        Thread.sleep(2000);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao processar o feed da fonte {}: {}", nomeFonte, e.getMessage());
        }
    }
}