package com.github.daniellramos09.discordvagas.domain.vaga;

import org.springframework.stereotype.Component;

@Component
public class VagaPromptProvider {

    public String buildPrompt(String descricaoBruta) {
        return String.format(
                "Analise a descrição desta vaga de TI e gere um resumo altamente organizado para o Discord. " +
                        "Use estritamente o formato abaixo, mantendo as quebras de linha para o texto não ficar amontoado:\n\n" +
                        "º Região: [Liste as cidades/regiões principais. Se houver São Paulo/SP, destaque-a]\n" +
                        "º Cargo: [Título do cargo ou programa]\n" +
                        "º O que fará: [Resumo conciso das atividades]\n" +
                        "º Resumo sobre a empresa: [Breve descrição sobre quem é a empresa]\n" +
                        "º Missão, Visão e Valores: [Descreva brevemente a cultura ou propósito da empresa com base no texto]\n\n" +
                        "Seja direto e conciso. Descrição da vaga:\n%s",
                descricaoBruta
        );
    }
}
