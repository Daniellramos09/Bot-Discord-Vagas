package com.github.daniellramos09.discordvagas.core;

/**
 * Contrato compartilhado entre os módulos do Modular Monolith
 * para construção de prompts enviados ao GeminiClient.
 */
public interface PromptProvider {

    String buildPrompt(String titulo);
}
