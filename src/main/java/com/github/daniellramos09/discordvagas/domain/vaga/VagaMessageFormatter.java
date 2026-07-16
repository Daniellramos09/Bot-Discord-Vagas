package com.github.daniellramos09.discordvagas.domain.vaga;

import org.springframework.stereotype.Component;

@Component
public class VagaMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Vagas de Estágio Bot";

    public String format(String titulo, String resumoAi, String url) {
        return String.format(
                "**%s**\n\n" +
                        "**Resumo:**\n%s\n\n" +
                        "**Link:** %s",
                titulo, resumoAi, url
        );
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
