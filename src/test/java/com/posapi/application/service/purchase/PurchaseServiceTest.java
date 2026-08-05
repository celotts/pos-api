package com.posapi.application.service.purchase;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.purchase.Purchase;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.InventoryTransactionRepository;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.PurchaseItemRepository;
import com.posapi.domain.port.output.PurchaseRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import com.posapi.infrastructure.adapter.input.rest.purchase.mapper.PurchaseRestMapper;
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
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock // Added mock for PurchaseRestMapper
    private PurchaseRestMapper purchaseRestMapper;

    @InjectMocks
    private PurchaseService purchaseService;

    private User currentUser;
    private Product product;
    private PurchaseRequest purchaseRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        currentUser = User.builder().id(userId).role(Role.builder().id(roleId).build()).build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .sku("TP-001")
                .currentStock(BigDecimal.TEN)
                .build();

        PurchaseItemRequest purchaseItemRequest = new PurchaseItemRequest(
                null, productId, BigDecimal.valueOf(5), BigDecimal.valueOf(20));

        purchaseRequest = new PurchaseRequest(UUID.randomUUID(), Instant.now(), List.of(purchaseItemRequest));
    }

    @Test
    void createPurchaseIncreasesStockAndCreatesInventoryTransaction() {
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(currentUser);
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(purchaseItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userRepository.findAllById(Set.of(currentUser.getId()))).thenReturn(List.of(currentUser));
        // Stub the purchaseRestMapper.toResponse method
        when(purchaseRestMapper.toResponse(any(Purchase.class))).thenReturn(PurchaseResponse.builder().build());


        purchaseService.createPurchase(purchaseRequest, currentUser.getId());

        verify(productRepository, times(1)).save(argThat((Product p) ->
                p.getCurrentStock().equals(BigDecimal.valueOf(15))));
        verify(inventoryTransactionRepository, times(1)).saveAll(anyList());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
        verify(purchaseItemRepository, times(1)).saveAll(anyList());
    }
}
