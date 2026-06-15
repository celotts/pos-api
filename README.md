# POS API - Spring Boot & Hexagonal Architecture

Este proyecto es una API de Punto de Venta (POS) construida con Spring Boot, siguiendo un patrón de Arquitectura Hexagonal (Ports and Adapters). Su objetivo es proporcionar un backend robusto y escalable para la gestión de productos, inventario, ventas, compras, usuarios y transacciones financieras.

## Arquitectura

El proyecto está estructurado utilizando una Arquitectura Hexagonal, separando la lógica de negocio central de las preocupaciones externas como bases de datos y la interfaz de usuario. Esto promueve la mantenibilidad, la capacidad de prueba y la flexibilidad.

## Prerrequisitos

Para levantar y ejecutar este proyecto, necesitarás lo siguiente:

- **Java 17 JDK**: Asegúrate de tener el JDK 17 instalado.
- **Gradle**: Generalmente viene incluido con el proyecto a través del wrapper `gradlew`.
- **Podman**: Una alternativa a Docker para gestionar contenedores.
- **`podman-compose`**: Una herramienta similar a `docker-compose` para orquestar contenedores con Podman. Puedes instalarlo con `pip`:
  ```bash
  pip install podman-compose
  ```
- **DBeaver** (o cualquier cliente PostgreSQL): Para inspeccionar la base de datos.

## Primeros Pasos

### 1. Clonar el Repositorio

```bash
git clone <URL_DE_TU_REPO>
cd pos-api
```

### 2. Construir y Ejecutar el Entorno (Base de Datos y Aplicación)

Utiliza el `Makefile` proporcionado para gestionar el entorno de desarrollo.

Para construir la aplicación, crear la base de datos, inicializar el esquema, insertar el usuario administrador inicial y luego iniciar tanto la base de datos PostgreSQL como la aplicación Spring Boot:

```bash
make up
```

Este comando realizará las siguientes acciones:

- Construirá la imagen de la aplicación Spring Boot.
- Iniciará el contenedor de la base de datos PostgreSQL.
- Si el volumen de la base de datos está vacío (primera ejecución o después de `make down-volumes`), realizará:
  - La creación de la base de datos `pos_db` y el usuario `pos_user`.
  - La ejecución de `V1__initial_schema.sql` (crea tablas principales como `users` con el ENUM `role`).
  - La ejecución de `V2__add_pos_features.sql` (crea todas las tablas adicionales del POS).
  - La ejecución de `V3__insert_initial_admin_user.sql` (inserta el usuario administrador inicial).
- Iniciará el contenedor de la aplicación Spring Boot.

### 3. Comprobar Logs

Para monitorear los servicios:

```bash
make logs-app   # Para ver los logs de la aplicación
make logs-db    # Para ver los logs de la base de datos
```

### 4. Detener y Limpiar

Para detener y eliminar los contenedores y la red:

```bash
make down
```

Para detener, eliminar contenedores, red y **borrar todos los datos de la base de datos**:

```bash
make down-volumes
```

**¡Usa `make down-volumes` con precaución, ya que eliminará todos tus datos de la base de datos!**

### 5. Ejecutar solo la Aplicación

If the database container is already running, you can restart or run only the application:

```bash
make app-only
```

This command will fail if the database container is not running.

## Usuario Administrador Inicial

Un usuario administrador inicial se crea automáticamente cuando la base de datos se inicializa por primera vez. Puedes usar estas credenciales para acceder a los endpoints protegidos de la API.

- **Email:** `admin@posapi.com`
- **Contraseña:** `admin`

## Endpoints de la API (Ejemplo de Producto)

El proyecto incluye una API CRUD básica para la gestión de `Product`. Todos los endpoints requieren autenticación básica HTTP utilizando las credenciales del usuario administrador inicial.

- **Crear Producto:** `POST /products`
  ```bash
  curl -u admin@posapi.com:admin -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "PROD001",
    "name": "Sample Product",
    "description": "A description",
    "purchasePrice": 10.50,
    "salePrice": 15.99,
    "currentStock": 100.00,
    "taxId": null,
    "supplierId": null
  }'
  ```
