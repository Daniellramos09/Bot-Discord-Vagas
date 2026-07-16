package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vaga")
public class Vaga extends Conteudo {

    @Column(columnDefinition = "TEXT")
    private String descricaoBruta;

    public Vaga() {
    }

    public Vaga(String titulo, String url, String descricaoBruta) {
        setTitulo(titulo);
        setUrl(url);
        this.descricaoBruta = descricaoBruta;
    }

    public String getDescricaoBruta() {
        return descricaoBruta;
    }
}
