package com.posapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.posapi.domain",
    "com.posapi.application",
    "com.posapi.infrastructure"
})
public class PosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosApiApplication.class, args);
    }

}
