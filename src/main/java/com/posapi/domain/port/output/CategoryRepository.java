package com.posapi.domain.port.output;

import com.posapi.domain.model.category.Category;
import org.springframework.data.domain.Page; // Añadir importación para Page
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Page<Category> findAll(Pageable pageable); // Cambiado de List a Page
    void deleteById(UUID id);
    boolean existsByName(String name);

    Optional<Category> findByName(String name); // Corregido el tipo de retorno
}
