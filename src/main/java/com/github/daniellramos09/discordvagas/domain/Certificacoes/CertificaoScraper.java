package com.github.daniellramos09.discordvagas.domain.Certificacoes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CertificaoScraper {

    private static final Logger logger = LoggerFactory.getLogger(CertificaoScraper.class);

    private final ObjectMapper objectMapper;

    public CertificaoScraper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<CertificaoRecord> scrape() {
        List<CertificaoRecord> results = new ArrayList<>();

        try {
            logger.info("-> Carregando base de dados de certificações...");
            ClassPathResource resource = new ClassPathResource("data/certificacoes.json");
            InputStream inputStream = resource.getInputStream();

            List<Map<String, String>> certificacoes = objectMapper.readValue(
                    inputStream, new TypeReference<>() {});

            for (Map<String, String> cert : certificacoes) {
                String titulo = cert.get("titulo");
                String link = cert.get("link");
                String descricao = cert.get("descricao");
                String publicoAlvo = cert.get("publicoAlvo");
                String documentacaoOficial = cert.get("documentacaoOficial");
                String reposEstudo = cert.get("reposEstudo");

                results.add(new CertificaoRecord(
                        titulo, link, descricao, publicoAlvo,
                        documentacaoOficial, reposEstudo));
            }

            logger.info("Carregadas {} certificações da base de dados.", results.size());
        } catch (Exception e) {
            logger.error("Erro ao carregar certificações: {}", e.getMessage(), e);
        }

        return results;
    }
}
