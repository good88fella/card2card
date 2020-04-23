package ru.sberbank.card2card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("ru.sberbank.card2card.repository")
@EntityScan("ru.sberbank.card2card.model")
@SpringBootApplication
public class Card2cardApplication {

    public static void main(String[] args) {
        SpringApplication.run(Card2cardApplication.class, args);
    }

}
