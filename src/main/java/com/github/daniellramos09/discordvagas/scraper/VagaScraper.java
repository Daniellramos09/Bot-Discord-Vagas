package com.github.daniellramos09.discordvagas.scraper;

import com.github.daniellramos09.discordvagas.entity.Vaga;
import java.util.List;

public interface VagaScraper {

    List<Vaga> buscarVagas();

    default String getNomeFonte() {
        return this.getClass().getSimpleName();
    }
}
