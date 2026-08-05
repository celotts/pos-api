package com.posapi.application.service.purchase;

import com.posapi.application.port.purchase.PurchaseManagementPort;
import com.posapi.domain.exception.ResourceNotFoundException;
import com.posapi.domain.model.inventory.InventoryTransaction;
import com.posapi.domain.model.inventory.TransactionType;
import com.posapi.domain.model.product.Product;
import com.posapi.domain.model.purchase.Purchase;
import com.posapi.domain.model.purchase.PurchaseItem;
import com.posapi.domain.model.user.User;
import com.posapi.domain.port.output.InventoryTransactionRepository;
import com.posapi.domain.port.output.ProductRepository;
import com.posapi.domain.port.output.PurchaseItemRepository;
import com.posapi.domain.port.output.PurchaseRepository;
import com.posapi.domain.port.output.UserRepository;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import com.posapi.infrastructure.adapter.input.rest.purchase.mapper.PurchaseRestMapper;
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService implements PurchaseManagementPort {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;
    private final PurchaseRestMapper purchaseRestMapper;

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        List<PurchaseItem> purchaseItems = request.items().stream()
                .map(itemRequest -> PurchaseItem.createNew(
                        null,
                        itemRequest.productId(),
                        itemRequest.quantity(),
                        itemRequest.unitPrice(),
                        currentUserId,
                        currentUserRoleId
                ))
                .collect(Collectors.toList());

        Purchase newPurchase = Purchase.createNew(
                request.supplierId(),
                request.purchaseDate(),
                purchaseItems,
                currentUserId,
                currentUserRoleId
        );

        Purchase savedPurchase = purchaseRepository.save(newPurchase);

        purchaseItems.forEach(item -> item.setPurchaseId(savedPurchase.getId()));
        List<PurchaseItem> savedPurchaseItems = purchaseItemRepository.saveAll(purchaseItems);

        List<InventoryTransaction> inventoryTransactions = new ArrayList<>();
        for (PurchaseItem item : savedPurchaseItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + item.getProductId()));

            product.increaseStock(item.getQuantity(), currentUserId, currentUserRoleId);
            productRepository.save(product);

            inventoryTransactions.add(InventoryTransaction.createNew(
                    product.getId(),
                    TransactionType.PURCHASE_IN,
                    item.getQuantity(),
                    product.getCurrentStock(),
                    savedPurchase.getId(),
                    "PURCHASE",
                    "Purchase " + savedPurchase.getId() + " - Item " + item.getId(),
                    currentUserId,
                    currentUserRoleId
            ));
        }
        inventoryTransactionRepository.saveAll(inventoryTransactions);

        BigDecimal totalAmount = savedPurchaseItems.stream()
                .map(PurchaseItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTaxAmount = BigDecimal.ZERO;

        savedPurchase.setTotalAmount(totalAmount);
        savedPurchase.setTotalTaxAmount(totalTaxAmount);
        purchaseRepository.save(savedPurchase);

        return mapToPurchaseResponse(savedPurchase, savedPurchaseItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseResponse> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id)
                .map(purchase -> {
                    List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchase.getId());
                    return mapToPurchaseResponse(purchase, items);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PurchaseResponse> getAllPurchases(Pageable pageable) {
        Page<Purchase> purchasesPage = purchaseRepository.findAll(pageable);
        List<PurchaseResponse> content = purchasesPage.getContent().stream()
                .map(purchase -> {
                    List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchase.getId());
                    return mapToPurchaseResponse(purchase, items);
                })
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                purchasesPage.getNumber(),
                purchasesPage.getSize(),
                purchasesPage.getTotalElements(),
                purchasesPage.getTotalPages(),
                purchasesPage.isLast()
        );
    }

    @Override
    @Transactional
    public Optional<PurchaseResponse> updatePurchase(UUID id, PurchaseRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        return purchaseRepository.findById(id).map(existingPurchase -> {
            existingPurchase.setSupplierId(request.supplierId());
            existingPurchase.setPurchaseDate(request.purchaseDate());
            existingPurchase.setUpdatedAt(Instant.now());
            existingPurchase.setUpdatedByUserId(currentUserId);
            existingPurchase.setUpdatedByUserRoleId(currentUserRoleId);

            Map<UUID, PurchaseItem> currentItemsMap = purchaseItemRepository.findByPurchaseId(existingPurchase.getId()).stream()
                    .collect(Collectors.toMap(PurchaseItem::getId, Function.identity()));

            Map<UUID, PurchaseItemRequest> requestItemsMap = request.items().stream()
                    .filter(itemRequest -> itemRequest.id() != null)
                    .collect(Collectors.toMap(PurchaseItemRequest::id, Function.identity()));

            List<PurchaseItem> itemsToSave = new ArrayList<>();
            List<InventoryTransaction> inventoryTransactions = new ArrayList<>();

            for (PurchaseItemRequest itemRequest : request.items()) {
                if (itemRequest.id() == null) {
                    PurchaseItem newItem = PurchaseItem.createNew(
                            existingPurchase.getId(),
                            itemRequest.productId(),
                            itemRequest.quantity(),
                            itemRequest.unitPrice(),
                            currentUserId,
                            currentUserRoleId
                    );
                    itemsToSave.add(newItem);
                    processStockChange(newItem.getProductId(), newItem.getQuantity(), TransactionType.PURCHASE_IN,
                            existingPurchase.getId(), "PURCHASE_ADD_ITEM", currentUserId, currentUserRoleId, inventoryTransactions);
                } else {
                    PurchaseItem existingItem = currentItemsMap.get(itemRequest.id());
                    if (existingItem == null) {
                        throw new ResourceNotFoundException("PurchaseItem not found with ID: " + itemRequest.id()
                                + " for Purchase: " + id);
                    }

                    currentItemsMap.remove(itemRequest.id());

                    if (!existingItem.getQuantity().equals(itemRequest.quantity())
                            || !existingItem.getUnitPrice().equals(itemRequest.unitPrice())) {
                        processStockChange(existingItem.getProductId(), existingItem.getQuantity().negate(),
                                TransactionType.ADJUSTMENT_OUT, existingPurchase.getId(), "PURCHASE_UPDATE_ITEM_OLD_QTY",
                                currentUserId, currentUserRoleId, inventoryTransactions);
                        existingItem.updateDetails(itemRequest.quantity(), itemRequest.unitPrice(), currentUserId, currentUserRoleId);
                        processStockChange(existingItem.getProductId(), existingItem.getQuantity(),
                                TransactionType.ADJUSTMENT_IN, existingPurchase.getId(), "PURCHASE_UPDATE_ITEM_NEW_QTY",
                                currentUserId, currentUserRoleId, inventoryTransactions);
                    }
                    itemsToSave.add(existingItem);
                }
            }

            for (PurchaseItem deletedItem : currentItemsMap.values()) {
                deletedItem.markAsDeleted(currentUserId, currentUserRoleId);
                itemsToSave.add(deletedItem);
                processStockChange(deletedItem.getProductId(), deletedItem.getQuantity().negate(),
                        TransactionType.ADJUSTMENT_OUT, existingPurchase.getId(), "PURCHASE_REMOVE_ITEM",
                        currentUserId, currentUserRoleId, inventoryTransactions);
            }

            purchaseItemRepository.saveAll(itemsToSave);
            inventoryTransactionRepository.saveAll(inventoryTransactions);

            List<PurchaseItem> finalItems = purchaseItemRepository.findByPurchaseId(existingPurchase.getId()).stream()
                    .filter(item -> item.getDeletedAt() == null)
                    .collect(Collectors.toList());

            BigDecimal totalAmount = finalItems.stream()
                    .map(PurchaseItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalTaxAmount = BigDecimal.ZERO;

            existingPurchase.setTotalAmount(totalAmount);
            existingPurchase.setTotalTaxAmount(totalTaxAmount);
            Purchase updatedPurchase = purchaseRepository.save(existingPurchase);

            return mapToPurchaseResponse(updatedPurchase, finalItems);
        });
    }

    @Override
    @Transactional
    public void deletePurchase(UUID id, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        purchaseRepository.findById(id).ifPresent(existingPurchase -> {
            List<PurchaseItem> itemsToDelete = purchaseItemRepository.findByPurchaseId(existingPurchase.getId());

            itemsToDelete.forEach(item -> {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + item.getProductId()));

                product.decreaseStock(item.getQuantity(), currentUserId, currentUserRoleId);
                productRepository.save(product);

                InventoryTransaction reverseTransaction = InventoryTransaction.createNew(
                        product.getId(),
                        TransactionType.ADJUSTMENT_OUT,
                        item.getQuantity().negate(),
                        product.getCurrentStock(),
                        existingPurchase.getId(),
                        "PURCHASE_REVERSAL",
                        "Reversal of Purchase " + existingPurchase.getId() + " - Item " + item.getId(),
                        currentUserId,
                        currentUserRoleId
                );
                inventoryTransactionRepository.save(reverseTransaction);
            });

            existingPurchase.markAsCancelled(currentUserId, currentUserRoleId);
            purchaseRepository.save(existingPurchase);
            log.info("Purchase with id {} marked as deleted by user {}", id, currentUserId);
        });
    }

    private void processStockChange(UUID productId, BigDecimal quantityChange, TransactionType type, UUID docId,
                                    String notes, UUID userId, UUID roleId, List<InventoryTransaction> transactions) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + productId));

        if (quantityChange.compareTo(BigDecimal.ZERO) > 0) {
            product.increaseStock(quantityChange, userId, roleId);
        } else {
            product.decreaseStock(quantityChange.negate(), userId, roleId);
        }
        productRepository.save(product);

        transactions.add(InventoryTransaction.createNew(
                product.getId(), type, quantityChange, product.getCurrentStock(), docId,
                "PURCHASE", notes + " - Product " + product.getName(), userId, roleId
        ));
    }

    private PurchaseResponse mapToPurchaseResponse(Purchase purchase, List<PurchaseItem> items) {
        PurchaseResponse response = purchaseRestMapper.toResponse(purchase);
        // Lógica de enriquecimiento si es necesaria
        return response;
    }
}
