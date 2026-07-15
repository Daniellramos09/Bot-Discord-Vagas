package com.github.daniellramos09.discordvagas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
public class GeminiApiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiApiService.class);

    // A URL com o nome EXATO do modelo exigido pelo Google
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final RestTemplate restTemplate;
    private final String apiKey;

    public GeminiApiService(RestTemplate restTemplate, @Value("${gemini.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String gerarResumo(String descricaoBruta) {
        String prompt = String.format(
                "Analise a descrição desta vaga de TI e gere um resumo altamente organizado para o Discord. " +
                        "Use estritamente o formato abaixo, mantendo as quebras de linha para o texto não ficar amontoado:\n\n" +
                        "º Região: [Liste as cidades/regiões principais. Se houver São Paulo/SP, destaque-a]\n" +
                        "º Cargo: [Título do cargo ou programa]\n" +
                        "º O que fará: [Resumo conciso das atividades]\n" +
                        "º Resumo sobre a empresa: [Breve descrição sobre quem é a empresa]\n" +
                        "º Missão, Visão e Valores: [Descreva brevemente a cultura ou propósito da empresa com base no texto]\n\n" +
                        "Seja direto e conciso. Descrição da vaga:\n%s",

                descricaoBruta
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Transformamos a String em um objeto URI blindado
            URI uri = URI.create(GEMINI_API_URL + "?key=" + apiKey);

            Map<String, Object> response = restTemplate.postForObject(uri, request, Map.class);

            if (response != null && response.containsKey("candidates")) {
                Map<String, Object> candidate = ((java.util.List<Map<String, Object>>) response.get("candidates")).get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                java.util.List<Map<String, Object>> parts = (java.util.List<Map<String, Object>>) content.get("parts");
                return (String) parts.get(0).get("text");
            }

            return "Erro ao gerar resumo: resposta inválida da API";

        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao chamar API Gemini: {}", e.getMessage(), e);
            return "Erro ao gerar resumo: " + e.getMessage();
        }
    }


    public String gerarResumoOpenSource(String titulo, String descricaoBruta) {
        String prompt = String.format(
                "Você é um desenvolvedor sênior ajudando profissionais juniores. Leia o título e a descrição desta issue " +
                        "do GitHub (marcada como 'good first issue') e faça um resumo direto, encorajador e conciso de no máximo 3 linhas " +
                        "em português, explicando claramente qual é o problema e o que precisa ser feito no código para resolvê-lo.\n\n" +
                        "Título da Issue: %s\n" +
                        "Descrição da Issue:\n%s",
                titulo, descricaoBruta
        );

        return executarRequisicaoGemini(prompt);
    }

    // Método privado auxiliar para evitar a repetição de código de montagem de payload JSON
    private String executarRequisicaoGemini(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            URI uri = URI.create(GEMINI_API_URL + "?key=" + apiKey);
            Map<String, Object> response = restTemplate.postForObject(uri, request, Map.class);

            if (response != null && response.containsKey("candidates")) {
                Map<String, Object> candidate = ((java.util.List<Map<String, Object>>) response.get("candidates")).get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                java.util.List<Map<String, Object>> parts = (java.util.List<Map<String, Object>>) content.get("parts");
                return (String) parts.get(0).get("text");
            }

            return "Erro ao gerar resumo: resposta inválida da API do Gemini";

        } catch (org.springframework.web.client.RestClientException e) {
            logger.error("Erro ao chamar API Gemini: {}", e.getMessage(), e);
            return "Erro ao gerar resumo através da IA: " + e.getMessage();
        }
    }

    public String gerarResumoCurso(String titulo) {
        String prompt = String.format(
                "Você é um curador especialista em educação de tecnologia para o estado de São Paulo. " +
                        "Avalie rigorosamente se o título desta notícia descreve um curso adequado para o nosso público:\n" +
                        "Notícia: '%s'\n\n" +
                        "REGRAS DE FILTRO:\n" +
                        "1. O curso DEVE ser nas áreas de tecnologia, programação, ciência de dados, inteligência artificial, segurança da informação (cybersecurity) ou infraestrutura/cloud.\n" +
                        "2. O curso pode ser ONLINE/EAD ou PRESENCIAL (em São Paulo ou Grande SP).\n" +
                        "3. O público-alvo DEVE ser: alunos de Ensino Médio, ensino Técnico, Graduação/Faculdade ou pessoas já na área buscando especialização (Juniores, Plenos, ou pessoas em transição de carreira).\n" +
                        "4. Se o curso for voltado para CRIANÇAS/PÚBLICO INFANTIL (ex: robótica infantil, Scratch para crianças, desenvolvimento de games para menores de 12 anos, escola de programação infantil), responda APENAS com a palavra: IGNORAR.\n" +
                        "5. Se o título indicar claramente que as inscrições já fecharam ou se o assunto principal não for tecnologia, responda APENAS com a palavra: IGNORAR ou EXPIRADO.\n\n" +
                        "Caso passe em todas as regras, faça um resumo altamente motivador de exatamente 2 linhas em português brasileiro, destacando:\n" +
                        "- O que vão aprender (foco técnico)\n" +
                        "- O formato (Se é Online ou Presencial em SP)\n" +
                        "- Indique para quem é recomendado (ex: 'Ideal para quem busca a primeira vaga' ou 'Ótimo para juniores/plenos').",
                        " Se o curso for PRESENCIAL (físico) em qualquer estado que NÃO seja São Paulo (como Rio de Janeiro, Minas Gerais, Paraná, etc.), responda APENAS com a palavra: IGNORAR. Mas se o curso for 100% ONLINE ou EAD, mesmo que organizado por uma instituição de outro estado (ex: Prefeitura do Rio, IFMG, etc.), você DEVE APROVAR.",

                titulo
        );
        return executarRequisicaoGemini(prompt);
    }


    public String gerarResumoHackathon(String titulo) {
        String prompt = String.format(
                "Você é um líder de comunidades de programação auxiliando estudantes do Brasil a criarem times para Hackathons.\n" +
                        "Leia o título desta notícia: '%s'\n\n" +
                        "REGRAS OBRIGATÓRIAS:\n" +
                        "1. Se a notícia informar apenas os 'vencedores' de um hackathon passado ou que as inscrições encerraram, responda APENAS com a palavra: IGNORAR.\n" +
                        "2. Se for um Hackathon PRESENCIAL (físico) e a cidade listada NÃO for São Paulo ou alguma cidade da Grande SP, responda APENAS com a palavra: IGNORAR.\n" +
                        "3. Se o Hackathon for ONLINE/EAD/VIRTUAL, você DEVE APROVAR, independentemente do estado ou instituição.\n" +
                        "4. Aprove apenas Hackathons que possui inscrições abertas para os candidatos se inscreverem. \n\n" +
                        "Se aprovado com base nas regras acima, escreva um convite animado de 2 linhas focado em motivar os estudantes a formarem um esquadrão no nosso Discord. Informe se o evento é ONLINE ou PRESENCIAL (SP) e qual o tema principal.",

                titulo
        );

        return executarRequisicaoGemini(prompt);
    }

}