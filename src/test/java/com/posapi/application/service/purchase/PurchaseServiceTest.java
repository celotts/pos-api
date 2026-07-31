package com.posapi.application.service.purchase;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.purchase.Purchase;
import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.domain.model.user.User;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.port.output.*;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest;
import com.posapi.infrastructure.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private PurchaseItemRepository purchaseItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private PurchaseService purchaseService;

    private User currentUser;
    private Product product;
    private PurchaseRequest purchaseRequest;
    private PurchaseItemRequest purchaseItemRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        currentUser = User.builder()
                .id(userId)
                .role(Role.builder().id(roleId).build())
                .build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .sku("TP-001")
                .currentStock(BigDecimal.TEN)
                .build();

        purchaseItemRequest = new PurchaseItemRequest(productId, BigDecimal.valueOf(5), BigDecimal.valueOf(20));

        purchaseRequest = new PurchaseRequest(UUID.randomUUID(), Instant.now(), List.of(purchaseItemRequest));
    }

    @Test
    void whenCreatePurchase_thenStockShouldIncreaseAndInventoryTransactionShouldBeCreated() {
        // Arrange
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(currentUser);
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(purchaseItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userRepository.findAllById(anySet())).thenReturn(Collections.emptyList()); // Simplificar el mock del mapper

        // Act
        purchaseService.createPurchase(purchaseRequest, currentUser.getId());

        // Assert
        // 1. Verificar que el producto se guarda con el stock incrementado
        verify(productRepository, times(1)).save(argThat(p ->
                p.getCurrentStock().equals(BigDecimal.valueOf(15)) // 10 + 5
        ));

        // 2. Verificar que se guarda la transacción de inventario
        verify(inventoryTransactionRepository, times(1)).saveAll(anyList());

        // 3. Verificar que se guarda la compra y sus ítems
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
        verify(purchaseItemRepository, times(1)).saveAll(anyList());
    }
}
