package com.github.daniellramos09.discordvagas.domain.hackathon;

import org.springframework.stereotype.Component;

@Component
public class HackathonMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Radar de Hackathons";

    public String format(String titulo, String resumoAi, String url) {
        return String.format(
                "🏁 **Hora de Codar! Novo Hackathon no Radar!**\n\n" +
                        "**Oportunidade:** %s\n\n" +
                        "👾 **O que o nosso Bot descobriu:**\n%s\n\n" +
                        "🙋‍♂️ **Quem quer formar time? Levanta a mão aqui no chat!**\n" +
                        "🔗 **Link Oficial:** %s",
                titulo, resumoAi, url
        );
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