- **Obtener Todos los Productos:** `GET /products`
  ```bash
  curl -u admin@posapi.com:admin http://localhost:8080/products
  ```
- **Obtener Producto por ID:** `GET /products/{id}`
  ```bash
  curl -u admin@posapi.com:admin http://localhost:8080/products/<PRODUCT_UUID>
  ```
- **Actualizar Producto:** `PUT /products/{id}`
  ```bash
  curl -u admin@posapi.com:admin -X PUT http://localhost:8080/products/<PRODUCT_UUID> \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "PROD001",
    "name": "Updated Product Name",
    "description": "Updated description",
    "purchasePrice": 11.00,
    "salePrice": 16.50,
    "currentStock": 99.00,
    "taxId": null,
    "supplierId": null
  }'
  ```
- **Eliminar Producto:** `DELETE /products/{id}`
  ```bash
  curl -u admin@posapi.com:admin -X DELETE http://localhost:8080/products/<PRODUCT_UUID>
  ```

## Acceso a la Base de Datos (DBeaver)

Para inspeccionar la base de datos con DBeaver (o cualquier otro cliente PostgreSQL):

- **Host:** `localhost`
- **Puerto:** `5432`
- **Base de Datos:** `pos_db`
- **Usuario:** `pos_user`
- **Contraseña:** `pos_password`

## Estructura del Proyecto

El proyecto sigue una Arquitectura Hexagonal, organizada por capas y luego por característica (por ejemplo, `product`, `user`).

```
src/main/java/com/posapi
├── application
│   └── service
│       ├── product  (ProductService)
│       └── user     (UserService)
├── domain
│   ├── model
│   │   ├── product  (Product)
│   │   └── user     (User)
│   └── repository
│       ├── product  (ProductRepository)
│       └── user     (UserRepository)
└── infrastructure
    ├── adapter
    │   ├── input
    │   │   └── rest
    │   │       ├── product  (ProductController, ProductRequest, ProductResponse)
    │   │       └── user     (User-related REST components)
    │   └── output
    │       └── persistence
    │           ├── product  (ProductJpaRepository, ProductRepositoryAdapter)
    │           └── user     (UserJpaRepository, UserRepositoryAdapter)
    ├── config       (SecurityConfig, UserDetailsServiceImpl)
    ├── exception    (GlobalExceptionHandler, ErrorResponse)
    └── persistence
        └── entity
            ├── product  (ProductEntity)
            └── user     (UserEntity, UserRole)
```

## Próximos Pasos

- Implementar endpoints de registro y login de usuarios.
- Añadir reglas de autorización (por ejemplo, solo ADMIN puede crear/actualizar productos).
- Implementar auditoría automática para `createdAt`, `updatedAt`, `createdByUserId`, etc.
- Continuar con otras entidades principales como `Customer` y `Supplier`.

## Configuración JWT

La API usa JWT para la autenticación. Añade las siguientes propiedades en `src/main/resources/application.yml` o como variables de entorno:

- `jwt.secret`: clave secreta para firmar tokens HMAC. Recomendamos usar una clave suficientemente larga y almacenarla en Base64. Por ejemplo, una clave HMAC-SHA256 segura puede generarse y codificarse en Base64.
- `jwt.expiration`: duración del token en milisegundos (por ejemplo `3600000` = 1 hora).

Ejemplo de configuración (en `src/main/resources/application.yml`):

```yaml
jwt:
  # Recomendado: almacena la clave en Base64. Ejemplo (no uses esta clave en producción):
  # secret: "dGhpc2lzYW5leGFtcGxlYmFzZTY0c2VjcmV0Zm9ySFRNQw=="
  # Si no usas Base64, la aplicación aceptará la cadena literal como bytes UTF-8.
  secret: 'clavesecretadebackendedeseguridadsuperlargade64bytes12345'
  # Duración del token en milisegundos (1h)
  expiration: 3600000
```

Notas:

- Es preferible usar una clave fuerte y almacenarla en un gestor de secretos en producción.
- Asegúrate de que la longitud de la clave sea adecuada para HMAC (p. ej. >= 256 bits para HS256).
- Si cambias el formato de la clave a Base64, actualiza la variable en el entorno que despliegues.
