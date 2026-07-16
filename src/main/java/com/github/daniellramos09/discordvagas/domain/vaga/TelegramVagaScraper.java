package com.github.daniellramos09.discordvagas.domain.vaga;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramVagaScraper implements VagaScraper {

    private static final Logger logger = LoggerFactory.getLogger(TelegramVagaScraper.class);

    private static final List<String> CANAIS_TELEGRAM = List.of(
            "https://t.me/s/CafeinaVagas",
            "https://t.me/s/VagasBRTI",
            "https://t.me/s/ciadeestagios",
            "https://t.me/s/estagiosDasi",
            "https://t.me/s/cizadetalentos",
            "https://t.me/s/sejatrainee",
            "https://t.me/s/vagasbackend",
            "https://t.me/s/vagasqa",
            "https://t.me/s/vagas_dev"
    );

    private static final int LIMITE_VAGAS = 8;
    private static final int TIMEOUT_MS = 15000;
    private static final int DIAS_LIMITE_VAGA = 30;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    private static final String[] PALAVRAS_TI = {
            "ti", "tecnologia", "desenvolvedor", "dados", "engenharia", "cyber",
            "projetos", "programação", "dev", "suporte", "engineering", "engineer",
            "technology", "developer", "software", "data", "it ", "backend", "frontend", "cloud"
    };

    private static final String[] PALAVRAS_ESTAGIO = {
            "estágio", "estagio", "estagiário", "estagiario", "internship", "intern"
    };

    private static final String[] PALAVRAS_FALSO_POSITIVO = {
            "administrativo", "administração", "recepção", "vendas", "atendimento"
    };

    private static final String[] PALAVRAS_SAO_PAULO = {
            "são paulo", "sao paulo", "#sp", "conceição", "faria lima", "paulista",
            "vila olímpia", "berrini", "morumbi", "tatuapé", "suzano"
    };

    private static final String[] PALAVRAS_REMOTO = {
            "#remoto", "remoto"
    };

    @Override
    public List<Vaga> buscarVagas() {
        List<Vaga> vagas = new ArrayList<>();

        loopCanais:
        for (String urlCanal : CANAIS_TELEGRAM) {
            try {
                Document doc = conectarAoCanal(urlCanal);
                Elements messageElements = doc.select(".tgme_widget_message");

                for (Element messageElement : messageElements) {
                    try {
                        Vaga vaga = processarMensagem(messageElement);
                        if (vaga != null) {
                            vagas.add(vaga);
                            if (vagas.size() >= LIMITE_VAGAS) {
                                break loopCanais;
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Erro ao processar mensagem do Telegram: {}", e.getMessage(), e);
                    }
                }
            } catch (IOException e) {
                logger.error("Erro ao conectar ao canal Telegram ({}): {}", urlCanal, e.getMessage(), e);
            }
        }

        return vagas;
    }

    private Document conectarAoCanal(String urlCanal) throws IOException {
        return Jsoup.connect(urlCanal)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();
    }

    private Vaga processarMensagem(Element messageElement) {
        Element textElement = messageElement.selectFirst(".tgme_widget_message_text");
        Element dateElement = messageElement.selectFirst(".tgme_widget_message_date");

        if (textElement == null || dateElement == null) {
            return null;
        }

        String urlVagaReal = extrairUrlVaga(messageElement, textElement);
        if (urlVagaReal == null || urlVagaReal.isEmpty()) {
            return null;
        }

        if (!validarTempoVaga(dateElement)) {
            return null;
        }

        String textoCompleto = textElement.text();
        if (textoCompleto.isEmpty()) {
            return null;
        }

        String[] linhas = textoCompleto.split("\n");
        String titulo = linhas[0].trim();
        String descricaoBruta = textoCompleto;

        FiltroVaga filtro = aplicarFiltros(textoCompleto);

        if (filtro.passouFiltro()) {
            return new Vaga(titulo, urlVagaReal, descricaoBruta);
        } else {
            logger.debug("Descartada [{}] -> {}", titulo, filtro.getMotivoRejeicao());
            return null;
        }
    }

    private String extrairUrlVaga(Element messageElement, Element textElement) {
        Element botaoInline = messageElement.selectFirst(".tgme_widget_message_inline_keyboard .tgme_widget_message_inline_button");
        Element linkNoTexto = textElement.selectFirst("a[href]");

        if (botaoInline != null) {
            return botaoInline.attr("href");
        } else if (linkNoTexto != null) {
            return linkNoTexto.attr("href");
        }
        return null;
    }

    private boolean validarTempoVaga(Element dateElement) {
        Element timeElement = dateElement.selectFirst("time");
        if (timeElement != null) {
            String dataHoraTelegram = timeElement.attr("datetime");
            if (!dataHoraTelegram.isEmpty()) {
                java.time.OffsetDateTime dataDaVaga = java.time.OffsetDateTime.parse(dataHoraTelegram);
                java.time.OffsetDateTime limiteDeTempo = java.time.OffsetDateTime.now().minusDays(DIAS_LIMITE_VAGA);
                return !dataDaVaga.isBefore(limiteDeTempo);
            }
        }
        return true;
    }

    private FiltroVaga aplicarFiltros(String textoCompleto) {
        String textoLower = textoCompleto.toLowerCase();

        boolean isTI = verificarAreaTI(textoLower);
        boolean isEstagio = verificarCargoEstagio(textoLower);
        boolean isFalsoPositivo = verificarFalsoPositivo(textoLower);
        boolean isSaoPaulo = verificarLocalizacaoSaoPaulo(textoLower);
        boolean isRemoto = verificarTrabalhoRemoto(textoLower);

        return new FiltroVaga(isTI, isEstagio, isFalsoPositivo, isSaoPaulo, isRemoto);
    }

    private boolean verificarAreaTI(String textoLower) {
        for (String palavra : PALAVRAS_TI) {
            if (palavra.equals("ti")) {
                if (textoLower.matches(".*\\bti\\b.*")) {
                    return true;
                }
            } else if (textoLower.contains(palavra)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarCargoEstagio(String textoLower) {
        for (String palavra : PALAVRAS_ESTAGIO) {
            if (textoLower.contains(palavra)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarFalsoPositivo(String textoLower) {
        for (String palavra : PALAVRAS_FALSO_POSITIVO) {
            if (textoLower.contains(palavra)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarLocalizacaoSaoPaulo(String textoLower) {
        for (String palavra : PALAVRAS_SAO_PAULO) {
            if (palavra.equals("sp")) {
                if (textoLower.matches(".*\\bsp\\b.*")) {
                    return true;
                }
            } else if (textoLower.contains(palavra)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarTrabalhoRemoto(String textoLower) {
        for (String palavra : PALAVRAS_REMOTO) {
            if (palavra.equals("remoto")) {
                if (textoLower.matches(".*\\bremoto\\b.*")) {
                    return true;
                }
            } else if (textoLower.contains(palavra)) {
                return true;
            }
        }
        return false;
    }

    private static class FiltroVaga {
        private final boolean isTI;
        private final boolean isEstagio;
        private final boolean isFalsoPositivo;
        private final boolean isSaoPaulo;
        private final boolean isRemoto;

        public FiltroVaga(boolean isTI, boolean isEstagio, boolean isFalsoPositivo, boolean isSaoPaulo, boolean isRemoto) {
            this.isTI = isTI;
            this.isEstagio = isEstagio;
            this.isFalsoPositivo = isFalsoPositivo;
            this.isSaoPaulo = isSaoPaulo;
            this.isRemoto = isRemoto;
        }

        public boolean passouFiltro() {
            return isEstagio && isTI && !isFalsoPositivo && (isRemoto || isSaoPaulo);
        }

        public String getMotivoRejeicao() {
            return String.format("isTI=%s isCargoAlvo=%s isFalsoPositivo=%s isSaoPaulo=%s isRemoto=%s",
                    isTI, isEstagio, isFalsoPositivo, isSaoPaulo, isRemoto);
        }
    }
}
