package com.github.daniellramos09.discordvagas.domain.evento;

import org.springframework.stereotype.Component;

@Component
public class EventoMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Radar de Eventos & Hackathons";
    private static final int DISCORD_MAX_LENGTH = 2000;

    public String format(String titulo, String resumoAi, String url) {
        String message = String.format(
                "🏁 **Novo Evento / Hackathon no Radar!**\n\n" +
                        "**Oportunidade:** %s\n\n" +
                        "🤖 **Curadoria da IA:**\n%s\n\n" +
                        "🙋‍♂️ **Quer formar time? Comenta aqui no chat!**\n" +
                        "🔗 **Link Oficial:** %s",
                titulo, resumoAi, url
        );

        if (message.length() > DISCORD_MAX_LENGTH) {
            return message.substring(0, DISCORD_MAX_LENGTH - 3) + "...";
        }

        return message;
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
