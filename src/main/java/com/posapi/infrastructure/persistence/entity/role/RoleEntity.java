package com.posapi.infrastructure.persistence.entity.role;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class RoleEntity {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    @ToString.Include
    private String name;
}