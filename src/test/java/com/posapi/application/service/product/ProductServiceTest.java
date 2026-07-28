package com.posapi.application.service.product;

import com.posapi.domain.exception.DuplicateResourceException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.role.Role;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
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
    private static final UUID ROLE_ID = UUID.randomUUID();

    private Product product1;
    private Product product2;
    private User mockUser;

    @BeforeEach
    void setUp() {
        Role mockRole = Role.builder()
                .id(ROLE_ID)
                .name("ADMIN")
                .build();

        mockUser = User.builder()
                .id(USER_ID)
                .role(mockRole)
                .build();

        product1 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU001")
                .name("Product 1")
                .purchasePrice(BigDecimal.valueOf(50))
                .salePrice(BigDecimal.valueOf(100))
                .currentStock(BigDecimal.valueOf(10))
                .createdByUserId(USER_ID)
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
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Create product - Should save and return product when SKU is unique")
    void createProductShouldSaveAndReturnProductWhenSkuIsUnique() {
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(mockUser);
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product createdProduct = productService.createProduct(product1, USER_ID);

        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId());
        assertEquals("SKU001", createdProduct.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Create product - Should throw DuplicateResourceException when SKU exists")
    void createProductShouldThrowDuplicateResourceExceptionWhenSkuExists() {
        when(productRepository.existsBySku("SKU001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                productService.createProduct(product1, USER_ID)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Get all products - Should return list of products")
    void getAllProductsShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertEquals(product1.getSku(), products.getFirst().getSku());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get product by ID - Should return product when found")
    void getProductByIdShouldReturnProductWhenFound() {
        UUID id = product1.getId();
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductById(id);

        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getSku(), foundProduct.get().getSku());
    }

    @Test
    @DisplayName("Update product - Should update and return product when found")
    void updateProductShouldUpdateAndReturnProductWhenFound() {
        UUID id = product1.getId();
        Product productChanges = Product.builder()
                .sku("SKU001")
                .name("Updated Name")
                .purchasePrice(BigDecimal.valueOf(60))
                .salePrice(BigDecimal.valueOf(110))
                .currentStock(BigDecimal.valueOf(10))
                .build();

        Product updatedDomainProduct = product1.toBuilder().name("Updated Name").build();

        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(mockUser);
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDomainProduct);

        Optional<Product> result = productService.updateProduct(id, productChanges, USER_ID);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Update product - Should return empty when not found")
    void updateProductShouldReturnEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        Product productChanges = Product.builder()
                .sku("SKU001")
                .name("Updated Name")
                .build();

        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(mockUser);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Product> result = productService.updateProduct(id, productChanges, USER_ID);

        assertFalse(result.isPresent());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product - Should mark product as deleted when found")
    void deleteProductShouldMarkProductAsDeleted() {
        UUID id = product1.getId();
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(mockUser);
        when(productRepository.findById(id)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        productService.deleteProduct(id, USER_ID);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Delete product - Should do nothing when not found")
    void deleteProductShouldDoNothingWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(securityContextHelper.getCurrentUserOrThrow()).thenReturn(mockUser);
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        productService.deleteProduct(id, USER_ID);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Get product by SKU - Should return product when SKU exists")
    void getProductBySkuShouldReturnProductWhenSkuExists() {
        when(productRepository.findBySku(product1.getSku())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductBySku(product1.getSku());

        assertTrue(foundProduct.isPresent());
        assertEquals(product1.getSku(), foundProduct.get().getSku());
        verify(productRepository, times(1)).findBySku(product1.getSku());
    }

    @Test
    @DisplayName("Get product by SKU - Should return empty when SKU does not exist")
    void getProductBySkuShouldReturnEmptyWhenSkuDoesNotExist() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductBySku("NON_EXISTENT_SKU");

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findBySku(anyString());
    }
}
