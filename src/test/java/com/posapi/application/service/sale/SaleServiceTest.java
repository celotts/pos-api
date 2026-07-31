package com.posapi.application.service.sale;

import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.sale.Sale;
import com.posapi.domain.model.sale.SaleItem;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.*;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SaleItemRepository saleItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InventoryTransactionRepository inventoryTransactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private SaleService saleService;

    private User currentUser;
    private Product product;
    private SaleRequest saleRequestWithSufficientStock;
    private SaleRequest saleRequestWithInsufficientStock;

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
                .currentStock(BigDecimal.TEN) // 10 en stock
                .build();

        // Request para un "happy path" (vender 5)
        SaleItemRequest sufficientItemRequest = new SaleItemRequest(productId, BigDecimal.valueOf(5), BigDecimal.valueOf(100));
        saleRequestWithSufficientStock = new SaleRequest();
        saleRequestWithSufficientStock.setItems(List.of(sufficientItemRequest));

        // Request para un caso de error (vender 15)
        SaleItemRequest insufficientItemRequest = new SaleItemRequest(productId, BigDecimal.valueOf(15), BigDecimal.valueOf(100));
        saleRequestWithInsufficientStock = new SaleRequest();
        saleRequestWithInsufficientStock.setItems(List.of(insufficientItemRequest));
    }

    @Test
    void whenCreateSale_withInsufficientStock_thenThrowExceptionAndDoNotPersist() {
        // Arrange
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(currentUser);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale saleToSave = invocation.getArgument(0);
            saleToSave.setId(UUID.randomUUID());
            return saleToSave;
        });

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(saleRequestWithInsufficientStock);
        });

        verify(saleRepository, times(1)).save(any(Sale.class));
        verify(saleItemRepository, never()).save(any());
        verify(inventoryTransactionRepository, never()).save(any());
    }

    @Test
    void whenCreateSale_withSufficientStock_thenStockShouldDecreaseAndTransactionShouldBeCreated() {
        // Arrange
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(currentUser);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(saleItemRepository.save(any(SaleItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        // Act
        saleService.createSale(saleRequestWithSufficientStock);

        // Assert
        // 1. Verificar que el producto se guarda con el stock disminuido
        verify(productRepository, times(1)).save(argThat(p ->
                p.getCurrentStock().equals(BigDecimal.valueOf(5)) // 10 - 5
        ));

        // 2. Verificar que se guarda la transacción de inventario
        verify(inventoryTransactionRepository, times(1)).save(any());

        // 3. Verificar que se guarda la venta y sus ítems
        verify(saleRepository, times(2)).save(any(Sale.class)); // Una para la creación inicial, otra para actualizar totales
        verify(saleItemRepository, times(1)).save(any(SaleItem.class));
    }
}
