package com.github.daniellramos09.discordvagas.domain.opensource;

import org.springframework.stereotype.Component;

@Component
public class OpenSourcePromptProvider {

    public String buildPrompt(String titulo, String descricaoBruta) {
        return String.format(
                "Você é um desenvolvedor sênior ajudando profissionais juniores. Leia o título e a descrição desta issue " +
                        "do GitHub (marcada como 'good first issue') e faça um resumo direto, encorajador e conciso de no máximo 3 linhas " +
                        "em português, explicando claramente qual é o problema e o que precisa ser feito no código para resolvê-lo.\n\n" +
                        "Título da Issue: %s\n" +
                        "Descrição da Issue:\n%s",
                titulo, descricaoBruta
        );
    }
}
