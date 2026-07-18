package com.github.daniellramos09.discordvagas.repository;

import com.github.daniellramos09.discordvagas.entity.FerramentaOpenSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FerramentaOpenSourceRepository extends JpaRepository<FerramentaOpenSource, Long> {
    boolean existsByUrl(String url);
}
