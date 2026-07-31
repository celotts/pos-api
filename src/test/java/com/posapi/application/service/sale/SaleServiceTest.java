package com.posapi.application.service.sale;

import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.sale.Sale;
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
    private Product productWithStock;
    private SaleRequest saleRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        currentUser = User.builder()
                .id(userId)
                .role(Role.builder().id(roleId).build())
                .build();

        productWithStock = Product.builder()
                .id(productId)
                .name("Test Product")
                .currentStock(BigDecimal.TEN) // 10 en stock
                .build();

        // Solicitud para vender 15 unidades, cuando solo hay 10
        SaleItemRequest itemRequest = new SaleItemRequest(productId, BigDecimal.valueOf(15), BigDecimal.valueOf(100));

        saleRequest = new SaleRequest();
        saleRequest.setItems(List.of(itemRequest));
    }

    @Test
    void whenCreateSale_withInsufficientStock_thenThrowExceptionAndDoNotPersist() {
        // Arrange
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(currentUser);
        when(productRepository.findById(productWithStock.getId())).thenReturn(Optional.of(productWithStock));

        // ** LA CORRECCIÓN CLAVE ESTÁ AQUÍ **
        // Cuando el servicio intente guardar la venta por primera vez,
        // devolvemos el mismo objeto para que no sea nulo.
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> {
            Sale saleToSave = invocation.getArgument(0);
            saleToSave.setId(UUID.randomUUID()); // Asignamos un ID como lo haría la BD real
            return saleToSave;
        });

        // Act & Assert
        // Verificar que se lanza la excepción de negocio esperada (IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> {
            saleService.createSale(saleRequest);
        });

        // Verificar que, aunque se intentó guardar la VENTA inicial,
        // NUNCA se guardaron los ITEMS ni las TRANSACCIONES DE INVENTARIO.
        verify(saleRepository, times(1)).save(any(Sale.class)); // Se llama una vez antes de la validación de stock
        verify(saleItemRepository, never()).save(any());
        verify(inventoryTransactionRepository, never()).save(any());
    }
}
