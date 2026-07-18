package com.github.daniellramos09.discordvagas.domain.ferramentas;

import org.springframework.stereotype.Component;

@Component
public class FerramentaPromptProvider {

    public String buildPrompt(String nomeRepo, String descricao, String linguagem,
                              long stars, long forks) {
        return String.format(
                "Você é um desenvolvedor sênior apaixonado por open source ajudando estudantes e desenvolvedores a descobrirem ferramentas incríveis.\n\n" +
                        "Analise este repositório e escreva um resumo empolgante de no máximo 4 linhas em português brasileiro, destacando:\n" +
                        "- Do que se trata a ferramenta e qual problema ela resolve\n" +
                        "- Por que ela é útil para estudantes e desenvolvedores\n" +
                        "- Uma dica de como começar a usar\n\n" +
                        "Seja entusiasmado, mas mantenha o foco na utilidade prática.\n\n" +
                        "Repositório: %s\n" +
                        "Descrição original: %s\n" +
                        "Linguagem: %s\n" +
                        "Stars: %d | Forks: %d",
                nomeRepo, descricao, linguagem, stars, forks
        );
    }
}
