package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "open_source_contribuicao")
public class OpenSource extends Conteudo {

    @Column(name = "github_id", unique = true, nullable = false)
    private Long githubId;

    @Column(nullable = false)
    private String repositorio;

    private String linguagem;

    public OpenSource() {
    }

    public Long getGithubId() { return githubId; }
    public void setGithubId(Long githubId) { this.githubId = githubId; }

    public String getRepositorio() { return repositorio; }
    public void setRepositorio(String repositorio) { this.repositorio = repositorio; }

    public String getLinguagem() { return linguagem; }
    public void setLinguagem(String linguagem) { this.linguagem = linguagem; }
}
