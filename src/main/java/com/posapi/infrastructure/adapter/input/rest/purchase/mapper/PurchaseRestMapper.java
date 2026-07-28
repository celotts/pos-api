package com.posapi.infrastructure.adapter.input.rest.purchase.mapper;

import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseResponse;
// CORREGIDO: Importación correcta para PurchaseItemRequest
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemRequest;
import com.posapi.infrastructure.adapter.input.rest.purchase.dto.PurchaseItemResponse;
import com.posapi.shared.dto.PageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PurchaseRestMapper {

    private final PurchaseItemRestMapper purchaseItemRestMapper;

    public PurchaseRestMapper(PurchaseItemRestMapper purchaseItemRestMapper) {
        this.purchaseItemRestMapper = purchaseItemRestMapper;
    }

    public PurchaseRequest toApplicationPurchaseRequest(PurchaseRequest restPurchaseRequest) {
        // Usar .items() para acceder a la lista de ítems del record PurchaseRequest
        if (restPurchaseRequest.items() != null) {
            List<PurchaseItemRequest> applicationItems = restPurchaseRequest.items().stream()
                    .map(purchaseItemRestMapper::toApplicationPurchaseItemRequest)
                    .collect(Collectors.toList());
            // Como PurchaseRequest es un record inmutable, si se modifican los ítems,
            // se debería crear una nueva instancia del record con los ítems actualizados.
            // Por ahora, como el mapper de ítems devuelve el mismo tipo, y el puerto espera el record,
            // asumimos que el record original es suficiente si los ítems no cambian de tipo.
            // Si los ítems se modificaran en tipo, necesitaríamos un constructor de copia o un builder para PurchaseRequest.
        }
        return restPurchaseRequest; // Devolvemos el mismo record inmutable
    }

    public PurchaseResponse toRestPurchaseResponse(PurchaseResponse applicationPurchaseResponse) {
        // Usar .items() para acceder a la lista de ítems del record PurchaseResponse
        if (applicationPurchaseResponse.items() != null) {
            List<PurchaseItemResponse> restItems = applicationPurchaseResponse.items().stream()
                    .map(purchaseItemRestMapper::toRestPurchaseItemResponse)
                    .toList();
            // Similar al caso de PurchaseRequest, si PurchaseResponse es un record inmutable,
            // no se puede llamar a setItems(). Si el mapeo de ítems implica un cambio,
            // se debería devolver una nueva instancia de PurchaseResponse.
            // Para este caso, asumimos que el PurchaseResponse de la capa de aplicación es el mismo record.
        }
        return applicationPurchaseResponse; // Devolvemos el mismo record inmutable
    }

    public List<PurchaseResponse> toRestPurchaseResponseList(List<PurchaseResponse> applicationPurchaseResponseList) {
        return applicationPurchaseResponseList.stream()
                .map(this::toRestPurchaseResponse)
                .collect(Collectors.toList());
    }

    public PageResponse<PurchaseResponse> toRestPageResponse(PageResponse<PurchaseResponse> applicationPageResponse) {
        // Usar los métodos de acceso correctos para los componentes del record PageResponse
        List<PurchaseResponse> restContent = applicationPageResponse.content().stream()
                .map(this::toRestPurchaseResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(restContent,
                applicationPageResponse.pageNumber(),
                applicationPageResponse.pageSize(),
                applicationPageResponse.totalElements(),
                applicationPageResponse.totalPages(),
                applicationPageResponse.isLast());
    }
}
