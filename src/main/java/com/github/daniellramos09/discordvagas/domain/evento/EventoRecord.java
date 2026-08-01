package com.github.daniellramos09.discordvagas.domain.evento;

/**
 * DTO bruto retornado pelo SerperEventoScraper (antes da curadoria da IA).
 */
public record EventoRecord(String titulo, String url) {
}
