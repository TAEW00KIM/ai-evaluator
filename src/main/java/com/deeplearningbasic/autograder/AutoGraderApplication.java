package com.deeplearningbasic.autograder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AutoGraderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoGraderApplication.class, args);
    }

}
