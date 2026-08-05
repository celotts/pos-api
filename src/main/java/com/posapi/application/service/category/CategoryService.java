package com.posapi.application.service.category;

import com.posapi.application.port.category.CategoryManagementPort;
import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.category.Category;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CategoryRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryRequest;
import com.posapi.infrastructure.adapter.input.rest.category.dto.CategoryResponse;
import com.posapi.infrastructure.adapter.input.rest.category.mapper.CategoryRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService implements CategoryManagementPort {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;
    private final CategoryRestMapper categoryRestMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, UUID currentUserId) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category with name '" + request.getName() + "' already exists.");
        }

        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Category newCategory = Category.createNew(request.getName(), currentUserId, currentUserRoleId);

        Category savedCategory = categoryRepository.save(newCategory);
        return categoryRestMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryById(UUID id) {
        return categoryRepository.findById(id).map(categoryRestMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        List<CategoryResponse> categoryResponses = categoriesPage.getContent().stream()
                .map(categoryRestMapper::toResponse)
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
                    if (request.getName() != null && !request.getName().equals(existingCategory.getName())) {
                        if (categoryRepository.existsByName(request.getName())) {
                            throw new DuplicateResourceException(
                                    "Category with name '" + request.getName() + "' already exists.");
                        }
                        existingCategory.setName(request.getName());
                    }
                    
                    User currentUser = securityContextHelper.getCurrentUserOrThrow();
                    existingCategory.updateName(request.getName(), currentUserId, currentUser.getRole().getId());

                    Category updatedCategory = categoryRepository.save(existingCategory);
                    return categoryRestMapper.toResponse(updatedCategory);
                });
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id, UUID currentUserId) {
        categoryRepository.findById(id)
                .ifPresent(existingCategory -> {
                    User currentUser = securityContextHelper.getCurrentUserOrThrow();
                    existingCategory.markAsDeleted(currentUserId, currentUser.getRole().getId());
                    categoryRepository.save(existingCategory);
                    log.info("Category with id {} marked as deleted by user {}", id, currentUserId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getCategoryByName(String name) {
        return categoryRepository.findByName(name).map(categoryRestMapper::toResponse);
    }
}
