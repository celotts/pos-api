package com.posapi.application.service.sale;

import com.posapi.application.port.sale.SaleMagnamentPort;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.customer.Customer;
import com.posapi.domain.model.inventory.InventoryTransaction;
import com.posapi.domain.model.inventory.TransactionType;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.sale.Sale;
import com.posapi.domain.model.sale.SaleItem;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.*;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService implements SaleMagnamentPort {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
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

        if (request.getCustomerId() != null && !customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId());
        }

        Sale newSale = Sale.createNew(
                request.getCustomerId(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO,
                request.getPosTerminalId(),
                request.getShiftId(),
                currentUser.getId(),
                currentUserRoleId
        );

        Sale savedSale = saleRepository.save(newSale);

        List<SaleItem> savedItems = request.getItems().stream()
                .map(itemRequest -> addSaleItemInternal(savedSale.getId(), itemRequest, currentUser.getId(), currentUserRoleId))
                .collect(Collectors.toList());

        BigDecimal totalAmount = savedItems.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        savedSale.setTotalAmount(totalAmount.subtract(savedSale.getDiscountAmount()));
        savedSale.setTotalTaxAmount(totalTaxAmount);
        saleRepository.save(savedSale);

        return mapToSaleResponse(savedSale, savedItems);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));
        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        return mapToSaleResponse(sale, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(sale -> {
                    List<SaleItem> items = saleItemRepository.findAllBySaleId(sale.getId());
                    return mapToSaleResponse(sale, items);
                })
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
        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        return mapToSaleResponse(updatedSale, items);
    }

    @Override
    @Transactional
    public void deleteSale(UUID saleId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        for (SaleItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + item.getProductId()));
            product.increaseStock(item.getQuantity(), currentUser.getId(), currentUserRoleId);
            productRepository.save(product);
            item.markAsDeleted(currentUser.getId(), currentUserRoleId);
            saleItemRepository.save(item);
        }

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

        SaleItem addedItem = addSaleItemInternal(saleId, itemRequest, currentUser.getId(), currentUserRoleId);

        existingSale.setTotalAmount(existingSale.getTotalAmount().add(addedItem.getSubtotal()));
        saleRepository.save(existingSale);

        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        return mapToSaleResponse(existingSale, items);
    }

    @Override
    @Transactional
    public SaleResponse updateSaleItem(UUID saleId, UUID itemId, SaleItemRequest itemRequest) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        SaleItem existingItem = saleItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale item not found with ID: " + itemId));

        Product product = productRepository.findById(existingItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + existingItem.getProductId()));

        product.increaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId);

        existingItem.updateDetails(itemRequest.getQuantity(), itemRequest.getUnitPrice(), currentUser.getId(), currentUserRoleId);

        product.decreaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId);

        productRepository.save(product);
        saleItemRepository.save(existingItem);

        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        BigDecimal totalAmount = items.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        existingSale.setTotalAmount(totalAmount.subtract(existingSale.getDiscountAmount()));
        saleRepository.save(existingSale);

        return mapToSaleResponse(existingSale, items);
    }

    @Override
    @Transactional
    public SaleResponse deleteSaleItem(UUID saleId, UUID itemId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        Sale existingSale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

        SaleItem existingItem = saleItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale item not found with ID: " + itemId));

        Product product = productRepository.findById(existingItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + existingItem.getProductId()));

        product.increaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId);
        productRepository.save(product);

        existingItem.markAsDeleted(currentUser.getId(), currentUserRoleId);
        saleItemRepository.save(existingItem);

        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        BigDecimal totalAmount = items.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        existingSale.setTotalAmount(totalAmount.subtract(existingSale.getDiscountAmount()));
        saleRepository.save(existingSale);

        return mapToSaleResponse(existingSale, items);
    }

    private SaleItem addSaleItemInternal(UUID saleId, SaleItemRequest itemRequest, UUID currentUserId, UUID currentUserRoleId) {
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.getProductId()));

        if (product.getCurrentStock().compareTo(itemRequest.getQuantity()) < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        SaleItem newSaleItem = SaleItem.createNew(
                saleId,
                product.getId(),
                itemRequest.getQuantity(),
                itemRequest.getUnitPrice(),
                currentUserId,
                currentUserRoleId
        );

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

        return saleItemRepository.save(newSaleItem);
    }

    private SaleResponse mapToSaleResponse(Sale sale, List<SaleItem> items) {
        Set<UUID> userIds = Stream.concat(
                Stream.of(sale.getCreatedByUserId(), sale.getUpdatedByUserId(), sale.getDeletedByUserId()),
                items.stream().flatMap(item -> Stream.of(item.getCreatedByUserId(), item.getUpdatedByUserId(), item.getDeletedByUserId()))
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        List<SaleItemResponse> itemResponses = items.stream()
                .map(item -> new SaleItemResponse(
                        item.getId(),
                        item.getSaleId(),
                        item.getProductId(),
                        productRepository.findById(item.getProductId()).map(Product::getName).orElse("N/A"),
                        productRepository.findById(item.getProductId()).map(Product::getSku).orElse("N/A"),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal(),
                        item.getCreatedAt(),
                        item.getUpdatedAt(),
                        item.getDeletedAt(),
                        item.getCreatedByUserId(),
                        item.getUpdatedByUserId(),
                        item.getDeletedByUserId(),
                        item.getCreatedByUserRoleId(),
                        item.getUpdatedByUserRoleId(),
                        item.getDeletedByUserRoleId(),
                        userNames.get(item.getCreatedByUserId()),
                        userNames.get(item.getUpdatedByUserId()),
                        userNames.get(item.getDeletedByUserId())
                )).collect(Collectors.toList());

        String createdByName = userNames.get(sale.getCreatedByUserId());
        String updatedByName = userNames.get(sale.getUpdatedByUserId());
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
                itemResponses,
                createdByName,
                updatedByName,
                customerName
        );
    }

    private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }
}
