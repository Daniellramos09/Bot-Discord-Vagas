package com.github.daniellramos09.discordvagas.domain.evento;

import com.github.daniellramos09.discordvagas.entity.Conteudo;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "evento_tecnologia")
public class Evento extends Conteudo {
}
