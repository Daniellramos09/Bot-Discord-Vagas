package com.github.daniellramos09.discordvagas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DiscordVagasApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscordVagasApplication.class, args);
    }

}
