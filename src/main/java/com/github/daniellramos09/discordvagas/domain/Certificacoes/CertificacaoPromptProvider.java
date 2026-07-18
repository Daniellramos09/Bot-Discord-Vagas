package com.github.daniellramos09.discordvagas.domain.Certificacoes;

import org.springframework.stereotype.Component;

@Component
public class CertificacaoPromptProvider {

    public String buildPrompt(String titulo, String descricao, String publicoAlvo) {
        return String.format(
                "Você é um especialista em carreiras de tecnologia ajudando estudantes e profissionais do Brasil a conquistarem certificações valiosas no mercado.\n\n" +
                        "Analise esta certificação e escreva um resumo motivador de no máximo 4 linhas em português brasileiro, destacando:\n" +
                        "- Do que se trata a certificação e por que ela é relevante no mercado atual\n" +
                        "- Para quem é mais importante (estudantes, juniores, plenos, carreira específica)\n" +
                        "- Uma dica prática de como começar a estudar para ela\n\n" +
                        "Seja empolgado e encorajador, mas mantenha o foco técnico.\n\n" +
                        "Certificação: %s\n" +
                        "Descrição: %s\n" +
                        "Público-alvo: %s",
                titulo, descricao, publicoAlvo
        );
    }
}
