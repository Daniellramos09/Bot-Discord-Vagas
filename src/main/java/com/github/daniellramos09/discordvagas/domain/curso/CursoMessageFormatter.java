package com.github.daniellramos09.discordvagas.domain.curso;

import org.springframework.stereotype.Component;

@Component
public class CursoMessageFormatter {

    private static final String WEBHOOK_USERNAME = "Cursos & Bootcamps";

    public String format(String titulo, String resumoAi, String url) {
        return String.format(
                "🚀 **Novo Bootcamp / Curso Gratuito na área!**\n\n" +
                        "**Notícia:** %s\n\n" +
                        "🤖 **O que a IA achou:**\n%s\n\n" +
                        "🔗 **Inscreva-se aqui:** %s",
                titulo, resumoAi, url
        );
    }

    public String getUsername() {
        return WEBHOOK_USERNAME;
    }
}
