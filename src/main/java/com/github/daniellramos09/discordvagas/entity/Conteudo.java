package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "resumo_ai", columnDefinition = "TEXT")
    private String resumoAi;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(nullable = false)
    private boolean enviado = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getResumoAi() { return resumoAi; }
    public void setResumoAi(String resumoAi) { this.resumoAi = resumoAi; }

    public LocalDateTime getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(LocalDateTime dataPublicacao) { this.dataPublicacao = dataPublicacao; }

    public boolean isEnviado() { return enviado; }
    public void setEnviado(boolean enviado) { this.enviado = enviado; }
}
