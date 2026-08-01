package com.github.daniellramos09.discordvagas.domain.vaga;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import com.github.daniellramos09.discordvagas.integration.DiscordClient;
import com.github.daniellramos09.discordvagas.integration.GeminiClient;
import com.github.daniellramos09.discordvagas.repository.VagaRepository;
import com.github.daniellramos09.discordvagas.scraper.VagaScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class VagaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(VagaOrchestrator.class);

    private static final String[] PALAVRAS_PROGRAMACAO = {
            "programação", "programacao", "desenvolvimento", "desenvolvedor", "desenvolvedora",
            "software", "engenharia de software", "backend", "frontend", "fullstack", "full stack",
            "web developer", "mobile developer", "sistema", "sistemas", "código", "codigo",
            "programador", "programadora", "devops", "sre", "arquiteto de software",
            "java", "python", "javascript", "typescript", "react", "angular", "node",
            "spring", "kotlin", "golang", "rust", "swift", "flutter", "ti", "tecnologia",
            "cyber", "dados", "cloud", "dev", "engineering", "engineer", "developer",
            "technology", "data", "infrastructure", "infraestrutura", "banco de dados",
            "machine learning", "inteligência artificial", "ia ", "ml ", "big data",
            "api", "microservice", "microserviço", "kubernetes", "docker", "aws", "azure",
            "linux", "git", "ci/cd", "agile", "scrum", "qa", "quality assurance",
            "test", "automação", "automação de testes", "mobile", "android", "ios",
            "ux", "ui", "design system", "figma", "html", "css", "sass", "php",
            "ruby", "c#", "c++", ".net", "scala", "perl", "lua", "r "
    };

    private static final String[] PALAVRAS_AREA_EXCLUIDA = {
            "jurídico", "juridico", "advocacia", "advogado", "advogada", "direito",
            "contabilidade", "contador", "contadora", "contábil",
            "recursos humanos", "rh ", "dp ", "departamento pessoal",
            "marketing", "comercial", "vendas", "telemarketing",
            "enfermagem", "enfermeiro", "enfermeira", "saúde", "saude", "médico", "medico",
            "odontologia", "odontológico", "fisioterapia", "nutrição", "psicologia",
            "hotelaria", "gastronomia", "culinária", "cozinheiro",
            "mecânica", "mecanico", "elétrica", "eletricista", "automotivo",
            "construção civil", "engenharia civil", "arquitetura",
            "educação", "educacao", "professor", "professora", "pedagogia",
            "logística", "logistica", "almoxarifado", "estoque",
            "agronegócio", "agricultura", "pecuária",
            "call center", "atendimento ao cliente", "suporte ao cliente"
    };

    private static final String[] PALAVRAS_VAGA_ENCERRADA = {
            "encerrada", "encerrado", "expirada", "expirado", "finalizada", "finalizado",
            "não mais disponível", "nao mais disponivel", "vaga preenchida",
            "processo seletivo encerrado", "inscrições encerradas", "inscricoes encerradas",
            "prazo encerrado", "vagas esgotadas", "vaga fechada",
            "deadline passed", "expired", "closed", "filled"
    };

    private static final String[] SENIORIDADE_ACEITA = {
            "estágio", "estagio", "estagiário", "estagiario", "internship", "intern",
            "júnior", "junior", "trainee", "entry level", "entry-level",
            "analista trainee", "desenvolvedor júnior", "desenvolvedor junior",
            "dev júnior", "dev junior", "programador júnior", "programador junior"
    };

    private static final String[] SENIORIDADE_EXCLUIDA = {
            "pleno", "sênior", "senior", "lead", "tech lead", "engineering manager",
            "gerente", "manager", "director", "diretor", "head ", "staff",
            "principal", "architect", "arquiteto", "coordenador", "coordenadora",
            "supervisor", "supervisora", "diretora", "vice-presidente", "vp ", "gestor", "pl", "PL",
            "SN", "sn"
    };

    private static final List<String> EMPRESAS_GRANDES = List.of(
            "itau", "itaú", "btg", "btg pactual", "volkswagen", "volks",
            "nubank", "qi tech", "qi ", "ibm", "binance", "mercado livre",
            "mercado pago", "amazon", "google", "microsoft", "meta", "facebook",
            "apple", "uber", "ifood", "99", "stone", "pagseguro", "pagbank",
            " Santander", "bradesco", "caixa", "petrobras", "vale",
            "totvs", "cielo", "redecard", "elogroup", "tivy", "sinqia",
            " Stefanini", "totvs", "dextra", " QuintoAndar", " Loft",
            "RD station", "resultados digitais", "vTEX", "vtex",
            "Accenture", "Deloitte", "PwC", "EY ", "KPMG", "Capgemini",
            "Cognizant", "Tata", "Infosys", "Wipro",
            "Netflix", "Spotify", "Twitter", "Linkedin", "Salesforce",
            "Oracle", "SAP", "Cisco", "Intel", "Qualcomm", "AMD",
            "Nvidia", "Tesla", "SpaceX", "Nubank", "Creditas",
            "Geru", "GuiaBolso", "Méliuz", "Vindi", "Hashdex",
            "Bitso", "Foxbit", "Mercado Bitcoin", "NovaDax",
            "Gympass", "Wellhub", "Cubo", "Arco", "Descomplica",
            "Hotmart", "Monetizze", "Eduzz", "Braze", "Zendesk",
            "Hubspot", "Slack", "Zoom", "Twilio", "Stripe",
            "Shopify", "Airbnb", "Uber", "iFood", "Rappi",
            "Loggi", "Gympass", "Unico", "Pismo", "Dock",
            "EBANX", "Cora", "Juro", "Belvo", "Promobit",
            "TechCrunch", "VTEX", "Locaweb", "UOL", "Globo",
            "Globo.com", "GVT", "Embrapa", "BNDES", "B3 ", "Ambev", "Accenture",
            "iFood", "Mercado Livre", "Bain & Company", "samsumg", "Serasa", "C6 Bank", "C6", "Creditas",
            "XP", "XP Inc", "Zippi", "Vivo", "Tim", "Claro"

    );

    private static final String[] PALAVRAS_ZONA_SUL_OESTE_CENTRO = {
            "berrini", "morumbi", "vila olímpia", "vila olimpia", "faria lima",
            "paulista", "avenida paulista", "pinheiros", "cidade jardim", "cidade jardin",
            "jardins", "jardim paulistano", "barueri", "brooklin", "vila mariana",
            "santo amaro", "vila cordeiro", "bela vista", "itaim", "itaim bibi",
            "consolação", "consolacao", "jardim europa", "jardim américa", "jardim america",
            "jardim anhangüera", "jardim ananguera", "vila nova", "moema",
            "campo belo", "campo bello", "planalto paulista", "chácara flora",
            "chacara flora", "vila work", "vila lúcia", "vila lucia",
            "alameda", "alamedas", "rua dos paulistanos", "av. rebouças",
            "rebouças", "haddock lobo", "augusta", "conselheiro furtado",
            "brasilio machado", "ochi flavio", "fly faria lima", "lagoa",
            "granja viana", "alphaville", "capital federal", "centro",
            "liberdade", "bela vista", "pacaembu", "paraiso", "paraíso",
            "chácara klabin", "chacara klabin", "saúde", "saude",
            "ipiranga", "brás", "bras", "mooca", "tatuapé",
            "sapopemba", "cursino", "vila prudente", "alto da lapa",
            "alto da mooca", "jardim helena", "vila matilde",
            "penha", "vila formosa", "aricanduva", "carrão"
    };

    private static final String[] PALAVRAS_SAO_PAULO_CAPITAL = {
            "são paulo", "sao paulo", "capital", "sp"
    };

    private static final String[] PALAVRAS_REMOTO = {
            "remoto", "remote", "home office", "homeoffice", "trabalho remoto"
    };

    private static final Set<String> PARAMS_RASTREAMENTO = Set.of(
            "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content",
            "ref", "source", "from", "track", "tracking", "click_id", "clid",
            "fbclid", "gclid", "msclkid", "mc_cid", "mc_eid",
            "pk_campaign", "pk_kwd", "pk_source", "pk_medium"
    );

    private final List<VagaScraper> scrapers;
    private final VagaPromptProvider promptProvider;
    private final VagaMessageFormatter formatter;
    private final GeminiClient geminiClient;
    private final DiscordClient discordClient;
    private final VagaRepository repository;

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    @Value("${vagas.max-per-run:5}")
    private int maxPerRun;

    public VagaOrchestrator(List<VagaScraper> scrapers,
                            VagaPromptProvider promptProvider,
                            VagaMessageFormatter formatter,
                            GeminiClient geminiClient,
                            DiscordClient discordClient,
                            VagaRepository repository) {
        this.scrapers = scrapers;
        this.promptProvider = promptProvider;
        this.formatter = formatter;
        this.geminiClient = geminiClient;
        this.discordClient = discordClient;
        this.repository = repository;
    }

    public int execute() {
        logger.info("Iniciando busca de vagas (limite: {} por rodada)...", maxPerRun);

        // Fase 1: Coletar vagas de TODOS os scrapers
        List<VagaComFonte> todasVagas = new ArrayList<>();
        for (VagaScraper scraper : scrapers) {
            try {
                List<Vaga> vagasEncontradas = scraper.buscarVagas();
                logger.info("Encontradas {} vagas pelo scraper: {}",
                        vagasEncontradas.size(), scraper.getNomeFonte());
                for (Vaga vaga : vagasEncontradas) {
                    todasVagas.add(new VagaComFonte(vaga, scraper.getNomeFonte()));
                }
            } catch (Exception e) {
                logger.error("Erro no scraper {}: {}", scraper.getNomeFonte(), e.getMessage(), e);
            }
        }

        logger.info("Total de vagas coletadas de todas as fontes: {}", todasVagas.size());

        // Fase 2: Filtrar duplicadas e já existentes no banco
        List<VagaComFonte> vagasFiltradas = new ArrayList<>();
        Set<String> urlsProcessadas = new HashSet<>();
        Set<String> titulosProcessados = new HashSet<>();

        for (VagaComFonte vf : todasVagas) {
            String urlNormalizada = normalizarUrl(vf.vaga.getUrl());
            String tituloNormalizado = normalizarTitulo(vf.vaga.getTitulo());

            // Checagem 1: URL duplicada na memória (mesma execução)
            if (urlsProcessadas.contains(urlNormalizada)) {
                logger.debug("Vaga duplicada (URL) descartada: {}", vf.vaga.getUrl());
                continue;
            }

            // Checagem 2: Título duplicado na memória (mesma execução)
            if (tituloNormalizado != null && titulosProcessados.contains(tituloNormalizado)) {
                logger.debug("Vaga duplicada (título) descartada: {}", vf.vaga.getTitulo());
                continue;
            }

            // Checagem 3: URL já existe no banco (normalizada)
            if (urlExisteNoBanco(urlNormalizada)) {
                logger.debug("Vaga já existe no banco (URL): {}", vf.vaga.getUrl());
                urlsProcessadas.add(urlNormalizada);
                continue;
            }

            // Checagem 4: Título já existe no banco
            if (tituloNormalizado != null && repository.existsByTitulo(vf.vaga.getTitulo())) {
                logger.debug("Vaga já existe no banco (título): {}", vf.vaga.getTitulo());
                titulosProcessados.add(tituloNormalizado);
                continue;
            }

            urlsProcessadas.add(urlNormalizada);
            if (tituloNormalizado != null) {
                titulosProcessados.add(tituloNormalizado);
            }
            vagasFiltradas.add(vf);
        }

        logger.info("Vagas após filtro de duplicatas: {}", vagasFiltradas.size());

        // Fase 2.5: Filtrar por área de atuação, senioridade e vagas encerradas
        List<VagaComFonte> vagasAreaFiltrada = new ArrayList<>();
        int descartadasForaEscopo = 0;
        int descartadasEncerradas = 0;
        int descartadasSenioridade = 0;

        for (VagaComFonte vf : vagasFiltradas) {
            String texto = (vf.vaga.getTitulo() + " " + vf.vaga.getDescricaoBruta()).toLowerCase();

            if (!isVagaDeTI(texto)) {
                descartadasForaEscopo++;
                logger.debug("Descartada (fora do escopo de TI): {}", vf.vaga.getTitulo());
                continue;
            }

            if (!isSenioridadeAceita(texto)) {
                descartadasSenioridade++;
                logger.debug("Descartada (senioridade não aceita): {}", vf.vaga.getTitulo());
                continue;
            }

            if (isVagaEncerrada(texto)) {
                descartadasEncerradas++;
                logger.debug("Descartada (vaga encerrada): {}", vf.vaga.getTitulo());
                continue;
            }

            vagasAreaFiltrada.add(vf);
        }

        logger.info("Vagas descartadas por área fora do escopo: {}", descartadasForaEscopo);
        logger.info("Vagas descartadas por senioridade (pleno/sênior/etc): {}", descartadasSenioridade);
        logger.info("Vagas descartadas por estar encerrada: {}", descartadasEncerradas);
        logger.info("Vagas após filtro de área, senioridade e encerradas: {}", vagasAreaFiltrada.size());

        // Fase 3: Pontuar e ordenar por prioridade
        for (VagaComFonte vf : vagasAreaFiltrada) {
            vf.pontuacao = calcularPontuacao(vf.vaga);
        }
        vagasAreaFiltrada.sort((a, b) -> Integer.compare(b.pontuacao, a.pontuacao));

        // Fase 4: Selecionar com diversidade de fontes (rodízio)
        List<VagaComFonte> selecionadas = selecionarComDiversidade(vagasAreaFiltrada);

        logger.info("Vagas selecionadas para envio: {}", selecionadas.size());

        // Fase 5: Processar e enviar (com tratamento de erro no Gemini)
        int vagasEnviadas = 0;
        int vagasComErroGemini = 0;
        for (VagaComFonte vf : selecionadas) {
            if (vagasEnviadas >= maxPerRun) {
                break;
            }

            Vaga vaga = vf.vaga;

            String prompt = promptProvider.buildPrompt(vaga.getDescricaoBruta());
            String resumo;
            try {
                resumo = geminiClient.generate(prompt);
            } catch (Exception e) {
                vagasComErroGemini++;
                logger.warn("Gemini falhou para a vaga '{}' ({}): {}. Pulando vaga.",
                        vaga.getTitulo(), vf.fonte, e.getMessage());

                // Se o Gemini falhou, envia um resumo genérico sem IA
                resumo = String.format(
                        "Empresa não informada na vaga.\n\n%s",
                        vaga.getDescricaoBruta()
                );
            }

            vaga.setResumoAi(resumo);
            vaga.setEnviado(true);

            try {
                Vaga vagaSalva = repository.save(vaga);
                String message = formatter.format(vagaSalva.getTitulo(), vagaSalva.getResumoAi(), vagaSalva.getUrl());
                discordClient.sendWebhook(webhookUrl, message, formatter.getUsername());

                vagasEnviadas++;
                logger.info("Vaga enviada ({}/{}) [{}] (pontuação: {}): {}",
                        vagasEnviadas, maxPerRun, vf.fonte, vf.pontuacao, vagaSalva.getTitulo());
            } catch (Exception e) {
                logger.error("Erro ao salvar/enviar vaga '{}': {}", vaga.getTitulo(), e.getMessage(), e);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (vagasComErroGemini > 0) {
            logger.warn("AVISO: {} vaga(s) foram enviadas sem resumo do Gemini devido a erros de quota/conexão.", vagasComErroGemini);
        }

        logger.info("Processamento de vagas concluído. Total enviado: {}", vagasEnviadas);
        return vagasEnviadas;
    }

    /**
     * Verifica se uma vaga com URL normalizada já existe no banco.
     * Normaliza a URL antes de comparar para pegar variações.
     */
    private boolean urlExisteNoBanco(String urlNormalizada) {
        // Busca todas as URLs similares no banco e compara normalizadas
        // Como não temos LIKE com normalização, fazemos busca por pedaços da URL
        if (urlNormalizada.isEmpty()) return false;

        // Extrai o path sem query params para buscar no banco
        try {
            URI uri = URI.create(urlNormalizada);
            String host = uri.getHost();
            String path = uri.getPath();
            if (host != null && path != null) {
                // Busca por host+path (sem query params) no banco
                String pattern = "%" + host + path + "%";
                List<Vaga> existentes = repository.findAll().stream()
                        .filter(v -> {
                            String urlDb = normalizarUrl(v.getUrl());
                            String hostPathDb = extrairHostPath(urlDb);
                            String hostPathNova = extrairHostPath(urlNormalizada);
                            return hostPathNova.equals(hostPathDb) && !hostPathNova.isEmpty();
                        })
                        .toList();
                return !existentes.isEmpty();
            }
        } catch (Exception e) {
            // Se não conseguir parsear, retorna falso (deixa passar)
        }
        return false;
    }

    /**
     * Extrai host + path de uma URL (sem query params, sem fragment, sem trailing slash).
     */
    private String extrairHostPath(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            String path = uri.getPath();
            if (host == null) return "";
            if (path == null) return host.toLowerCase();
            return (host + path).toLowerCase().replaceAll("/$", "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gate obrigatório: verifica se a vaga é da área de TI/tecnologia.
     * Uma vaga DEVE conter ao menos uma keyword de tech para ser considerada.
     * Também verifica se NÃO pertence a uma área excluída (jurídico, RH, etc.).
     */
    private boolean isVagaDeTI(String texto) {
        // Verifica se NÃO é de uma área excluída
        for (String palavra : PALAVRAS_AREA_EXCLUIDA) {
            if (palavra.equals("rh ") || palavra.equals("dp ")) {
                if (texto.matches(".*\\b(rh|dp)\\b.*")) {
                    return false;
                }
            } else if (texto.contains(palavra)) {
                return false;
            }
        }

        // Deve conter pelo menos UMA keyword de tech
        for (String palavra : PALAVRAS_PROGRAMACAO) {
            if (palavra.equals("r ") || palavra.equals("ti") || palavra.equals("ia ") || palavra.equals("ml ")) {
                if (texto.matches(".*\\b" + palavra.trim() + "\\b.*")) {
                    return true;
                }
            } else if (texto.contains(palavra)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se a vaga parece estar encerrada ou expirada.
     */
    private boolean isVagaEncerrada(String texto) {
        for (String palavra : PALAVRAS_VAGA_ENCERRADA) {
            if (texto.contains(palavra)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se a senioridade da vaga é aceita (estágio ou júnior).
     * Se mencionar senioridade excluída (pleno, sênior, etc.), rejeita.
     * Se não mencionar nenhuma senioridade, aceita (pode ser vaga mal descrita).
     */
    private boolean isSenioridadeAceita(String texto) {
        // Se menciona senioridade excluída, rejeita
        for (String palavra : SENIORIDADE_EXCLUIDA) {
            if (palavra.equals("head ") || palavra.equals("vp ") || palavra.equals("qi ")) {
                if (texto.matches(".*\\b" + palavra.trim() + "\\b.*")) {
                    return false;
                }
            } else if (texto.contains(palavra)) {
                return false;
            }
        }

        // Se menciona senioridade aceita, aceita
        for (String palavra : SENIORIDADE_ACEITA) {
            if (texto.contains(palavra)) {
                return true;
            }
        }

        // Se não menciona nenhuma senioridade, aceita (pode ser vaga genérica de tech)
        return true;
    }

    /**
     * Verifica se a vaga é de uma empresa grande/renomada.
     */
    private boolean isEmpresaGrande(String texto) {
        for (String empresa : EMPRESAS_GRANDES) {
            if (texto.contains(empresa.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    private int calcularPontuacao(Vaga vaga) {
        int pontuacao = 0;
        String texto = (vaga.getTitulo() + " " + vaga.getDescricaoBruta()).toLowerCase();

        // Bônus: Empresa grande/renomada (+5)
        if (isEmpresaGrande(texto)) {
            pontuacao += 5;
        }

        // Tech keywords (+3)
        for (String palavra : PALAVRAS_PROGRAMACAO) {
            if (texto.contains(palavra)) {
                pontuacao += 3;
                break;
            }
        }

        // Localização: Zona Sul/Oeste/Centro prioritária (+4)
        boolean isZonaPrioritaria = false;
        for (String palavra : PALAVRAS_ZONA_SUL_OESTE_CENTRO) {
            if (palavra.equals("sp")) {
                if (texto.matches(".*\\bsp\\b.*")) {
                    isZonaPrioritaria = true;
                    break;
                }
            } else if (texto.contains(palavra)) {
                isZonaPrioritaria = true;
                break;
            }
        }
        if (isZonaPrioritaria) {
            pontuacao += 4;
        } else {
            // Localização: São Paulo geral (+2)
            boolean isSaoPauloCapital = false;
            for (String palavra : PALAVRAS_SAO_PAULO_CAPITAL) {
                if (palavra.equals("sp")) {
                    if (texto.matches(".*\\bsp\\b.*")) {
                        isSaoPauloCapital = true;
                        break;
                    }
                } else if (texto.contains(palavra)) {
                    isSaoPauloCapital = true;
                    break;
                }
            }
            if (isSaoPauloCapital) {
                pontuacao += 2;
            }
        }

        // Remoto (+3)
        boolean isRemoto = false;
        for (String palavra : PALAVRAS_REMOTO) {
            if (texto.contains(palavra)) {
                isRemoto = true;
                break;
            }
        }
        if (isRemoto) {
            pontuacao += 3;
        }

        // Salário/benefício mencionado (+1)
        if (texto.contains("salário") || texto.contains("salario") || texto.contains("remuneração")
                || texto.contains("remuneracao") || texto.contains("r$") || texto.contains("benefício")
                || texto.contains("beneficio")) {
            pontuacao += 1;
        }

        // Estágio (+1)
        if (texto.contains("estágio") || texto.contains("estagio") || texto.contains("internship")
                || texto.contains("intern")) {
            pontuacao += 1;
        }

        return pontuacao;
    }

    private List<VagaComFonte> selecionarComDiversidade(List<VagaComFonte> ordenadasPorPontuacao) {
        List<VagaComFonte> selecionadas = new ArrayList<>();
        Map<String, Integer> contagemPorFonte = new HashMap<>();

        int limitePorFonte = Math.max(1, (int) Math.ceil(maxPerRun * 0.6));
        int limiteJooble = 1;

        for (VagaComFonte vf : ordenadasPorPontuacao) {
            if (selecionadas.size() >= maxPerRun) {
                break;
            }

            int contagemFonte = contagemPorFonte.getOrDefault(vf.fonte, 0);

            // Limite específico para Jooble: no máximo 1 vaga
            if (vf.fonte.contains("Jooble") && contagemFonte >= limiteJooble) {
                continue;
            }

            // Limite geral para outras fontes
            if (!vf.fonte.contains("Jooble") && contagemFonte >= limitePorFonte
                    && selecionadas.size() < maxPerRun - 1) {
                continue;
            }

            contagemPorFonte.put(vf.fonte, contagemFonte + 1);
            selecionadas.add(vf);
        }

        if (selecionadas.size() < maxPerRun) {
            Set<String> urlsJaSelecionadas = new HashSet<>();
            for (VagaComFonte vf : selecionadas) {
                urlsJaSelecionadas.add(normalizarUrl(vf.vaga.getUrl()));
            }

            for (VagaComFonte vf : ordenadasPorPontuacao) {
                if (selecionadas.size() >= maxPerRun) {
                    break;
                }
                if (urlsJaSelecionadas.contains(normalizarUrl(vf.vaga.getUrl()))) {
                    continue;
                }

                int contagemFonte = contagemPorFonte.getOrDefault(vf.fonte, 0);

                // Limite específico para Jooble: no máximo 1 vaga
                if (vf.fonte.contains("Jooble") && contagemFonte >= limiteJooble) {
                    continue;
                }

                contagemPorFonte.put(vf.fonte, contagemFonte + 1);
                selecionadas.add(vf);
                urlsJaSelecionadas.add(normalizarUrl(vf.vaga.getUrl()));
            }
        }

        logger.info("Distribuição de fontes selecionadas:");
        contagemPorFonte.forEach((fonte, contagem) ->
                logger.info("  {}: {} vaga(s)", fonte, contagem));

        return selecionadas;
    }

    /**
     * Normaliza uma URL para comparação: remove query params de rastreamento,
     * normaliza esquema, host, path e trailing slash.
     */
    private String normalizarUrl(String url) {
        if (url == null) return "";
        try {
            URI uri = URI.create(url.trim().toLowerCase()
                    .replaceAll("^http://", "https://"));

            String host = uri.getHost();
            String path = uri.getPath();
            if (host == null) return url.trim().toLowerCase();

            // Reconstrói sem query params de rastreamento
            String query = uri.getQuery();
            if (query != null && !query.isEmpty()) {
                String queryLimpa = Arrays.stream(query.split("&"))
                        .map(param -> param.split("=", 2)[0].toLowerCase())
                        .filter(param -> !PARAMS_RASTREAMENTO.contains(param))
                        .reduce((a, b) -> a + "&" + b)
                        .orElse("");

                return (host + (path != null ? path : "") + (queryLimpa.isEmpty() ? "" : "?" + queryLimpa))
                        .replaceAll("/$", "");
            }

            return (host + (path != null ? path : "")).replaceAll("/$", "");
        } catch (Exception e) {
            return url.trim().toLowerCase()
                    .replaceAll("^http://", "https://")
                    .replaceAll("/$", "");
        }
    }

    /**
     * Normaliza o título para comparação de duplicatas.
     */
    private String normalizarTitulo(String titulo) {
        if (titulo == null) return null;
        return titulo.toLowerCase()
                .replaceAll("[^a-záàâãéèêíïóôõúüç\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static class VagaComFonte {
        final Vaga vaga;
        final String fonte;
        int pontuacao;

        VagaComFonte(Vaga vaga, String fonte) {
            this.vaga = vaga;
            this.fonte = fonte;
            this.pontuacao = 0;
        }
    }
}
