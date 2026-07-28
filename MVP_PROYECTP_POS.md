# Plan de Construcción MVP: Sistema POS e Inventario

**Meta del MVP:** Permitir el registro de productos (materias primas y terminados), gestionar usuarios, registrar transacciones de compra y venta que impacten directamente el inventario, y consultar el stock actual en tiempo real.

---
## Actúa como un desarrollador Java Senior especializado en Spring Boot y pruebas unitarias. Necesito solucionar dos fallos de pruebas en mi proyecto con Arquitectura Hexagonal.
## Fase 0: Estabilización y Fundamentos
* **Prioridad:** Alta
* **Objetivo:** Asegurar la estabilidad del proyecto, garantizando que todas las pruebas existentes pasen consistentemente y que la aplicación se inicie sin errores dentro del entorno containerizado (Podman). Esta fase establece la base para cualquier desarrollo futuro.
* **Estado Actual:** Existen fallos en `PosApiApplicationTests` y `UserServiceTest` que deben ser resueltos antes de avanzar.
* **Entregable:** Proyecto estable, suite de pruebas unitarias aprobada en su totalidad y aplicación iniciando correctamente.

### Tareas
1. **Resolver `ConflictingBeanDefinitionException` en `PosApiApplicationTests`**
   * *Acción:* Re-aplicar la configuración en `src/test/java/com/posapi/PosApiApplicationTests.java` utilizando `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)` y `@TestPropertySource` para deshabilitar la base de datos de producción y el entorno web durante la ejecución de los tests.
2. **Resolver `AssertionFailedError` en `UserServiceTest`**
   * *Acción:* Re-aplicar la corrección en `src/test/java/com/posapi/application/service/user/UserServiceTest.java` para asegurar que la instrucción `thenAnswer` del mock de `userRepository.save()` devuelva el objeto `User` con el ID correctamente asignado por el servicio.
3. **Ejecutar y validar la suite de pruebas**
   * *Acción:* Ejecutar el comando `make clean` seguido de `./gradlew test`. Confirmar que todas las pruebas unitarias (`ProductServiceTest`, `UserServiceTest`, `PosApiApplicationTests`) pasen al 100%.
4. **Confirmar inicio de la aplicación en Podman**
   * *Acción:* Ejecutar `make up` y verificar a través de los logs (`make logs-app`) que la aplicación levanta sin excepciones ni errores de configuración.

---

## Actúa como un desarrollador Java Senior con experiencia en Arquitectura Hexagonal y DDD. Vamos a implementar la categorización de tipo de producto en el módulo `product`.
## Fase 1: Categorización de Productos e Inventario Básico
* **Prioridad:** Alta
* **Objetivo:** Diferenciar claramente entre Materias Primas y Productos Terminados dentro de la entidad `Product`, preparando la estructura de inventario para las transacciones.
* **Estado Actual:** La entidad `Product` contiene el atributo `currentStock`, pero carece de una clasificación por tipo.
* **Entregable:** Catálogo de productos con tipo definido y `ProductService` actualizado con la nueva lógica.

## Tareas
1. **Modificar Entidad `Product` (`com.posapi.domain.model.product.Product`)**
   * *Acción:* Añadir el campo `productType` utilizando una enumeración adecuada (ej. `enum ProductType { RAW_MATERIAL, FINISHED_GOOD }`).
2. **Actualizar DTOs de Producto (`ProductRequest`, `ProductResponse`)**
   * *Acción:* Incluir el atributo `productType` en las estructuras de solicitud y respuesta para la creación, edición y lectura de productos.
3. **Actualizar Capa de Servicio (`ProductService`)**
   * *Acción:* Ajustar la lógica de los métodos CRUD para procesar, validar y mapear correctamente el campo `productType`.
4. **Actualizar Cobertura de Pruebas (`ProductServiceTest`)**
   * *Acción:* Implementar y adaptar casos de prueba unitarios para validar que la asignación y persistencia de `productType` se realice según lo esperado.

---

## Actúa como un arquitecto de software backend Java. Vamos a implementar el módulo de Compras (`purchase`) respetando la Arquitectura Hexagonal de `pos-api`.
## Fase 2: Gestión de Proveedores y Órdenes de Compra
* **Prioridad:** Alta
* **Objetivo:** Permitir el registro de proveedores, la creación de órdenes de compra (materias primas/productos) y el incremento automático del inventario al recibir la mercancía.
* **Estado Actual:** No existen entidades ni casos de uso implementados para el flujo de compras.
* **Entregable:** Flujo end-to-end para la recepción de compras e incremento transaccional de stock.

