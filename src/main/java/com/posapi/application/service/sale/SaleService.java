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
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemRequest; // CORRECTED IMPORT
import com.posapi.infrastructure.adapter.input.rest.saleItem.dto.SaleItemResponse;
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
                                currentUserRoleId);

                Sale savedSale = saleRepository.save(newSale);

                List<SaleItem> savedItems = request.getItems().stream()
                                .map(itemRequest -> addSaleItemInternal(savedSale.getId(), itemRequest,
                                                currentUser.getId(), currentUserRoleId))
                                .collect(Collectors.toList());

                BigDecimal totalAmount = savedItems.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

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
                                throw new ResourceNotFoundException(
                                                "Customer not found with ID: " + request.getCustomerId());
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

                // TODO: Lógica para actualizar ítems de venta (añadir, modificar, eliminar)
                // Esto es más complejo y requeriría comparar la lista de ítems de request con
                // los existentes.
                // Por ahora, solo actualizamos la venta principal.

                Sale updatedSale = saleRepository.save(existingSale);
                List<SaleItem> items = saleItemRepository.findAllBySaleId(saleId); // Recuperar ítems para la respuesta
                return mapToSaleResponse(updatedSale, items);
        }

        @Override
        @Transactional
        public void deleteSale(UUID saleId) {
                User currentUser = securityContextHelper.getCurrentUserOrThrow();
                UUID currentUserRoleId = currentUser.getRole().getId();

                Sale existingSale = saleRepository.findById(saleId)
                                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

                // Revertir stock de productos de los ítems de la venta
                List<SaleItem> itemsToDelete = saleItemRepository.findAllBySaleId(saleId);
                for (SaleItem item : itemsToDelete) {
                        Product product = productRepository.findById(item.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product not found for ID: " + item.getProductId()));

                        // Aumentar stock (revertir la venta)
                        product.increaseStock(item.getQuantity(), currentUser.getId(), currentUserRoleId);
                        productRepository.save(product);

                        // Marcar el SaleItem como eliminado
                        item.markAsDeleted(currentUser.getId(), currentUserRoleId);
                        saleItemRepository.save(item);

                        // Registrar transacción de inventario de ajuste
                        InventoryTransaction reverseTransaction = InventoryTransaction.createNew(
                                        product.getId(),
                                        TransactionType.ADJUSTMENT_IN, // Ajuste de entrada por reversión de venta
                                        item.getQuantity(),
                                        product.getCurrentStock(),
                                        saleId,
                                        "SALE_REVERSAL",
                                        "Reversal of Sale " + saleId + " - Item " + item.getId(),
                                        currentUser.getId(),
                                        currentUserRoleId);
                        inventoryTransactionRepository.save(reverseTransaction);
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

                // Recalcular totales de la venta
                List<SaleItem> currentItems = saleItemRepository.findAllBySaleId(saleId); // Obtener todos los ítems
                                                                                          // (incluido el nuevo)
                BigDecimal totalAmount = currentItems.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

                existingSale.setTotalAmount(totalAmount.subtract(existingSale.getDiscountAmount()));
                existingSale.setTotalTaxAmount(totalTaxAmount);
                saleRepository.save(existingSale);

                return mapToSaleResponse(existingSale, currentItems);
        }

        @Override
        @Transactional
        public SaleResponse updateSaleItem(UUID saleId, UUID itemId, SaleItemRequest itemRequest) {
                User currentUser = securityContextHelper.getCurrentUserOrThrow();
                UUID currentUserRoleId = currentUser.getRole().getId();

                Sale existingSale = saleRepository.findById(saleId)
                                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

                SaleItem existingItem = saleItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Sale item not found with ID: " + itemId));

                // 1. Revertir stock del ítem antiguo
                Product oldProduct = productRepository.findById(existingItem.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found for ID: " + existingItem.getProductId()));
                oldProduct.increaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId); // Aumentar
                                                                                                              // stock
                                                                                                              // con la
                                                                                                              // cantidad
                                                                                                              // antigua
                productRepository.save(oldProduct);

                // 2. Actualizar detalles del ítem de venta
                existingItem.updateDetails(itemRequest.quantity(), itemRequest.unitPrice(), currentUser.getId(),
                                currentUserRoleId); // Changed to record access

                // 3. Aplicar nuevo stock
                Product newProduct = productRepository.findById(existingItem.getProductId()) // Podría ser el mismo
                                                                                             // producto
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found for ID: " + existingItem.getProductId()));
                newProduct.decreaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId); // Disminuir
                                                                                                              // stock
                                                                                                              // con la
                                                                                                              // nueva
                                                                                                              // cantidad
                productRepository.save(newProduct);

                // 4. Guardar el SaleItem actualizado
                saleItemRepository.save(existingItem);

                // 5. Registrar transacciones de inventario
                inventoryTransactionRepository.save(InventoryTransaction.createNew(
                                oldProduct.getId(),
                                TransactionType.ADJUSTMENT_IN, // Ajuste de entrada por reversión de cantidad antigua
                                existingItem.getQuantity(), // Cantidad antigua (negativa para reversión)
                                oldProduct.getCurrentStock(),
                                saleId,
                                "SALE_UPDATE_ITEM_OLD_QTY",
                                "Reverted old quantity for SaleItem " + itemId + " in Sale " + saleId,
                                currentUser.getId(),
                                currentUserRoleId));
                inventoryTransactionRepository.save(InventoryTransaction.createNew(
                                newProduct.getId(),
                                TransactionType.SALE_OUT, // Salida por nueva cantidad
                                existingItem.getQuantity(),
                                newProduct.getCurrentStock(),
                                saleId,
                                "SALE_UPDATE_ITEM_NEW_QTY",
                                "Applied new quantity for SaleItem " + itemId + " in Sale " + saleId,
                                currentUser.getId(),
                                currentUserRoleId));

                // 6. Recalcular totales de la venta
                List<SaleItem> currentItems = saleItemRepository.findAllBySaleId(saleId);
                BigDecimal totalAmount = currentItems.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

                existingSale.setTotalAmount(totalAmount.subtract(existingSale.getDiscountAmount()));
                existingSale.setTotalTaxAmount(totalTaxAmount);
                saleRepository.save(existingSale);

                return mapToSaleResponse(existingSale, currentItems);
        }

        @Override
        @Transactional
        public SaleResponse deleteSaleItem(UUID saleId, UUID itemId) {
                User currentUser = securityContextHelper.getCurrentUserOrThrow();
                UUID currentUserRoleId = currentUser.getRole().getId();

                Sale existingSale = saleRepository.findById(saleId)
                                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with ID: " + saleId));

                SaleItem existingItem = saleItemRepository.findById(itemId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Sale item not found with ID: " + itemId));

                // 1. Revertir stock del producto
                Product product = productRepository.findById(existingItem.getProductId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found for ID: " + existingItem.getProductId()));
                product.increaseStock(existingItem.getQuantity(), currentUser.getId(), currentUserRoleId);
                productRepository.save(product);

                // 2. Marcar el SaleItem como eliminado
                existingItem.markAsDeleted(currentUser.getId(), currentUserRoleId);
                saleItemRepository.save(existingItem);

                // 3. Registrar transacción de inventario de ajuste
                InventoryTransaction reverseTransaction = InventoryTransaction.createNew(
                                product.getId(),
                                TransactionType.ADJUSTMENT_IN, // Ajuste de entrada por reversión de ítem eliminado
                                existingItem.getQuantity(),
                                product.getCurrentStock(),
                                saleId,
                                "SALE_REMOVE_ITEM",
                                "Reversal for removed SaleItem " + itemId + " from Sale " + saleId,
                                currentUser.getId(),
                                currentUserRoleId);
                inventoryTransactionRepository.save(reverseTransaction);

                // 4. Recalcular totales de la venta
                List<SaleItem> currentItems = saleItemRepository.findAllBySaleId(saleId);
                BigDecimal totalAmount = currentItems.stream().map(SaleItem::getSubtotal).reduce(BigDecimal.ZERO,
                                BigDecimal::add);
                BigDecimal totalTaxAmount = BigDecimal.ZERO; // TODO: Implementar cálculo real de impuestos

                existingSale.setTotalAmount(totalAmount.subtract(existingSale.getDiscountAmount()));
                existingSale.setTotalTaxAmount(totalTaxAmount);
                saleRepository.save(existingSale);

                return mapToSaleResponse(existingSale, currentItems);
        }

        private SaleItem addSaleItemInternal(UUID saleId, SaleItemRequest itemRequest, UUID currentUserId,
                        UUID currentUserRoleId) {
                Product product = productRepository.findById(itemRequest.productId()) // Changed to record access
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found with ID: " + itemRequest.productId())); // Changed to
                                                                                                           // record
                                                                                                           // access

                if (product.getCurrentStock().compareTo(itemRequest.quantity()) < 0) { // Changed to record access
                        throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }

                SaleItem newSaleItem = SaleItem.createNew(
                                saleId,
                                product.getId(),
                                itemRequest.quantity(), // Changed to record access
                                itemRequest.unitPrice(), // Changed to record access
                                currentUserId,
                                currentUserRoleId);

                product.decreaseStock(itemRequest.quantity(), currentUserId, currentUserRoleId); // Changed to record
                                                                                                 // access
                productRepository.save(product);

                InventoryTransaction inventoryTransaction = InventoryTransaction.createNew(
                                product.getId(),
                                TransactionType.SALE_OUT,
                                itemRequest.quantity().negate(), // Changed to record access
                                product.getCurrentStock(),
                                saleId,
                                "SALE",
                                "Sale " + saleId + " - Product " + product.getName(),
                                currentUserId,
                                currentUserRoleId);
                inventoryTransactionRepository.save(inventoryTransaction);

                return saleItemRepository.save(newSaleItem);
        }

        private SaleResponse mapToSaleResponse(Sale sale, List<SaleItem> items) {
                Set<UUID> userIds = Stream.concat(
                                Stream.of(sale.getCreatedByUserId(), sale.getUpdatedByUserId(),
                                                sale.getDeletedByUserId()),
                                items.stream().flatMap(item -> Stream.of(item.getCreatedByUserId(),
                                                item.getUpdatedByUserId(), item.getDeletedByUserId())))
                                .filter(Objects::nonNull).collect(Collectors.toSet());

                Map<UUID, String> userNames = fetchUserNames(userIds);

                List<SaleItemResponse> itemResponses = items.stream()
                                .map(item -> new SaleItemResponse(
                                                item.getId(),
                                                item.getSaleId(),
                                                item.getProductId(),
                                                productRepository.findById(item.getProductId()).map(Product::getName)
                                                                .orElse("N/A"),
                                                productRepository.findById(item.getProductId()).map(Product::getSku)
                                                                .orElse("N/A"),
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
                                                userNames.get(item.getDeletedByUserId())))
                                .collect(Collectors.toList());

                String createdByName = userNames.get(sale.getCreatedByUserId());
                String updatedByName = userNames.get(sale.getUpdatedByUserId());
                String customerName = sale.getCustomerId() != null
                                ? customerRepository.findById(sale.getCustomerId()).map(Customer::getFullName)
                                                .orElse(null)
                                : null;

                // CORRECCIÓN: Usar el constructor adecuado para SaleResponse
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
                                customerName);
        }

        private Map<UUID, String> fetchUserNames(Set<UUID> userIds) {
                if (userIds.isEmpty()) {
                        return Map.of();
                }
                return userRepository.findAllById(userIds).stream()
                                .collect(Collectors.toMap(User::getId, User::getFullName));
        }
}
