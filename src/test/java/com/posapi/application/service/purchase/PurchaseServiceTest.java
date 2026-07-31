package com.posapi.application.service.purchase;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.purchase.Purchase;
import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.domain.port.output.InventoryTransactionRepository;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.PurchaseRepository;
import com.posapi.infrastructure.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
    private ProductRepository productRepository;
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private PurchaseService purchaseService;

    private Product product;
    private Purchase purchase;
    private PurchaseItem purchaseItem;

    @BeforeEach
    void setUp() {
        UUID productId = UUID.randomUUID();
        product = Product.builder()
                .id(productId)
                .currentStock(BigDecimal.TEN)
                .build();

        purchaseItem = PurchaseItem.builder()
                .productId(productId)
                .quantity(BigDecimal.valueOf(5))
                .build();

        purchase = Purchase.builder()
                .id(UUID.randomUUID())
                .items(List.of(purchaseItem))
                .build();
    }

    @Test
    void whenCreatePurchase_thenStockShouldIncreaseAndInventoryTransactionShouldBeCreated() {
        // Arrange
        when(productRepository.findById(purchaseItem.getProductId())).thenReturn(Optional.of(product));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

        // Act
        purchaseService.createPurchase(purchase);

        // Assert
        // Verificar que el stock del producto se incrementó
        verify(productRepository, times(1)).save(argThat(p ->
                p.getCurrentStock().equals(BigDecimal.valueOf(15))
        ));

        // Verificar que se creó una transacción de inventario
        verify(inventoryTransactionRepository, times(1)).save(any());
    }
}
