package com.posapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.posapi.infrastructure.adapter.output.persistence.entity")
@EnableJpaRepositories(basePackages = "com.posapi.infrastructure.adapter.output.persistence.repository")
public class PosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosApiApplication.class, args);
    }
}
