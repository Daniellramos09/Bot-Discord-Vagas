package com.github.daniellramos09.discordvagas.domain.evento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    boolean existsByUrl(String url);
}
