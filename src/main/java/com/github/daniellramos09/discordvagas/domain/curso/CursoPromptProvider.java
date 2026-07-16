package com.github.daniellramos09.discordvagas.domain.curso;

import org.springframework.stereotype.Component;

@Component
public class CursoPromptProvider {

    public String buildPrompt(String titulo) {
        return String.format(
                "Você é um curador especialista em educação de tecnologia para o estado de São Paulo. " +
                        "Avalie rigorosamente se o título desta notícia descreve um curso adequado para o nosso público:\n" +
                        "Notícia: '%s'\n\n" +
                        "REGRAS DE FILTRO:\n" +
                        "1. O curso DEVE ser nas áreas de tecnologia, programação, ciência de dados, inteligência artificial, segurança da informação (cybersecurity) ou infraestrutura/cloud/nuvem como AWS, Azure, Oracle e dentre outros.\n" +
                        "2. O curso pode ser ONLINE/EAD ou PRESENCIAL (em São Paulo ou Grande SP).\n" +
                        "3. O público-alvo DEVE ser: alunos de Ensino Médio, ensino Técnico, Graduação/Faculdade ou pessoas já na área buscando especialização (Juniores, Plenos, ou pessoas em transição de carreira).\n" +
                        "4. Se o curso for voltado para CRIANÇAS/PÚBLICO INFANTIL (ex: robótica infantil, Scratch para crianças, desenvolvimento de games para menores de 12 anos, escola de programação infantil), responda APENAS com a palavra: IGNORAR.\n" +
                        "5. Se o título indicar claramente que as inscrições já fecharam ou se o assunto principal não for tecnologia, responda APENAS com a palavra: IGNORAR ou EXPIRADO.\n\n" +
                        "Caso passe em todas as regras, faça um resumo altamente motivador de exatamente 2 linhas em português brasileiro, destacando:\n" +
                        "- O que vão aprender (foco técnico)\n" +
                        "- O formato (Se é Online ou Presencial em SP)\n" +
                        "- Indique para quem é recomendado (ex: 'Ideal para quem busca a primeira vaga' ou 'Ótimo para juniores/plenos').",
                titulo
        );
    }
}
