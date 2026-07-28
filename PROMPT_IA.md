Prompt — Fase 0: Estabilización y Fundamentos
Actúa como un desarrollador Java Senior especializado en Spring Boot y pruebas unitarias. Necesito solucionar dos fallos de pruebas en mi proyecto con Arquitectura Hexagonal.

**Contexto y Estructura:**
- Proyecto: pos-api (Spring Boot + Gradle)
- Modificación de pruebas en `src/test/java/com/posapi/...`

**Objetivo:**
1. **Corregir `PosApiApplicationTests`:** Presenta un `ConflictingBeanDefinitionException`. Ajusta la anotación del test usando `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)` y `@TestPropertySource` para deshabilitar las configuraciones de base de datos de producción y el entorno web.
2. **Corregir `UserServiceTest`:** Presenta un `AssertionFailedError`. Re-aplica la instrucción `thenAnswer` en el mock de `userRepository.save()` para asegurar que devuelva el objeto `User` del dominio con su ID asignado correctamente durante el flujo del servicio.

Proporciona únicamente el código completo corregido para ambos archivos de prueba y las explicaciones técnicas necesarias para asegurar que `./gradlew test` pase al 100%.

Prompt — Fase 1: Categorización de Productos e Inventario Básico
Actúa como un desarrollador Java Senior con experiencia en Arquitectura Hexagonal y DDD. Vamos a implementar la categorización de tipo de producto en el módulo `product`.

**Contexto del Proyecto (`com.posapi`):**
- Dominio: `domain.model.product.Product`
- Adaptadores REST: DTOs `ProductRequest`, `ProductResponse` y mapper en `infrastructure.adapter.input.rest.product`
- Aplicación: `application.service.product.ProductService`
- Pruebas: `ProductServiceTest`

**Tareas a realizar:**
1. Crear el enum `ProductType` en `com.posapi.domain.model.product` con los valores: `RAW_MATERIAL`, `FINISHED_GOOD`.
2. Agregar el atributo `ProductType productType` en la entidad del dominio `Product` (con Lombok/Builder).
3. Modificar la entidad JPA `ProductEntity` (`infrastructure.adapter.output.persistence.entity.product`) mapeando el enum con `@Enumerated(EnumType.STRING)`.
4. Actualizar los DTOs (`ProductRequest`, `ProductResponse`), el mapper MapStruct/Manual correspondiente y el servicio `ProductService` para soportar este nuevo campo en las operaciones CRUD.
5. Actualizar la suite de pruebas `ProductServiceTest` para validar que el `productType` se asigne, persista y devuelva correctamente.

Entrega el código de los archivos modificados manteniendo la estricta separación de capas (Domain -> Port -> Service -> Infrastructure).

Prompt — Fase 2: Gestión de Proveedores y Órdenes de Compra
Actúa como un arquitecto de software backend Java. Vamos a implementar el módulo de Compras (`purchase`) respetando la Arquitectura Hexagonal de `pos-api`.

**Estructura y Mapeo del Proyecto:**
- Dominio (`com.posapi.domain`):
  - Modelos: `Supplier`, `PurchaseOrder` (estado: `PENDING`, `RECEIVED`), `PurchaseOrderItem`.
  - Puertos de salida: `PurchaseOrderRepository` (interfaz de dominio).
- Aplicación (`com.posapi.application`):
  - Servicio: `PurchaseService`
- Infraestructura (`com.posapi.infrastructure`):
  - Adaptadores Input REST: `PurchaseController`, DTOs (`PurchaseOrderRequest`, `PurchaseOrderResponse`), `PurchaseRestMapper`.
  - Adaptadores Output Persistencia: `PurchaseOrderEntity`, `PurchaseOrderItemEntity`, `PurchaseOrderJpaRepository`, `PurchasePersistenceAdapter`, `PurchasePersistenceMapper`.

**Reglas de Negocio:**
- `PurchaseService.createPurchaseOrder(...)`: Registra la orden con sus ítems en estado `PENDING`.
- `PurchaseService.receivePurchaseOrder(UUID id)`: Cambia el estado a `RECEIVED` e incrementa de forma **@Transactional** el campo `currentStock` de cada entidad `Product` involucrada en la orden.
- Incluir manejo de excepciones del dominio si la orden no existe o ya fue recibida (`ConflictException`, `ResourceNotFoundException`).

**Entregables:**
1. Los archivos de Dominio, Aplicación e Infraestructura necesarios.
2. Un test unitario completo para `PurchaseServiceTest` probando la recepción exitosa y la actualización del stock.

Prompt — Fase 3: Gestión de Clientes y Órdenes de Venta
Actúa como un desarrollador backend Java Senior. Vamos a implementar el módulo de Ventas (`sale`) con control transaccional de inventario.

**Requerimientos Arquitectónicos (Hexagonal):**
- Dominio (`com.posapi.domain`):
  - Modelos: `Customer`, `SaleOrder` (estado: `PENDING`, `COMPLETED`), `SaleOrderItem`.
  - Puertos de salida: Interfaces de repositorio para `Customer` y `SaleOrder`.
- Aplicación (`com.posapi.application.service.sale`):
  - Servicio: `SaleService`.
- Infraestructura:
  - Controlador REST `SaleController` y DTOs correspondientes.
  - Entidades JPA, Mappers y Adaptadores de Persistencia.

**Lógica de Negocio Crucial:**
- `createSaleOrder(...)`: Crea la orden en borrador.
- `completeSaleOrder(UUID saleOrderId)`:
  1. Valida que `Product.currentStock >= quantityRequested` para cada ítem. Si no hay suficiente stock, debe lanzar una `InvariantException` o `BadRequestException`.
  2. Si hay stock suficiente, descuenta las unidades de `Product.currentStock` y cambia el estado de la venta a `COMPLETED`. Toda esta operación debe ser **@Transactional**.

**Entregables:**
1. Código fuente modularizado por capas.
2. Pruebas unitarias en `SaleServiceTest` cubriendo el caso exitoso (descuento de stock) y el caso de fallo (stock insuficiente).

Prompt — Fase 4: Reportes Básicos de Inventario y Auditoría
Actúa como un desarrollador Java Senior. Vamos a implementar el historial y trazabilidad de movimientos de inventario (`InventoryMovement`) en el proyecto `pos-api`.

**Requerimientos:**
1. **Entidad del Dominio:** `InventoryMovement` con campos: `id`, `productId`, `quantityChange` (positivo para entradas, negativo para salidas), `movementType` (enum: `PURCHASE_RECEIPT`, `SALE_DISPATCH`, `ADJUSTMENT`), `timestamp`, y `relatedDocumentId` (UUID de la compra/venta).
2. **Auditoría Transaccional:**
   - Modificar `PurchaseService` para registrar automáticamente un `InventoryMovement` con tipo `PURCHASE_RECEIPT` (+cantidad) al recibir una compra.
   - Modificar `SaleService` para registrar automáticamente un `InventoryMovement` con tipo `SALE_DISPATCH` (-cantidad) al completar una venta.
3. **Adaptador REST:**
   - Exponer en `ProductController` (o un nuevo `InventoryController` en `infrastructure.adapter.input.rest.inventory`) endpoints para:
     a) Consultar stock actual filtrado por `ProductType`.
     b) Consultar el historial de movimientos de un producto por su `productId`.

Proporciona el código modular respetando los mapeos entre entidades de persistencia JPA y entidades de dominio.
