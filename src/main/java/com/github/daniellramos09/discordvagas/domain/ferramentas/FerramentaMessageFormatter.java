package com.github.daniellramos09.discordvagas.domain.ferramentas;

import org.springframework.stereotype.Component;

@Component
public class FerramentaMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Ferramentas Open Source";

    public String format(String owner, String nomeRepo, String linguagem,
                         long stars, long forks, String resumoAi, String url) {
        return String.format(
                "🔧 **Ferramenta Open Source Incrível no Radar!**\n\n" +
                        "**Repositório:** `%s/%s`\n" +
                        "**Linguagem:** %s\n" +
                        "**Stars:** ⭐ %d | **Forks:** 🍴 %d\n\n" +
                        "🤖 **O que a IA achou:**\n%s\n\n" +
                        "🔗 **Confira o repositório:** %s",
                owner, nomeRepo, linguagem, stars, forks, resumoAi, url
        );
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
