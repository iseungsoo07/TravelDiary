package com.project.traveldiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TravelDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelDiaryApplication.class, args);
    }

}
