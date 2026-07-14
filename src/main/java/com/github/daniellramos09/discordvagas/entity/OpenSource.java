package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "open_source_contribuicao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "github_id", unique = true, nullable = false)
    private Long githubId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(nullable = false)
    private String repositorio;

    private String linguagem;

    @Column(name = "resumo_ai", columnDefinition = "TEXT")
    private String resumoAi;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(nullable = false)
    @Builder.Default
    private boolean enviado = false;
}