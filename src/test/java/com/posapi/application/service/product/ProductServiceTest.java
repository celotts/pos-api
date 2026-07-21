package com.posapi.application.service.product;

import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductRequest;
import com.posapi.infrastructure.adapter.input.rest.product.dto.ProductResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private ProductService productService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ADMIN_ROLE_ID = UUID.randomUUID();

    private User adminUser;
    private Product product1;
    private Product product2;
    private ProductRequest productRequest1;

    @BeforeEach
    void setUp() {
        Role adminRole = Role.builder()
                .id(ADMIN_ROLE_ID)
                .name("ADMIN")
                .build();

        adminUser = User.builder()
                .id(USER_ID)
                .email("admin@example.com")
                .fullName("Admin User")
                .role(adminRole)
                .createdByRoleId(ADMIN_ROLE_ID)
                .build();

        product1 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU001")
                .name("Product 1")
                .purchasePrice(BigDecimal.valueOf(50))
                .salePrice(BigDecimal.valueOf(100))
                .currentStock(BigDecimal.valueOf(10))
                .createdByUserId(USER_ID)
                .createdByUserRoleId(ADMIN_ROLE_ID)
                .createdAt(Instant.now())
                .build();

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU002")
                .name("Product 2")
                .purchasePrice(BigDecimal.valueOf(75))
                .salePrice(BigDecimal.valueOf(200))
                .currentStock(BigDecimal.valueOf(5))
                .createdByUserId(USER_ID)
                .createdByUserRoleId(ADMIN_ROLE_ID)
                .createdAt(Instant.now())
                .build();

        productRequest1 = new ProductRequest(
                product1.getSku(), product1.getName(), product1.getDescription(),
                product1.getPurchasePrice(), product1.getSalePrice(), product1.getCurrentStock(),
                product1.getCategoryId(), product1.getTaxId(), product1.getSupplierId()
        );

        lenient().when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(adminUser);
        lenient().when(securityContextHelper.getCurrentUserId()).thenReturn(USER_ID);
        lenient().when(securityContextHelper.getCurrentUserRoleId()).thenReturn(ADMIN_ROLE_ID);
    }

    @Test
    @DisplayName("Create product - Should save and return product response when SKU is unique")
    void createProductShouldSaveAndReturnProductResponseWhenSkuIsUnique() {
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        when(userRepository.findAllById(anySet())).thenReturn(List.of(adminUser));

        ProductResponse createdProductResponse = productService.createProduct(productRequest1, USER_ID);

        assertNotNull(createdProductResponse);
        assertNotNull(createdProductResponse.id());
        assertEquals("SKU001", createdProductResponse.sku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Create product - Should throw DuplicateResourceException when SKU exists")
    void createProductShouldThrowDuplicateResourceExceptionWhenSkuExists() {
        when(productRepository.existsBySku("SKU001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                productService.createProduct(productRequest1, USER_ID)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Get all products - Should return list of product responses")
    void getAllProductsShouldReturnListOfProductResponses() {
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(userRepository.findAllById(anySet())).thenReturn(List.of(adminUser));

        List<ProductResponse> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals(product1.getSku(), products.getFirst().sku());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get product by ID - Should return product response when found")
    void getProductByIdShouldReturnProductResponseWhenFound() {
        UUID id = product1.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(userRepository.findAllById(anySet())).thenReturn(List.of(adminUser));

        Optional<ProductResponse> foundProductResponse = productService.getProductById(id);

        assertTrue(foundProductResponse.isPresent());
        assertEquals(product1.getSku(), foundProductResponse.get().sku());
    }

    @Test
    @DisplayName("Update product - Should update and return product response when found")
    void updateProductShouldUpdateAndReturnProductResponseWhenFound() {
        UUID id = product1.getId();
        ProductRequest updateRequest = new ProductRequest(
                "SKU001", "Updated Name", "Updated Desc",
                BigDecimal.valueOf(60), BigDecimal.valueOf(110), BigDecimal.valueOf(10),
                null, null, null
        );
        Product updatedDomainProduct = product1.toBuilder().name("Updated Name").build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDomainProduct);
        when(userRepository.findAllById(anySet())).thenReturn(List.of(adminUser));

        Optional<ProductResponse> result = productService.updateProduct(id, updateRequest, USER_ID);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product - Should return empty when not found")
    void updateProductShouldReturnEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        ProductRequest updateRequest = new ProductRequest(
                "SKU001", "Updated Name", "Updated Desc",
                BigDecimal.valueOf(60), BigDecimal.valueOf(110), BigDecimal.valueOf(10),
                null, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Optional<ProductResponse> result = productService.updateProduct(id, updateRequest, USER_ID);

        assertFalse(result.isPresent());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product - Should mark product as deleted when found")
    void deleteProductShouldMarkProductAsDeleted() {
        UUID id = product1.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        productService.deleteProduct(id, USER_ID);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product - Should do nothing when not found")
    void deleteProductShouldDoNothingWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        productService.deleteProduct(id, USER_ID);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Get product by SKU - Should return product response when SKU exists")
    void getProductBySkuShouldReturnProductResponseWhenSkuExists() {
        when(productRepository.findBySku(product1.getSku())).thenReturn(Optional.of(product1));
        when(userRepository.findAllById(anySet())).thenReturn(List.of(adminUser));

        Optional<ProductResponse> foundProductResponse = productService.getProductBySku(product1.getSku());

        assertTrue(foundProductResponse.isPresent());
        assertEquals(product1.getSku(), foundProductResponse.get().sku());
        verify(productRepository, times(1)).findBySku(product1.getSku());
    }

    @Test
    @DisplayName("Get product by SKU - Should return empty when SKU does not exist")
    void getProductBySkuShouldReturnEmptyWhenSkuDoesNotExist() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<ProductResponse> foundProductResponse = productService.getProductBySku("NON_EXISTENT_SKU");

        assertFalse(foundProductResponse.isPresent());
        verify(productRepository, times(1)).findBySku(anyString());
    }
}
