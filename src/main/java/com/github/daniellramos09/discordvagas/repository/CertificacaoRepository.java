package com.github.daniellramos09.discordvagas.repository;

import com.github.daniellramos09.discordvagas.entity.Certificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificacaoRepository extends JpaRepository<Certificacao, Long> {
    boolean existsByUrl(String url);
}
