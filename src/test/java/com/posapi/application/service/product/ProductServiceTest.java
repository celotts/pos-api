package com.posapi.application.service.product;

import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.port.output.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU001")
                .name("Product 1")
                .description("Description 1")
                .purchasePrice(BigDecimal.valueOf(10.0))
                .salePrice(BigDecimal.valueOf(15.0))
                .currentStock(new BigDecimal("100.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .sku("SKU001")
                .name("Product 1")
                .description("Description 1")
                .purchasePrice(BigDecimal.valueOf(10.0))
                .salePrice(BigDecimal.valueOf(15.0))
                .currentStock(new BigDecimal("100.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createProductShouldReturnCreatedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product createdProduct = productService.createProduct(product1);

        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getSku()).isEqualTo("SKU001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductByIdShouldReturnProductWhenFound() {
        when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductById(product1.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getId()).isEqualTo(product1.getId());
        assertThat(foundProduct.get().getName()).isEqualTo(product1.getName());

        verify(productRepository, times(1)).findById(product1.getId());
    }

    @Test
    void getProductByIdShouldReturnEmptyWhenNotFound() {
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductById(UUID.randomUUID());

        assertThat(foundProduct).isNotPresent();
        verify(productRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getAllProductsShouldReturnListOfProducts() {
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> allProducts = productService.getAllProducts();

        assertThat(allProducts).isNotNull();
        assertThat(allProducts).hasSize(2);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void updateProductShouldReturnUpdatedProductWhenFound() {
        UUID id = UUID.randomUUID();
        Product existingProduct = Product.builder()
                .id(id)
                .name("Old Name")
                .currentStock(new BigDecimal("50.0"))
                .build();

        Product updatedDetails = Product.builder()
                .name("New Name")
                .currentStock(new BigDecimal("75.5"))
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(id, updatedDetails);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertThat(savedProduct.getName()).isEqualTo("New Name");
        assertThat(savedProduct.getCurrentStock()).isEqualTo(new BigDecimal("75.5"));
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void updateProductShouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        Product updatedDetails = Product.builder().name("Non Existent").build();

        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                productService.updateProduct(id, updatedDetails)
        );

        assertThat(exception.getMessage()).isEqualTo("Product not found with ID: " + id);
        verify(productRepository, times(1)).findById(any(UUID.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProductShouldCallRepositoryDelete() {
        doNothing().when(productRepository).deleteById(product1.getId());

        productService.deleteProduct(product1.getId());

        verify(productRepository, times(1)).deleteById(product1.getId());
    }

    @Test
    void getProductBySkuShouldReturnProductWhenFound() {
        when(productRepository.findBySku(product1.getSku())).thenReturn(Optional.of(product1));

        Optional<Product> foundProduct = productService.getProductBySku(product1.getSku());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo(product1.getName());
        verify(productRepository, times(1)).findBySku(product1.getSku());
    }

    @Test
    void getProductBySkuShouldReturnEmptyWhenNotFound() {
        when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProductBySku("NON_EXISTENT_SKU");

        assertThat(foundProduct).isNotPresent();
        verify(productRepository, times(1)).findBySku(anyString());
    }
}
