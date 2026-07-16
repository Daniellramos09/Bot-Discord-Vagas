package com.github.daniellramos09.discordvagas.repository;

import com.github.daniellramos09.discordvagas.entity.OpenSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenSourceRepository extends JpaRepository<OpenSource, Long> {
    // O bot vai usar isso para perguntar: "Já tenho essa issue no banco?"
    boolean existsByGithubId(Long githubId);

    List<OpenSource> findByEnviadoFalse();
}