### Tareas
1. **Crear Entidad `Supplier` (`com.posapi.domain.model.supplier.Supplier`)**
   * *Atributos:* `id`, `name`, `contactInfo`, `createdAt`, `updatedAt`.
2. **Crear Persistencia para Proveedores (`SupplierRepository`)**
   * *Acción:* Definir la interfaz JPA correspondiente para el acceso a datos de `Supplier`.
3. **Crear Entidad `PurchaseOrder` (`com.posapi.domain.model.purchase.PurchaseOrder`)**
   * *Atributos:* `id`, `supplierId` (referencia a `Supplier`), `orderDate`, `status` (ej. `PENDING`, `RECEIVED`), `totalAmount`, `createdAt`, `updatedAt`.
4. **Crear Entidad `PurchaseOrderItem` (`com.posapi.domain.model.purchase.PurchaseOrderItem`)**
   * *Atributos:* `id`, `purchaseOrderId` (referencia a `PurchaseOrder`), `productId` (referencia a `Product`), `quantity`, `unitPrice`.
5. **Crear Repositorios de Compras**
   * *Acción:* Crear las interfaces `PurchaseOrderRepository` y `PurchaseOrderItemRepository`.
6. **Implementar Servicio de Compras (`PurchaseService`)**
   * *Ubicación:* `com.posapi.application.service.purchase.PurchaseService`
   * *Método `createPurchaseOrder(PurchaseOrderRequest)`:* Registra la orden de compra junto con su detalle de ítems.
   * *Método `receivePurchaseOrder(UUID purchaseOrderId)`:* Modifica el estado a `RECEIVED` e incrementa de forma **transaccional** el campo `Product.currentStock` para cada ítem de la orden.
7. **Exponer Adaptador REST (`PurchaseController`)**
   * *Ubicación:* `com.posapi.infrastructure.adapter.input.rest.purchase.PurchaseController`
   * *Acción:* Crear los endpoints REST para registrar, consultar y marcar como recibidas las órdenes de compra.
8. **Pruebas Unitarias**
   * *Acción:* Construir `SupplierServiceTest` y `PurchaseServiceTest` cubriendo escenarios de éxito y manejo de errores.

---

## Actúa como un desarrollador backend Java Senior. Vamos a implementar el módulo de Ventas (`sale`) con control transaccional de inventario.
## Fase 3: Gestión de Clientes y Órdenes de Venta
* **Prioridad:** Alta
* **Objetivo:** Registrar clientes, procesar órdenes de venta de productos terminados y actualizar el inventario deduciendo las existencias al completar la transacción.
* **Estado Actual:** No existen entidades ni lógica de negocio desarrolladas para el módulo de ventas.
* **Entregable:** Módulo funcional de ventas con validación estricta de existencias y descuento transaccional de inventario.

### Tareas
1. **Crear Entidad `Customer` (`com.posapi.domain.model.customer.Customer`)**
   * *Atributos:* `id`, `name`, `contactInfo`, `createdAt`, `updatedAt`.
2. **Crear Persistencia para Clientes (`CustomerRepository`)**
   * *Acción:* Definir la interfaz JPA para la entidad `Customer`.
3. **Crear Entidad `SaleOrder` (`com.posapi.domain.model.sale.SaleOrder`)**
   * *Atributos:* `id`, `customerId` (referencia a `Customer`), `orderDate`, `status` (ej. `PENDING`, `COMPLETED`), `totalAmount`, `createdAt`, `updatedAt`.
4. **Crear Entidad `SaleOrderItem` (`com.posapi.domain.model.sale.SaleOrderItem`)**
   * *Atributos:* `id`, `saleOrderId` (referencia a `SaleOrder`), `productId` (referencia a `Product`), `quantity`, `unitPrice`.
5. **Crear Repositorios de Ventas**
   * *Acción:* Definir las interfaces `SaleOrderRepository` y `SaleOrderItemRepository`.
