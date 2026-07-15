package com.github.daniellramos09.discordvagas.repository;

import com.github.daniellramos09.discordvagas.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    boolean existsByUrl(String url);
}