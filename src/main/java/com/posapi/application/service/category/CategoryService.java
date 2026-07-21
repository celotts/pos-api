package com.posapi.application.service.category;

import com.posapi.application.port.category.CategoryManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.category.Category;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CategoryRepository;
import com.posapi.domain.port.output.UserRepository; // Necesario para obtener nombres de usuario
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;
import com.posapi.infrastructure.adapter.output.persistence.mapper.category.CategoryPersistenceMapper; // Mapper para entidad/dominio
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements CategoryManagementPort {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository; // Para obtener nombres de usuario
    private final SecurityContextHelper securityContextHelper;
    private final CategoryPersistenceMapper categoryPersistenceMapper; // Inyectar el mapper

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, UUID currentUserId) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category with name '" + request.name() + "' already exists.");
        }

        // Obtener el rol del usuario actual (asumiendo que User tiene getRoleId())
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId(); // CORREGIDO: Acceder al ID del rol a través de la relación

        Category newCategory = Category.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .createdAt(Instant.now())
                .createdByUserId(currentUserId)
                .createdByRoleId(currentUserRoleId) // Asignar el rol del creador
                .build();

        Category savedCategory = categoryRepository.save(newCategory);
        return mapToCategoryResponse(savedCategory); // Mapear a DTO de respuesta
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(this::mapToCategoryResponse); // Mapear a DTO de respuesta
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable); // Eliminado el cast innecesario
        List<CategoryResponse> categoryResponses = categoriesPage.getContent().stream()
                .map(this::mapToCategoryResponse) // Mapear cada entidad a DTO de respuesta
                .collect(Collectors.toList());

        return new PageResponse<>(
                categoryResponses,
                categoriesPage.getNumber(),
                categoriesPage.getSize(),
                categoriesPage.getTotalElements(),
                categoriesPage.getTotalPages(),
                categoriesPage.isLast()
        );
    }

    @Override
    @Transactional
    public Optional<CategoryResponse> updateCategory(UUID id, CategoryRequest request, UUID currentUserId) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    if (request.name() != null && !request.name().equals(existingCategory.getName())) {
                        if (categoryRepository.existsByName(request.name())) {
                            throw new DuplicateResourceException(
                                    "Category with name '" + request.name() + "' already exists.");
                        }
                        existingCategory.setName(request.name());
                    }
                    existingCategory.setUpdatedAt(Instant.now());
                    existingCategory.setUpdatedByUserId(currentUserId);

                    // Obtener el rol del usuario actual
                    User currentUser = securityContextHelper.getCurrentUserOrThrow();
                    existingCategory.setUpdatedByRoleId(currentUser.getRole().getId()); // CORREGIDO: Acceder al ID del rol a través de la relación

                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return mapToCategoryResponse(updatedCategory); // Mapear a DTO de respuesta
                });
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id, UUID currentUserId) {
        categoryRepository.findById(id)
                .ifPresent(existingCategory -> {
                    existingCategory.setDeletedAt(Instant.now());
                    existingCategory.setDeletedByUserId(currentUserId);

                    // Obtener el rol del usuario actual
                    User currentUser = securityContextHelper.getCurrentUserOrThrow();
                    existingCategory.setDeletedByRoleId(currentUser.getRole().getId()); // CORREGIDO: Acceder al ID del rol a través de la relación

                    categoryRepository.save(existingCategory);
                    log.info("Category with id {} marked as deleted by user {}", id, currentUserId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(this::mapToCategoryResponse); // Mapear a DTO de respuesta
    }

    // Método auxiliar para mapear Category a CategoryResponse y obtener nombres de usuario
    private CategoryResponse mapToCategoryResponse(Category category) {
        Set<UUID> userIds = Stream.of(
                category.getCreatedByUserId(),
                category.getUpdatedByUserId(),
                category.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.get(category.getCreatedByUserId());
        String updatedByName = userNames.get(category.getUpdatedByUserId());
        String deletedByName = userNames.get(category.getDeletedByUserId());

        // Asumiendo que CategoryResponse.fromDomain ahora acepta Category y los 3 nombres
        return CategoryResponse.fromDomain(category, createdByName, updatedByName, deletedByName);
    }

    // Método auxiliar para obtener nombres de usuario
    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