6. **Implementar Servicio de Ventas (`SaleService`)**
   * *Ubicación:* `com.posapi.application.service.sale.SaleService`
   * *Método `createSaleOrder(SaleOrderRequest)`:* Crea la orden de venta en estado borrador/pendiente con sus ítems.
   * *Método `completeSaleOrder(UUID saleOrderId)`:* Marca la orden como `COMPLETED` y descuenta transaccionalmente el stock (`Product.currentStock`). Incluye validación de negocio para prevenir ventas sin disponibilidad suficiente.
7. **Exponer Adaptador REST (`SaleController`)**
   * *Ubicación:* `com.posapi.infrastructure.adapter.input.rest.sale.SaleController`
   * *Acción:* Definir endpoints para la creación, consulta y finalización de ventas.
8. **Pruebas Unitarias**
   * *Acción:* Crear `CustomerServiceTest` y `SaleServiceTest` (validando escenarios con y sin stock disponible).

---

## Actúa como un desarrollador Java Senior. Vamos a implementar el historial y trazabilidad de movimientos de inventario (`InventoryMovement`) en el proyecto `pos-api`.
## Fase 4: Reportes Básicos de Inventario y Auditoría
* **Prioridad:** Media
* **Objetivo:** Brindar visibilidad completa sobre las existencias actuales de mercancía y establecer la trazabilidad de los movimientos de almacén.
* **Estado Actual:** Atributo `Product.currentStock` existente pero sin endpoints de consulta especializada o historial de movimientos.
* **Entregable:** Reporte de existencias filtrable y trazabilidad histórica de entradas y salidas de almacén.

### Tareas
1. **Endpoint de Consulta de Inventario**
   * *Acción:* Exponer un endpoint en `ProductController` (o un nuevo `InventoryController`) que retorne la lista de productos con su stock actual, incluyendo filtros opcionales por `ProductType`.
2. **Registro Histórico de Movimientos (`InventoryMovement`)**
   * *Creación de Entidad:* Implementar `InventoryMovement` con los campos: `productId`, `quantityChange`, `movementType` (ej. `PURCHASE_RECEIPT`, `SALE_DISPATCH`, `ADJUSTMENT`), `timestamp`, y `relatedDocumentId` (referencia a la orden de compra o venta).
   * *Integración Transaccional:* Invocar la creación del registro de `InventoryMovement` dentro de `PurchaseService` (al recibir compras) y `SaleService` (al completar ventas).

