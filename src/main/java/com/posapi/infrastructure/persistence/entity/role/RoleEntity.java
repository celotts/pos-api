package com.posapi.infrastructure.persistence.entity.role;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;
}