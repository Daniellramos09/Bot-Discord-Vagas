package com.github.daniellramos09.discordvagas.domain.hackathon;

import org.springframework.stereotype.Component;

@Component
public class HackathonPromptProvider {

    public String buildPrompt(String titulo) {
        return String.format(
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
    }
}