```text
.
├── Containerfile
├── Dockerfile
├── HELP.md
├── Makefile
├── README.md
├── REFACTORING_GUIDE.md
├── Task
├── backend_documentation_1.md
├── bin
│   ├── default
│   ├── generated-sources
│   │   └── annotations
│   ├── generated-test-sources
│   │   └── annotations
│   ├── main
│   │   ├── application-docker.yml
│   │   ├── application.yml
│   │   ├── com
│   │   │   └── posapi
│   │   │       ├── PosApiApplication.class
│   │   │       ├── application
│   │   │       │   ├── port
│   │   │       │   │   ├── category
│   │   │       │   │   │   ├── CategoryInputPort.class
│   │   │       │   │   │   └── CategoryManagementPort.class
│   │   │       │   │   ├── product
│   │   │       │   │   │   ├── ProductInputPort.class
│   │   │       │   │   │   └── ProductManagementPort.class
│   │   │       │   │   ├── role
│   │   │       │   │   │   └── RoleManagementPort.class
│   │   │       │   │   ├── secondary
│   │   │       │   │   │   └── PasswordEncoderPort.class
│   │   │       │   │   ├── supplier
│   │   │       │   │   │   └── SupplierManagementPort.class
│   │   │       │   │   ├── tax
│   │   │       │   │   │   └── TaxManagementPort.class
│   │   │       │   │   └── user
│   │   │       │   │       └── UserManagementPort.class
│   │   │       │   └── service
│   │   │       │       ├── audit
│   │   │       │       │   └── AuditLogService.class
│   │   │       │       ├── auth
│   │   │       │       │   └── AuthenticationService.class
│   │   │       │       ├── bootstrap
│   │   │       │       │   └── BootstrapService.class
│   │   │       │       ├── category
│   │   │       │       │   └── CategoryService.class
│   │   │       │       ├── jwt
│   │   │       │       │   └── JwtService.class
│   │   │       │       ├── product
│   │   │       │       │   └── ProductService.class
│   │   │       │       ├── role
│   │   │       │       │   └── RoleService.class
│   │   │       │       ├── supplier
│   │   │       │       │   └── SupplierService.class
│   │   │       │       ├── tax
│   │   │       │       │   └── TaxService.class
│   │   │       │       └── user
│   │   │       │           └── UserService.class
│   │   │       ├── domain
│   │   │       │   ├── exception
│   │   │       │   │   ├── ApplicationException.class
│   │   │       │   │   ├── BadRequestException.class
│   │   │       │   │   ├── ConfigurationException.class
│   │   │       │   │   ├── ConflictException.class
│   │   │       │   │   ├── DuplicateResourceException.class
│   │   │       │   │   ├── ForbiddenException.class
│   │   │       │   │   ├── InvariantException.class
│   │   │       │   │   ├── ResourceNotFoundException.class
│   │   │       │   │   └── UnauthorizedException.class
│   │   │       │   ├── model
│   │   │       │   │   ├── audit
│   │   │       │   │   │   ├── AuditAction.class
│   │   │       │   │   │   ├── AuditLog$AuditLogBuilder.class
│   │   │       │   │   │   └── AuditLog.class
│   │   │       │   │   ├── category
│   │   │       │   │   │   ├── Category$CategoryBuilder.class
│   │   │       │   │   │   └── Category.class
│   │   │       │   │   ├── product
│   │   │       │   │   │   ├── Product$ProductBuilder.class
│   │   │       │   │   │   └── Product.class
│   │   │       │   │   ├── role
│   │   │       │   │   │   ├── Role$RoleBuilder.class
│   │   │       │   │   │   └── Role.class
│   │   │       │   │   ├── supplier
│   │   │       │   │   │   ├── Supplier$SupplierBuilder.class
│   │   │       │   │   │   └── Supplier.class
│   │   │       │   │   ├── tax
│   │   │       │   │   │   ├── Tax$TaxBuilder.class
│   │   │       │   │   │   ├── Tax$TaxCategory.class
│   │   │       │   │   │   ├── Tax.class
│   │   │       │   │   │   └── TaxEnum.class
│   │   │       │   │   └── user
│   │   │       │   │       ├── User$UserBuilder.class
│   │   │       │   │       ├── User.class
│   │   │       │   │       └── UserRole.class
│   │   │       │   └── port
│   │   │       │       └── output
│   │   │       │           ├── AuditLogRepository.class
│   │   │       │           ├── CategoryRepository.class
│   │   │       │           ├── PasswordEncoderPort.class
│   │   │       │           ├── ProductRepository.class
│   │   │       │           ├── RoleRepository.class
│   │   │       │           ├── SupplierRepository.class
│   │   │       │           ├── TaxRepository.class
│   │   │       │           └── UserRepository.class
│   │   │       ├── infrastructure
│   │   │       │   ├── adapter
│   │   │       │   │   ├── input
│   │   │       │   │   │   └── rest
│   │   │       │   │   │       ├── auth
│   │   │       │   │   │       │   ├── AuthController.class
│   │   │       │   │   │       │   └── dto
│   │   │       │   │   │       │       ├── LoginRequest.class
│   │   │       │   │   │       │       └── LoginResponse.class
│   │   │       │   │   │       ├── category
│   │   │       │   │   │       │   ├── CategoryController.class
│   │   │       │   │   │       │   ├── dto
│   │   │       │   │   │       │   │   ├── CategoryRequest.class
│   │   │       │   │   │       │   │   └── CategoryResponse.class
│   │   │       │   │   │       │   └── mapper
│   │   │       │   │   │       │       └── CategoryRestMapper.class
│   │   │       │   │   │       ├── error
│   │   │       │   │   │       │   ├── ErrorResponse$ErrorResponseBuilder.class
│   │   │       │   │   │       │   ├── ErrorResponse$FieldError$FieldErrorBuilder.class
│   │   │       │   │   │       │   ├── ErrorResponse$FieldError.class
│   │   │       │   │   │       │   └── ErrorResponse.class
│   │   │       │   │   │       ├── product
│   │   │       │   │   │       │   ├── ProductController.class
│   │   │       │   │   │       │   ├── dto
│   │   │       │   │   │       │   │   ├── ProductRequest.class
│   │   │       │   │   │       │   │   ├── ProductResponse$ProductResponseBuilder.class
│   │   │       │   │   │       │   │   └── ProductResponse.class
│   │   │       │   │   │       │   └── mapper
│   │   │       │   │   │       │       └── ProductRestMapper.class
│   │   │       │   │   │       ├── role
│   │   │       │   │   │       │   ├── RoleController.class
│   │   │       │   │   │       │   ├── dto
│   │   │       │   │   │       │   │   ├── RoleRequest.class
│   │   │       │   │   │       │   │   ├── RoleResponse$RoleResponseBuilder.class
│   │   │       │   │   │       │   │   └── RoleResponse.class
│   │   │       │   │   │       │   └── mapper
│   │   │       │   │   │       │       └── RoleRestMapper.class
│   │   │       │   │   │       ├── supplier
│   │   │       │   │   │       │   ├── SupplierController.class
│   │   │       │   │   │       │   ├── dto
│   │   │       │   │   │       │   │   ├── SupplierRequest.class
│   │   │       │   │   │       │   │   └── SupplierResponse.class
│   │   │       │   │   │       │   └── mapper
│   │   │       │   │   │       │       └── SupplierRestMapper.class
│   │   │       │   │   │       ├── tax
│   │   │       │   │   │       │   ├── TaxController.class
│   │   │       │   │   │       │   ├── dto
│   │   │       │   │   │       │   │   ├── TaxRequest.class
│   │   │       │   │   │       │   │   └── TaxResponse.class
│   │   │       │   │   │       │   └── mapper
│   │   │       │   │   │       │       └── TaxRestMapper.class
│   │   │       │   │   │       └── user
│   │   │       │   │   │           ├── UserController.class
│   │   │       │   │   │           ├── dto
│   │   │       │   │   │           │   ├── UserRequest$UserRequestBuilder.class
│   │   │       │   │   │           │   ├── UserRequest.class
│   │   │       │   │   │           │   ├── UserResponse$UserResponseBuilder.class
│   │   │       │   │   │           │   └── UserResponse.class
│   │   │       │   │   │           └── mapper
│   │   │       │   │   │               └── UserRestMapper.class
│   │   │       │   │   └── output
│   │   │       │   │       └── persistence
│   │   │       │   │           ├── adapter
│   │   │       │   │           │   ├── audit
│   │   │       │   │           │   │   └── AuditLogPersistenceAdapter.class
│   │   │       │   │           │   ├── category
│   │   │       │   │           │   │   └── CategoryPersistenceAdapter.class
│   │   │       │   │           │   ├── product
│   │   │       │   │           │   │   └── ProductPersistenceAdapter.class
│   │   │       │   │           │   ├── role
│   │   │       │   │           │   │   └── RolePersistenceAdapter.class
│   │   │       │   │           │   ├── supplier
│   │   │       │   │           │   │   └── SupplierPersistenceAdapter.class
│   │   │       │   │           │   ├── tax
│   │   │       │   │           │   │   └── TaxPersistenceAdapter.class
│   │   │       │   │           │   └── user
│   │   │       │   │           │       └── UserPersistenceAdapter.class
│   │   │       │   │           ├── entity
│   │   │       │   │           │   ├── audit
│   │   │       │   │           │   │   ├── AuditLogEntity$AuditLogEntityBuilder.class
│   │   │       │   │           │   │   └── AuditLogEntity.class
│   │   │       │   │           │   ├── category
│   │   │       │   │           │   │   ├── CategoryEntity$CategoryEntityBuilder.class
│   │   │       │   │           │   │   └── CategoryEntity.class
│   │   │       │   │           │   ├── product
│   │   │       │   │           │   │   ├── ProductEntity$ProductEntityBuilder.class
│   │   │       │   │           │   │   └── ProductEntity.class
│   │   │       │   │           │   ├── role
│   │   │       │   │           │   │   ├── RoleEntity$RoleEntityBuilder.class
│   │   │       │   │           │   │   └── RoleEntity.class
│   │   │       │   │           │   ├── supplier
│   │   │       │   │           │   │   ├── SupplierEntity$SupplierEntityBuilder.class
│   │   │       │   │           │   │   └── SupplierEntity.class
│   │   │       │   │           │   ├── tax
│   │   │       │   │           │   │   ├── TaxEntity$TaxEntityBuilder.class
│   │   │       │   │           │   │   ├── TaxEntity$TaxType.class
│   │   │       │   │           │   │   └── TaxEntity.class
│   │   │       │   │           │   └── user
│   │   │       │   │           │       ├── UserEntity$UserEntityBuilder.class
│   │   │       │   │           │       ├── UserEntity.class
│   │   │       │   │           │       └── UserRole.class
│   │   │       │   │           ├── mapper
│   │   │       │   │           │   ├── audit
│   │   │       │   │           │   │   └── AuditLogPersistenceMapper.class
│   │   │       │   │           │   ├── category
│   │   │       │   │           │   │   └── CategoryPersistenceMapper.class
│   │   │       │   │           │   ├── product
│   │   │       │   │           │   │   └── ProductPersistenceMapper.class
│   │   │       │   │           │   ├── role
│   │   │       │   │           │   │   └── RolePersistenceMapper.class
│   │   │       │   │           │   ├── supplier
│   │   │       │   │           │   │   └── SupplierPersistenceMapper.class
│   │   │       │   │           │   ├── tax
│   │   │       │   │           │   │   └── TaxPersistenceMapper.class
│   │   │       │   │           │   └── user
│   │   │       │   │           │       └── UserPersistenceMapper.class
│   │   │       │   │           └── repository
│   │   │       │   │               ├── audit
│   │   │       │   │               │   └── AuditLogJpaRepository.class
│   │   │       │   │               ├── category
│   │   │       │   │               │   └── CategoryJpaRepository.class
│   │   │       │   │               ├── product
│   │   │       │   │               │   └── ProductJpaRepository.class
│   │   │       │   │               ├── role
│   │   │       │   │               │   └── RoleJpaRepository.class
│   │   │       │   │               ├── supplier
│   │   │       │   │               │   └── SupplierJpaRepository.class
│   │   │       │   │               ├── tax
│   │   │       │   │               │   └── TaxJpaRepository.class
│   │   │       │   │               └── user
│   │   │       │   │                   └── UserJpaRepository.class
│   │   │       │   ├── aspect
│   │   │       │   │   ├── AuditAspect.class
│   │   │       │   │   ├── Auditable.class
│   │   │       │   │   └── LoggableAction.class
│   │   │       │   ├── config
│   │   │       │   │   ├── AsyncConfig.class
│   │   │       │   │   └── AuditConfig.class
│   │   │       │   ├── filter
│   │   │       │   │   └── RequestCachingFilter.class
│   │   │       │   └── security
│   │   │       │       ├── CustomUserDetails.class
│   │   │       │       ├── JwtAuthenticationEntryPoint.class
│   │   │       │       ├── JwtRequestFilter.class
│   │   │       │       ├── JwtUtil.class
│   │   │       │       ├── SecurityConfig.class
│   │   │       │       ├── SecurityContextHelper.class
│   │   │       │       ├── UserDetailsProvider.class
│   │   │       │       └── UserSecurity.class
│   │   │       └── shared
│   │   │           ├── dto
│   │   │           │   ├── ApiResponse.class
│   │   │           │   └── PageResponse.class
│   │   │           ├── exception
│   │   │           │   ├── ApplicationException.class
│   │   │           │   ├── BadRequestException.class
│   │   │           │   ├── ConflictException.class
│   │   │           │   ├── ErrorResponse.class
│   │   │           │   ├── ForbiddenException.class
│   │   │           │   ├── GlobalExceptionHandler.class
│   │   │           │   ├── ResourceNotFoundException.class
│   │   │           │   └── UnauthorizedException.class
│   │   │           └── mapper
│   │   │               └── EntityMapper.class
│   │   ├── db
│   │   │   └── migration
│   │   └── docker
│   │       └── init-db.sh
│   └── test
│       ├── application.yml
│       └── com
│           └── posapi
│               ├── PosApiApplicationTests$TestConfig.class
│               ├── PosApiApplicationTests.class
│               ├── application
│               │   └── service
│               │       ├── product
│               │       │   └── ProductServiceTest.class
│               │       └── user
│               │           └── UserServiceTest.class
│               └── infrastructure
│                   ├── adapter
│                   │   └── input
│                   │       └── rest
│                   │           ├── product
│                   │           └── user
│                   │               └── UserControllerTest.class
│                   └── security
│                       └── JwtUtilTest.class
├── build.gradle
├── config
│   ├── checkstyle
│   │   └── checkstyle.xml
│   ├── config.yml
│   └── spotbugs
│       └── exclude.xml
├── dryrun.log
└── front
    ├── index.html
    └── node_modules
```
