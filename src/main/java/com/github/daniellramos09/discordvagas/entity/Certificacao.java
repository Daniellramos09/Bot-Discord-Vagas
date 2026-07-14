package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    // A URL é o nosso escudo contra duplicatas
    @Column(unique = true, nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private String fonte;

    @Column(name = "resumo_ai", columnDefinition = "TEXT")
    private String resumoAi;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(nullable = false)
    @Builder.Default
    private boolean enviado = false;
}