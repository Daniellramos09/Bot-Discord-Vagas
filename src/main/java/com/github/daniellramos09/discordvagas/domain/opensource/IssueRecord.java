package com.github.daniellramos09.discordvagas.domain.opensource;

public record IssueRecord(Long githubId, String titulo, String url,
                          String repositorioUrl, String descricaoOriginal) {
}
