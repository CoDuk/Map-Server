package com.coduk.duksungmap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DuksungMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuksungMapApplication.class, args);
    }

}
