package com.github.daniellramos09.discordvagas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "curso_tecnologia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(unique = true, nullable = false, length = 800)
    private String url;

    @Column(name = "resumo_ai", columnDefinition = "TEXT")
    private String resumoAi;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(nullable = false)
    @Builder.Default
    private boolean enviado = false;
}