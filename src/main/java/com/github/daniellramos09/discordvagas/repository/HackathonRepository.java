package com.github.daniellramos09.discordvagas.repository;

import com.github.daniellramos09.discordvagas.entity.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    // Método crucial para evitar spam: verifica se o hackathon já foi postado
    boolean existsByUrl(String url);
}