package com.github.daniellramos09.discordvagas.domain.Certificacoes;

import org.springframework.stereotype.Component;

@Component
public class CertificacaoMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Certificações Tech";

    public String format(String titulo, String resumoAi, String url,
                         String documentacaoOficial, String reposEstudo) {
        return String.format(
                "🏅 **Nova Certificação no Radar!**\n\n" +
                        "**Certificação:** %s\n\n" +
                        "🤖 **Resumo da IA:**\n%s\n\n" +
                        "📚 **Documentação Oficial:** %s\n" +
                        "📂 **Repositórios de Estudo:** %s\n" +
                        "🔗 **Saiba Mais:** %s",
                titulo, resumoAi, documentacaoOficial, reposEstudo, url
        );
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
