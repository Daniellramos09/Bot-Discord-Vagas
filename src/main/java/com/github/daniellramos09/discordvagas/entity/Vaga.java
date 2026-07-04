package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaga")
public class Vaga{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titulo;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String descricaoBruta;

    @Column(columnDefinition = "TEXT")
    private String resumoAi;

    @Column(nullable = false)
    private LocalDateTime dataDescoberta;

    public Vaga() {
    }

    public Vaga(String titulo, String url, String descricaoBruta) {
        this.titulo = titulo;
        this.url = url;
        this.descricaoBruta = descricaoBruta;
        this.dataDescoberta = LocalDateTime.now();
    }



    public String getTitulo() {
        return titulo;
    }



    public String getUrl() {
        return url;
    }


    public String getDescricaoBruta() {
        return descricaoBruta;
    }



    public String getResumoAi() {
        return resumoAi;
    }

    public void setResumoAi(String resumoAi) {
        this.resumoAi = resumoAi;
    }



}
