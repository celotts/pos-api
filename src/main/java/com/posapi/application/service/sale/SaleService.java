package com.posapi.application.service.sale;

import com.posapi.application.port.sale.SaleMagnamentPort;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.customer.Customer;
import com.posapi.domain.model.inventory.InventoryTransaction;
import com.posapi.domain.model.inventory.TransactionType;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.sale.Sale;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.CustomerRepository;
import com.posapi.domain.port.output.InventoryTransactionRepository;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.SaleRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleItemResponse;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleRequest;
import com.posapi.infrastructure.adapter.input.rest.sale.dto.SaleResponse;
import com.posapi.infrastructure.security.SecurityContextHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService implements SaleMagnamentPort {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        // 1. Validar cliente
        if (request.getCustomerId() != null && !customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId());
        }

        // 2. Crear Sale de dominio
        Sale newSale = Sale.createNew(
                request.getCustomerId(),
                BigDecimal.ZERO, // Se calculará después de añadir ítems
                BigDecimal.ZERO, // Se calculará después de añadir ítems
                request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO,
                request.getPosTerminalId(),
                request.getShiftId(),
                currentUser.getId(),
                currentUserRoleId
        );

        Sale savedSale = saleRepository.save(newSale);

        // 3. Añadir ítems y actualizar stock
        List<SaleItemResponse> itemResponses = request.getItems().stream()
                .map(itemRequest -> addSaleItemInternal(savedSale.getId(), itemRequest, currentUser.getId(), currentUserRoleId))
                .collect(Collectors.toList());

        // 4. Recalcular totales de la venta
        BigDecimal totalAmount = itemResponses.stream()
                .map(SaleItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        savedSale.setTotalAmount(totalAmount.subtract(savedSale.getDiscountAmount()));
        savedSale.setTotalTaxAmount(totalTaxAmount);
        saleRepository.save(savedSale);

        return mapToSaleResponse(savedSale, itemResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        return mapToSaleResponse(sale, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(sale -> mapToSaleResponse(sale, List.of()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SaleResponse updateSale(UUID saleId, SaleRequest request) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        if (request.getCustomerId() != null) {
            if (!customerRepository.existsById(request.getCustomerId())) {
                throw new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId());
            }
            existingSale.setCustomerId(request.getCustomerId());
        }
        if (request.getDiscountAmount() != null) {
            existingSale.setDiscountAmount(request.getDiscountAmount());
        }
        if (request.getPosTerminalId() != null) {
            existingSale.setPosTerminalId(request.getPosTerminalId());
        }
        if (request.getShiftId() != null) {
            existingSale.setShiftId(request.getShiftId());
        }

        existingSale.setUpdatedAt(Instant.now());
        existingSale.setUpdatedByUserId(currentUser.getId());
        existingSale.setUpdatedByUserRoleId(currentUserRoleId);

        Sale updatedSale = saleRepository.save(existingSale);
        return mapToSaleResponse(updatedSale, List.of());
    }

    @Override
    @Transactional
    public void deleteSale(UUID saleId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        existingSale.markAsDeleted(currentUser.getId(), currentUserRoleId);
        saleRepository.save(existingSale);
        log.info("Sale with id {} marked as deleted by user {}", saleId, currentUser.getId());
    }

    @Override
    @Transactional
    public SaleResponse addSaleItem(UUID saleId, SaleItemRequest itemRequest) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        SaleItemResponse addedItem = addSaleItemInternal(saleId, itemRequest, currentUser.getId(), currentUserRoleId);

        existingSale.setTotalAmount(existingSale.getTotalAmount().add(addedItem.getSubtotal()));
        saleRepository.save(existingSale);

        return mapToSaleResponse(existingSale, List.of(addedItem));
    }

    @Override
    @Transactional
    public SaleResponse updateSaleItem(UUID saleId, UUID itemId, SaleItemRequest itemRequest) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        // TODO: Implementar la reversión del ítem anterior y aplicar las nuevas cantidades
        log.info("Updating item {} for sale {} by user {}", itemId, saleId, currentUser.getId());

        return mapToSaleResponse(existingSale, List.of());
    }

    @Override
    @Transactional
    public SaleResponse deleteSaleItem(UUID saleId, UUID itemId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        // TODO: Implementar la eliminación del ítem y restaurar el stock del producto
        log.info("Deleting item {} from sale {} by user {}", itemId, saleId, currentUser.getId());

        return mapToSaleResponse(existingSale, List.of());
    }

    private SaleItemResponse addSaleItemInternal(UUID saleId, SaleItemRequest itemRequest, UUID currentUserId, UUID currentUserRoleId) {
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

        if (product.getCurrentStock().compareTo(itemRequest.getQuantity()) < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        BigDecimal subtotal = itemRequest.getQuantity().multiply(itemRequest.getUnitPrice());

        product.decreaseStock(itemRequest.getQuantity(), currentUserId, currentUserRoleId);
        productRepository.save(product);

        InventoryTransaction inventoryTransaction = InventoryTransaction.createNew(
                product.getId(),
                TransactionType.SALE_OUT,
                itemRequest.getQuantity().negate(),
                product.getCurrentStock(),
                saleId,
                "SALE",
                "Sale " + saleId + " - Product " + product.getName(),
                currentUserId,
                currentUserRoleId
        );
        inventoryTransactionRepository.save(inventoryTransaction);

        String currentUserName = userRepository.findById(currentUserId)
                .map(User::getUsername)
                .orElse("System");

        Instant now = Instant.now();

        return new SaleItemResponse(
                UUID.randomUUID(),
                saleId,
                product.getId(),
                product.getName(),
                product.getSku(),
                itemRequest.getQuantity(),
                itemRequest.getUnitPrice(),
                subtotal,
                now,
                now,
                now,
                currentUserId,
                currentUserId,
                currentUserRoleId,
                currentUserRoleId,
                currentUserId,
                currentUserRoleId,
                currentUserName,
                currentUserName,
                currentUserName
        );
    }

    private SaleResponse mapToSaleResponse(Sale sale, List<SaleItemResponse> items) {
        String createdByName = sale.getCreatedByUserId() != null
                ? userRepository.findById(sale.getCreatedByUserId()).map(User::getUsername).orElse(null)
                : null;
        String updatedByName = sale.getUpdatedByUserId() != null
                ? userRepository.findById(sale.getUpdatedByUserId()).map(User::getUsername).orElse(null)
                : null;
        String customerName = sale.getCustomerId() != null
                ? customerRepository.findById(sale.getCustomerId()).map(Customer::getFullName).orElse(null)
                : null;

        return new SaleResponse(
                sale.getId(),
                sale.getCustomerId(),
                sale.getCreatedAt(),
                sale.getTotalAmount(),
                sale.getTotalTaxAmount(),
                sale.getDiscountAmount(),
                sale.getStatus(),
                sale.getPaymentStatus(),
                sale.getPosTerminalId(),
                sale.getShiftId(),
                sale.getCreatedAt(),
                sale.getUpdatedAt(),
                sale.getCreatedByUserId(),
                sale.getUpdatedByUserId(),
                items,
                createdByName,
                updatedByName,
                customerName
        );
    }
}
