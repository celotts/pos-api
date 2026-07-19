package com.posapi.domain.port.output;

import com.posapi.domain.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findAllByFullName(String fullName);
    User save(User user);
    boolean existsByEmail(String email);
    boolean existsByFullName(String fullName);
    boolean existsByRoleName(String roleName);

    // NUEVOS MÉTODOS REQUERIDOS POR OTROS COMPONENTES:
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    List<User> findAllById(Collection<UUID> ids);

    List<User> findAllById(Iterable<UUID> ids);
}
