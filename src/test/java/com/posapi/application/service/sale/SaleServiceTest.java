package com.posapi.application.service.sale;

import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.sale.Sale;
import com.posapi.domain.model.sale.SaleItem;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CustomerRepository;
import com.posapi.domain.port.output.InventoryTransactionRepository;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.SaleItemRepository;
import com.posapi.domain.port.output.SaleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.adapter.input.rest.sale.mapper.SaleRestMapper;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
import com.posapi.infrastructure.adapter.input.rest.saleItem.mapper.SaleItemRestMapper;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @Mock
    private SaleRestMapper saleRestMapper;
    @Mock
    private SaleItemRestMapper saleItemRestMapper;

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

        currentUser = User.builder().id(userId).role(Role.builder().id(roleId).build()).build();

        product = Product.builder()
                .id(productId)
                .name("Test Product")
                .currentStock(BigDecimal.TEN)
                .build();

        SaleItemRequest sufficientItemRequest = new SaleItemRequest(
                null, productId, BigDecimal.valueOf(5), BigDecimal.valueOf(100));
        saleRequestWithSufficientStock = new SaleRequest();
        saleRequestWithSufficientStock.setItems(List.of(sufficientItemRequest));

        SaleItemRequest insufficientItemRequest = new SaleItemRequest(
                null, productId, BigDecimal.valueOf(15), BigDecimal.valueOf(100));
        saleRequestWithInsufficientStock = new SaleRequest();
        saleRequestWithInsufficientStock.setItems(List.of(insufficientItemRequest));

        // Common stubbing moved to setUp
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(currentUser);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(userRepository.findAllById(Set.of(currentUser.getId()))).thenReturn(List.of(currentUser));
        when(saleRestMapper.toResponse(any(Sale.class))).thenReturn(SaleResponse.builder().build());
        when(saleItemRestMapper.toResponseList(anyList(), anyMap(), anyMap())).thenReturn(List.of(SaleItemResponse.builder().build()));
    }

    @Test
    void createSaleWithInsufficientStockThrowsExceptionAndDoesNotPersist() {
        // No specific stubbing needed here, common stubs are in setUp

        assertThrows(IllegalArgumentException.class, () -> saleService.createSale(saleRequestWithInsufficientStock));

        verify(saleRepository, never()).save(any(Sale.class));
        verify(saleItemRepository, never()).save(any());
        verify(inventoryTransactionRepository, never()).save(any());
    }

    @Test
    void createSaleWithSufficientStockDecreasesStockAndCreatesTransaction() {
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(saleItemRepository.save(any(SaleItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        saleService.createSale(saleRequestWithSufficientStock);

        verify(productRepository, times(1)).save(argThat((Product p) ->
                p.getCurrentStock().equals(BigDecimal.valueOf(5))));
        verify(inventoryTransactionRepository, times(1)).save(any());
        verify(saleRepository, times(2)).save(any(Sale.class));
        verify(saleItemRepository, times(1)).save(any(SaleItem.class));
    }
}
