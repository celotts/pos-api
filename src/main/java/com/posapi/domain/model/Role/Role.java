package com.posapi.domain.model.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter; // Añadido para getters/setters
import lombok.Setter; // Añadido para getters/setters
import lombok.NoArgsConstructor; // Añadido para constructor sin argumentos
import lombok.AllArgsConstructor; // Añadido para constructor con todos los argumentos
import lombok.Builder; // Añadido para el patrón Builder

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
