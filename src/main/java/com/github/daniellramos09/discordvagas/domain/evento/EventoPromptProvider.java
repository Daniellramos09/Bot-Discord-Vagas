package com.github.daniellramos09.discordvagas.domain.evento;

import com.github.daniellramos09.discordvagas.core.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class EventoPromptProvider implements PromptProvider {

    @Override
    public String buildPrompt(String titulo) {
        return String.format(
                "Você é um curador especialista em eventos de tecnologia para a comunidade DiscordVagas.\n" +
                        "Analise rigorosamente este evento/hackathon:\n" +
                        "Título: '%s'\n\n" +
                        "REGRAS DE REJEIÇÃO (responda APENAS com a palavra: IGNORAR):\n" +
                        "1. O evento JÁ ACONTECEU (resultados, vencedores, cobertura retrospectiva).\n" +
                        "2. As INSCRIÇÕES JÁ ENCERRARAM ou o prazo expirou.\n" +
                        "3. O evento é PRESENCIAL e a cidade/estado NÃO é São Paulo (capital ou Grande SP).\n\n" +
                        "REGRAS DE APROVAÇÃO:\n" +
                        "- Eventos ONLINE/EAD/VIRTUAIS devem ser APROVADOS, independentemente do estado.\n" +
                        "- Eventos presenciais em São Paulo / Grande SP com inscrições abertas devem ser APROVADOS.\n" +
                        "- Hackathons, maratonas de programação e meetups tech com inscrição aberta são bem-vindos.\n\n" +
                        "Se aprovado, escreva um texto curto e animado (no máximo 2 linhas) em português brasileiro, " +
                        "convidando a comunidade a participar. Informe se é ONLINE ou PRESENCIAL (SP) e o tema principal.",
                titulo
        );
    }
}
