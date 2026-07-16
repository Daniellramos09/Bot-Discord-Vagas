package com.github.daniellramos09.discordvagas.domain.opensource;

import org.springframework.stereotype.Component;

@Component
public class OpenSourceMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Open Source Bot";

    public String format(String repositorio, String titulo, String linguagem, String resumoAi, String url) {
        return String.format(
                "🌟 **Nova Oportunidade Open Source (Good First Issue)!**\n\n" +
                        "**Repositório:** `%s`\n" +
                        "**Título:** %s\n" +
                        "**Linguagem:** %s\n\n" +
                        "🤖 **Resumo da IA:**\n%s\n\n" +
                        "🔗 **Acesse aqui para contribuir:** %s",
                repositorio, titulo, linguagem, resumoAi, url
        );
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
