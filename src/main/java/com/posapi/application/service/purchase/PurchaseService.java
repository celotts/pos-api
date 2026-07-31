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
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemResponse;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest; // Importar PurchaseItemRequest
import com.posapi.infrastructure.security.SecurityContextHelper;
import com.posapi.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, UUID currentUserId) {
        User currentUser = securityContextHelper.getCurrentUserOrThrow();
        UUID currentUserRoleId = currentUser.getRole().getId();

        // 1. Convertir PurchaseItemRequest a PurchaseItem de dominio
        List<PurchaseItem> purchaseItems = request.items().stream()
                .map(itemRequest -> PurchaseItem.createNew(
                        null, // purchaseId se asignará después de guardar la compra
                        itemRequest.productId(), // Usar productId() para records
                        itemRequest.quantity(),  // Usar quantity() para records
                        itemRequest.unitPrice(), // Usar unitPrice() para records
                        currentUserId,
                        currentUserRoleId
                ))
                .collect(Collectors.toList());

        // 2. Crear la entidad Purchase de dominio
        Purchase newPurchase = Purchase.createNew(
                request.supplierId(),
                request.purchaseDate(),
                purchaseItems, // Pasar los ítems para calcular totales
                currentUserId,
                currentUserRoleId
        );

        // 3. Guardar la Purchase principal
        Purchase savedPurchase = purchaseRepository.save(newPurchase);

        // 4. Asignar purchaseId a cada PurchaseItem y guardarlos
        purchaseItems.forEach(item -> item.setPurchaseId(savedPurchase.getId()));
        List<PurchaseItem> savedPurchaseItems = purchaseItemRepository.saveAll(purchaseItems);

        // 5. Actualizar stock de productos y registrar transacciones de inventario
        List<InventoryTransaction> inventoryTransactions = new ArrayList<>();
        for (PurchaseItem item : savedPurchaseItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + item.getProductId()));

            // Aumentar stock del producto
            product.increaseStock(item.getQuantity(), currentUserId, currentUserRoleId);
            productRepository.save(product); // Guardar el producto con el stock actualizado

            // Crear transacción de inventario
            inventoryTransactions.add(InventoryTransaction.createNew(
                    product.getId(),
                    TransactionType.PURCHASE_IN,
                    item.getQuantity(),
                    product.getCurrentStock(), // Stock final después de la actualización
                    savedPurchase.getId(),
                    "PURCHASE",
                    "Purchase " + savedPurchase.getId() + " - Item " + item.getId(),
                    currentUserId,
                    currentUserRoleId
            ));
        }
        inventoryTransactionRepository.saveAll(inventoryTransactions);

        // 6. Recalcular totales de la compra
        BigDecimal totalAmount = savedPurchaseItems.stream()
                .map(PurchaseItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

        savedPurchase.setTotalAmount(totalAmount);
        savedPurchase.setTotalTaxAmount(totalTaxAmount);
        purchaseRepository.save(savedPurchase); // Guardar la compra con los totales actualizados

        // 7. Mapear a PurchaseResponse
        return mapToPurchaseResponse(savedPurchase, savedPurchaseItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PurchaseResponse> getPurchaseById(UUID id) {
        return purchaseRepository.findById(id)
                .map(purchase -> {
                    List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchase.getId());
                    purchase.setItems(items); // Asignar los ítems a la compra
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
                    purchase.setItems(items);
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
            // 1. Actualizar campos principales de la compra
            existingPurchase.setSupplierId(request.supplierId());
            existingPurchase.setPurchaseDate(request.purchaseDate());
            existingPurchase.setUpdatedAt(Instant.now());
            existingPurchase.setUpdatedByUserId(currentUserId);
            existingPurchase.setUpdatedByUserRoleId(currentUserRoleId);

            // 2. Gestionar ítems de la compra
            Map<UUID, PurchaseItem> currentItemsMap = purchaseItemRepository.findByPurchaseId(existingPurchase.getId()).stream()
                    .collect(Collectors.toMap(PurchaseItem::getId, Function.identity()));

            Map<UUID, PurchaseItemRequest> requestItemsMap = request.items().stream()
                    .filter(itemRequest -> itemRequest.id() != null) // Solo ítems con ID para comparación
                    .collect(Collectors.toMap(PurchaseItemRequest::id, Function.identity()));

            List<PurchaseItem> itemsToSave = new ArrayList<>();
            List<InventoryTransaction> inventoryTransactions = new ArrayList<>();

            // Iterar sobre los ítems de la solicitud
            for (PurchaseItemRequest itemRequest : request.items()) {
                if (itemRequest.id() == null) {
                    // Nuevo ítem: Crear, incrementar stock
                    PurchaseItem newItem = PurchaseItem.createNew(
                            existingPurchase.getId(),
                            itemRequest.productId(),
                            itemRequest.quantity(),
                            itemRequest.unitPrice(),
                            currentUserId,
                            currentUserRoleId
                    );
                    itemsToSave.add(newItem);
                    processStockChange(newItem.getProductId(), newItem.getQuantity(), TransactionType.PURCHASE_IN, existingPurchase.getId(), "PURCHASE_ADD_ITEM", currentUserId, currentUserRoleId, inventoryTransactions);
                } else {
                    // Ítem existente: Modificar o mantener
                    PurchaseItem existingItem = currentItemsMap.get(itemRequest.id());
                    if (existingItem == null) {
                        throw new ResourceNotFoundException("PurchaseItem not found with ID: " + itemRequest.id() + " for Purchase: " + id);
                    }

                    // Eliminar de currentItemsMap para identificar los eliminados al final
                    currentItemsMap.remove(itemRequest.id());

                    if (!existingItem.getQuantity().equals(itemRequest.quantity()) || !existingItem.getUnitPrice().equals(itemRequest.unitPrice())) {
                        // Cantidad o precio modificado: Revertir stock antiguo, aplicar nuevo
                        processStockChange(existingItem.getProductId(), existingItem.getQuantity().negate(), TransactionType.ADJUSTMENT_OUT, existingPurchase.getId(), "PURCHASE_UPDATE_ITEM_OLD_QTY", currentUserId, currentUserRoleId, inventoryTransactions);
                        existingItem.updateDetails(itemRequest.quantity(), itemRequest.unitPrice(), currentUserId, currentUserRoleId);
                        processStockChange(existingItem.getProductId(), existingItem.getQuantity(), TransactionType.ADJUSTMENT_IN, existingPurchase.getId(), "PURCHASE_UPDATE_ITEM_NEW_QTY", currentUserId, currentUserRoleId, inventoryTransactions);
                    }
                    itemsToSave.add(existingItem);
                }
            }

            // Ítems eliminados (los que quedan en currentItemsMap)
            for (PurchaseItem deletedItem : currentItemsMap.values()) {
                deletedItem.markAsDeleted(currentUserId, currentUserRoleId);
                itemsToSave.add(deletedItem);
                processStockChange(deletedItem.getProductId(), deletedItem.getQuantity().negate(), TransactionType.ADJUSTMENT_OUT, existingPurchase.getId(), "PURCHASE_REMOVE_ITEM", currentUserId, currentUserRoleId, inventoryTransactions);
            }

            // 3. Guardar ítems y transacciones
            purchaseItemRepository.saveAll(itemsToSave);
            inventoryTransactionRepository.saveAll(inventoryTransactions);

            // 4. Recalcular totales de la compra
            List<PurchaseItem> finalItems = purchaseItemRepository.findByPurchaseId(existingPurchase.getId()).stream()
                    .filter(item -> item.getDeletedAt() == null) // Solo ítems no eliminados
                    .collect(Collectors.toList());

            BigDecimal totalAmount = finalItems.stream()
                    .map(PurchaseItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

            existingPurchase.setTotalAmount(totalAmount);
            existingPurchase.setTotalTaxAmount(totalTaxAmount);
            Purchase updatedPurchase = purchaseRepository.save(existingPurchase);

            updatedPurchase.setItems(finalItems);
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
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " +
                                item.getProductId()));

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

    private void processStockChange(UUID productId, BigDecimal quantityChange, TransactionType transactionType, UUID sourceDocumentId, String notesPrefix, UUID currentUserId, UUID currentUserRoleId, List<InventoryTransaction> transactionsList) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + productId));

        if (quantityChange.compareTo(BigDecimal.ZERO) > 0) {
            product.increaseStock(quantityChange, currentUserId, currentUserRoleId);
        } else {
            product.decreaseStock(quantityChange.negate(), currentUserId, currentUserRoleId);
        }
        productRepository.save(product);

        transactionsList.add(InventoryTransaction.createNew(
                product.getId(),
                transactionType,
                quantityChange,
                product.getCurrentStock(),
                sourceDocumentId,
                "PURCHASE",
                notesPrefix + " - Product " + product.getName(),
                currentUserId,
                currentUserRoleId
        ));
    }


    private PurchaseResponse mapToPurchaseResponse(Purchase purchase, List<PurchaseItem> items) {
        Set<UUID> userIds = Stream.of(
                purchase.getCreatedByUserId(),
                purchase.getUpdatedByUserId(),
                purchase.getDeletedByUserId()
        ).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<UUID, String> userNames = fetchUserNames(userIds);

        String createdByName = userNames.getOrDefault(purchase.getCreatedByUserId(), null);
        String updatedByName = userNames.getOrDefault(purchase.getUpdatedByUserId(), null);
        String deletedByName = userNames.getOrDefault(purchase.getDeletedByUserId(), null);

        List<PurchaseItemResponse> itemResponses = items.stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null);
                    String productName = (product != null) ? product.getName() : "Unknown Product";
                    String productSku = (product != null) ? product.getSku() : "Unknown SKU";

                    Set<UUID> itemUserIds = Stream.of(
                            item.getCreatedByUserId(),
                            item.getUpdatedByUserId(),
                            item.getDeletedByUserId()
                    ).filter(Objects::nonNull).collect(Collectors.toSet());
                    Map<UUID, String> itemUserNames = fetchUserNames(itemUserIds);

                    String itemCreatedByName = itemUserNames.getOrDefault(item.getCreatedByUserId(), null);
                    String itemUpdatedByName = itemUserNames.getOrDefault(item.getUpdatedByUserId(), null);
                    String itemDeletedByName = itemUserNames.getOrDefault(item.getDeletedByUserId(), null);

                    return PurchaseItemResponse.fromDomain(
                            item,
                            productName,
                            productSku,
                            itemCreatedByName,
                            itemUpdatedByName,
                            itemDeletedByName
                    );
                })
                .collect(Collectors.toList());

        return PurchaseResponse.fromDomain(
                purchase,
                itemResponses,
                createdByName,
                updatedByName,
                deletedByName
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
