package com.posapi.application.service.sale;

import com.posapi.application.port.sale.SaleMagnamentPort;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.inventory.InventoryTransaction;
import com.posapi.domain.model.inventory.TransactionType;
import com.posapi.domain.model.product.Product;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SaleRestMapper saleRestMapper;
    private final SaleItemRestMapper saleItemRestMapper;

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
                currentUserRoleId);

        Sale savedSale = saleRepository.save(newSale);

        List<SaleItem> savedItems = request.getItems().stream()
                .map(item -> addSaleItemInternal(savedSale.getId(), item, currentUser.getId(), currentUserRoleId))
                .collect(Collectors.toList());

        BigDecimal totalAmount = savedItems.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

        savedSale.setTotalAmount(totalAmount.subtract(savedSale.getDiscountAmount()));
        savedSale.setTotalTaxAmount(totalTaxAmount);
        saleRepository.save(savedSale);

        return mapToSaleResponse(savedSale, savedItems);
    }

    private SaleItem addSaleItemInternal(UUID saleId, SaleItemRequest itemRequest, UUID userId, UUID roleId) {
        Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemRequest.productId()));

        if (product.getCurrentStock().compareTo(itemRequest.quantity()) < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        SaleItem newSaleItem = SaleItem.createNew(
                saleId, product.getId(), itemRequest.quantity(), itemRequest.unitPrice(), userId, roleId);

        product.decreaseStock(itemRequest.quantity(), userId, roleId);
        productRepository.save(product);

        var inventoryTransaction = InventoryTransaction.createNew(
                product.getId(),
                TransactionType.SALE_OUT,
                itemRequest.quantity().negate(),
                product.getCurrentStock(),
                saleId,
                "SALE",
                "Sale " + saleId + " - Product " + product.getName(),
                userId,
                roleId);
        inventoryTransactionRepository.save(inventoryTransaction);

        return saleItemRepository.save(newSaleItem);
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
    @Transactional(readOnly = true)
    public Page<SaleResponse> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable)
                .map(sale -> {
                    List<SaleItem> items = saleItemRepository.findAllBySaleId(sale.getId());
                    return mapToSaleResponse(sale, items);
                });
    }

    @Override
    @Transactional
    public SaleResponse updateSale(UUID saleId, SaleRequest request, UUID currentUserId) {
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

        List<SaleItem> itemsToDelete = saleItemRepository.findAllBySaleId(saleId);
        for (SaleItem item : itemsToDelete) {
            deleteSaleItem(saleId, item.getId());
        }

        existingSale.markAsDeleted(currentUser.getId(), currentUserRoleId);
        saleRepository.save(existingSale);
        log.info("Sale with id {} marked as deleted by user {}", saleId, currentUser.getId());
    }

    @Override
    @Transactional
    public SaleResponse addSaleItem(UUID saleId, SaleItemRequest itemRequest) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        addSaleItemInternal(saleId, itemRequest, currentUser.getId(), currentUser.getRole().getId());
        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        return mapToSaleResponse(sale, items);
    }

    @Override
    @Transactional
    public SaleResponse updateSaleItem(UUID saleId, UUID itemId, SaleItemRequest itemRequest, UUID currentUserId) {
        // Lógica para actualizar un ítem
        return getSaleById(saleId); // Placeholder
    }

    @Override
    @Transactional
    public SaleResponse deleteSaleItem(UUID saleId, UUID itemId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        SaleItem existingItem = saleItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale item not found with ID: " + itemId));

        Product product = productRepository.findById(existingItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + existingItem.getProductId()));
        product.increaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId);
        productRepository.save(product);

        existingItem.markAsDeleted(currentUser.getId(), currentUserRoleId);
        saleItemRepository.save(existingItem);

        var reverseTransaction = InventoryTransaction.createNew(
                product.getId(),
                TransactionType.ADJUSTMENT_IN,
                existingItem.getQuantity(),
                product.getCurrentStock(),
                saleId,
                "SALE_REMOVE_ITEM",
                "Reversal for removed SaleItem " + itemId + " from Sale " + saleId,
                currentUser.getId(),
                currentUserRoleId);
        inventoryTransactionRepository.save(reverseTransaction);

        Sale sale = saleRepository.findById(saleId).orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId);
        return mapToSaleResponse(sale, items);
    }

    private SaleResponse mapToSaleResponse(Sale sale, List<SaleItem> items) {
        SaleResponse response = saleRestMapper.toResponse(sale);

        List<UUID> productIds = items.stream().map(SaleItem::getProductId).collect(Collectors.toList());
        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        Set<UUID> userIds = Stream.of(
                        sale.getCreatedByUserId(),
                        sale.getUpdatedByUserId(),
                        sale.getDeletedByUserId()
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        items.forEach(item -> {
            userIds.add(item.getCreatedByUserId());
            userIds.add(item.getUpdatedByUserId());
            userIds.add(item.getDeletedByUserId());
        });
        userIds.remove(null); // Remove any null UUIDs that might have been added

        Map<UUID, String> userNames = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            userNames = userRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getFullName));
        }

        List<SaleItemResponse> itemResponses = saleItemRestMapper.toResponseList(items, productMap, userNames);
        response.setItems(itemResponses);

        if (sale.getCustomerId() != null) {
            customerRepository.findById(sale.getCustomerId())
                    .ifPresent(customer -> response.setCustomerName(customer.getFullName()));
        }

        response.setCreatedByName(userNames.get(sale.getCreatedByUserId()));
        response.setUpdatedByName(userNames.get(sale.getUpdatedByUserId()));
        response.setDeletedByName(userNames.get(sale.getDeletedByUserId()));


        return response;
    }
}
