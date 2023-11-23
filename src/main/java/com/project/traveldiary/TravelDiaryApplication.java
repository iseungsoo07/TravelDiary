package com.project.traveldiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    exclude = {
        org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
        org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
    }
)
public class TravelDiaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelDiaryApplication.class, args);
    }

}
