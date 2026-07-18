package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ferramenta_open_source")
public class FerramentaOpenSource extends Conteudo {

    @Column(nullable = false)
    private String repositorio;

    private String linguagem;

    private Long stars;

    private Long forks;

    public FerramentaOpenSource() {
    }

    public String getRepositorio() { return repositorio; }
    public void setRepositorio(String repositorio) { this.repositorio = repositorio; }

    public String getLinguagem() { return linguagem; }
    public void setLinguagem(String linguagem) { this.linguagem = linguagem; }

    public Long getStars() { return stars; }
    public void setStars(Long stars) { this.stars = stars; }

    public Long getForks() { return forks; }
    public void setForks(Long forks) { this.forks = forks; }
}
