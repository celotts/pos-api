package com.posapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// 🟢 Escaneamos todas las entidades del dominio e infraestructura de forma amplia
@EntityScan(basePackages = {
        "com.posapi.domain.model",
        "com.posapi.infrastructure"
})
// 🔍 AQUÍ EL CAMBIO: Ampliamos el barrido de repositorios para atrapar cualquier subpaquete de persistencia
@EnableJpaRepositories(basePackages = {
        "com.posapi.domain.repository",
        "com.posapi.infrastructure" // Esto incluye .repository y .adapter.output.persistence...
})
public class PosApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PosApiApplication.class, args);
    }
}