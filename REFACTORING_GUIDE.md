# 📁 Guía de Estructura Refactorizada - POS API

## 🎯 Cambios Implementados

### 1. **Estructura por Capas + Características (Escalable)**

```
src/main/java/com/posapi/
├── shared/                          # 🔧 Código compartido entre features
│   ├── config/                      # Configuraciones (OpenAPI, Security, etc.)
│   │   └── OpenApiConfig.java       # Documentación automática Swagger/OpenAPI
│   ├── exception/                   # Manejo centralizado de excepciones
│   │   ├── ApplicationException.java        # Excepción base
│   │   ├── ResourceNotFoundException.java   # 404
│   │   ├── BadRequestException.java         # 400
│   │   ├── UnauthorizedException.java       # 401
│   │   ├── ForbiddenException.java          # 403
│   │   ├── ConflictException.java           # 409
│   │   ├── ErrorResponse.java               # Respuesta estándar de error
│   │   └── GlobalExceptionHandler.java      # ✅ NEW: Manejador global
│   ├── dto/                         # DTOs reutilizables
│   │   ├── ApiResponse.java         # ✅ NEW: Respuesta genérica
│   │   └── PageResponse.java        # ✅ NEW: Respuestas paginadas
│   └── mapper/                      # Mappers base (MapStruct)
│       └── EntityMapper.java        # ✅ NEW: Interface base para mappers
│
├── feature/                         # 🚀 Características del negocio
│   ├── user/
│   │   ├── application/             # Use cases / Servicios
│   │   ├── domain/                  # Lógica de negocio
│   │   └── infrastructure/          # Adaptadores (persistencia, REST)
│   ├── product/
│   ├── audit/
│   └── [otras features]
│
├── infrastructure/                  # 🔌 Infraestructura (existente, será refactorizada)
│   ├── adapter/
│   │   ├── input/  (REST Controllers - se moverán a features)
│   │   └── output/ (Persistencia - se moverá a features)
│   ├── aspect/
│   ├── config/
│   ├── filter/
│   ├── persistence/ (se moverá a features)
│   └── security/
│
├── domain/                          # 🎯 Lógica de dominio (existente, será refactorizada)
│   ├── exception/
│   ├── model/
│   ├── port/
│   └── service/
│
├── application/                     # 📱 Casos de uso (existente, será refactorizado)
│   └── port/
│       ├── input/
│       └── output/
│
└── config/                          # ⚙️ Configuraciones generales
```

### 2. **Nuevas Dependencias Agregadas**

```gradle
// MapStruct para mappers automáticos
implementation 'org.mapstruct:mapstruct:1.5.5.Final'
annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'

// OpenAPI/Swagger para documentación automática
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

// SpotBugs ACTIVADO (detecta bugs potenciales)
```

### 3. **Mejoramientos en Checkstyle**

✅ **Nuevas reglas agregadas:**
- ✅ Naming conventions (camelCase, UPPER_CASE)
- ✅ Límite de línea: 120 caracteres máximo
- ✅ Complejidad ciclomática: máximo 10
- ✅ Profundidad de anidamiento: máximo 4
- ✅ Máximo 20 parámetros por método
- ✅ Documentación JavaDoc obligatoria para métodos públicos
- ✅ Sin excepciones genéricas (Exception, Throwable, RuntimeException)
- ✅ Validación de equals/hashCode
- ✅ Y muchas más...

### 4. **Nueva Configuración de Testing**

**Archivo:** `src/test/resources/application.yml`
- Base de datos: **H2** (en memoria para tests rápidos)
- Aislamiento: Cada test crea/destruye esquema automáticamente
- Logging: Reducido para salida más clara
- No requiere Testcontainers (aunque disponible)

### 5. **OpenAPI/Swagger Automático**

**URL:** `http://localhost:8080/swagger-ui.html`
**Beneficios:**
- ✅ Documentación automática de todos los endpoints
- ✅ Interfaz interactiva para probar la API
- ✅ Definición JSON en `/v3/api-docs`
- ✅ Anotaciones @Schema para documentar request/response

### 6. **Manejo Global de Excepciones**

```java
// Ejemplo de uso
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Usuario no encontrado con ID: " + id
        ));
    return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", userMapper.toDTO(user)));
}
```

**Todas las excepciones generan ErrorResponse estándar:**
```json
{
  "status": 404,
  "message": "Usuario no encontrado con ID: 123",
  "timestamp": "2026-07-01T10:30:00Z",
  "path": "/api/v1/users/123"
}
```

---

## 🔄 Roadmap Siguiente

### Fase 1: Refactorización de Features (TODO)
```
Per feature (user, product, etc.):
1. Crear estructura feature/* con sus 3 capas
2. Migrar controllers a feature/*/infrastructure/adapter/input
3. Migrar servicios a feature/*/application
4. Migrar lógica de dominio a feature/*/domain
5. Migrar repositorios a feature/*/infrastructure/persistence
6. Crear DTOs específicos por feature
7. Crear mappers usando MapStruct
8. Crear tests de integración
```

### Fase 2: API Versioning (TODO)
```
Cambiar rutas de:
  /api/users        → /api/v1/users
  /api/products     → /api/v1/products
```

### Fase 3: Documentación (TODO)
```
1. Agregar @OpenAPIDefinition en features
2. Documentar todos los endpoints
3. Crear ejemplos en Swagger
```

---

## 📊 Validaciones Realizadas

✅ **Build Success** - Proyecto compila sin errores
✅ **Checkstyle Mejorado** - 50+ reglas de estilo
✅ **SpotBugs Activado** - Detecta bugs automáticamente
✅ **OpenAPI Configurado** - Swagger disponible en `/swagger-ui.html`
✅ **Testing Profile** - H2 para tests integrados
✅ **Excepciones Centralizadas** - GlobalExceptionHandler funcional
✅ **DTOs Creados** - ApiResponse, PageResponse listos para usar
✅ **Mappers Base** - EntityMapper interface lista para extensión

---

## 🎓 Cómo Usar la Nueva Estructura

### 1. **Crear una nueva Feature**

```bash
# 1. Crear estructura de directorios
mkdir -p src/main/java/com/posapi/feature/{nombre}/application
mkdir -p src/main/java/com/posapi/feature/{nombre}/domain  
mkdir -p src/main/java/com/posapi/feature/{nombre}/infrastructure

# 2. Crear controlador en REST adapter
# 3. Crear servicio de aplicación
# 4. Crear entities
# 5. Crear DTOs
# 6. Crear mapper (MapStruct)
# 7. Crear tests
```

### 2. **Crear un Mapper**

```java
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<User, UserDTO> {
    // MapStruct genera la implementación automáticamente
}
```

### 3. **Manejar Excepciones**

```java
User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

// Respuesta automática:
// 404 | {"status": 404, "message": "Usuario no encontrado", ...}
```

### 4. **Retornar Respuestas Estándar**

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable UUID id) {
    UserDTO user = userService.findById(id);
    return ResponseEntity.ok(ApiResponse.success("Usuario obtenido", user));
}

// Respuesta:
// 200 | {"success": true, "message": "Usuario obtenido", "data": {...}}
```

---

## 🔧 Próximos Pasos Recomendados

1. **Refactorizar features uno a uno** (User → Product → Audit)
2. **Ejecutar tests** para validar cambios
3. **Revisar Checkstyle** y aplicar correcciones
4. **Ejecutar SpotBugs** para detectar bugs
5. **Documentar endpoints** con @OpenAPI anotaciones
6. **Versionar API** (`/api/v1/...`)

---

**¿Necesitas ayuda migrando una feature específica?**
