package com.github.daniellramos09.discordvagas.repository;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {

    boolean existsByUrl(String url);


}
