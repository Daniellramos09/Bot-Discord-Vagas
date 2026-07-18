package com.github.daniellramos09.discordvagas.domain.ferramentas;

public record FerramentaRecord(String owner, String name, String url,
                                String descricao, String linguagem,
                                long stars, long forks) {
}
