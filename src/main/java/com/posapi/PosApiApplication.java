package com.posapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal de POS API.
 * Punto de entrada para la aplicación Spring Boot.
 */
@SpringBootApplication
public class PosApiApplication {

    /**
     * Método principal de la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(PosApiApplication.class, args);
    }
}