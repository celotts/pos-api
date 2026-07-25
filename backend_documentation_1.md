# Documentación de la Estructura del Backend `pos-api`

### **1. Arquitectura General: Capas y Responsabilidades**

Tu proyecto `pos-api` está organizado en tres capas principales, siguiendo los principios de la Arquitectura Limpia (Clean Architecture) o Hexagonal. Esta separación garantiza una alta cohesión dentro de cada capa y un bajo acoplamiento entre ellas, facilitando el mantenimiento, la escalabilidad y la testabilidad.

*   **`com.posapi.domain` (Capa de Dominio / Core de Negocio)**
    *   **Propósito**: Es el corazón de la aplicación. Contiene la lógica de negocio pura, las entidades (modelos de dominio) y las interfaces (puertos) que definen cómo el dominio interactúa con el mundo exterior (bases de datos, APIs externas, etc.). Es la capa más interna y no debe depender de ninguna otra capa.
    *   **Independencia**: Es completamente independiente de frameworks, bases de datos o interfaces de usuario.
    *   **Archivos clave**:
        *   `model`: Clases que representan las entidades de negocio (ej. `User.java`, `Role.java`, `Product.java`).
        *   `port.input`: Interfaces que definen los casos de uso que la capa de aplicación debe implementar (ej. `AuthManagementPort.java`).
        *   `port.output`: Interfaces que definen las operaciones que la capa de infraestructura debe implementar para interactuar con recursos externos (ej. `UserRepository.java`, `ProductRepository.java`).

*   **`com.posapi.application` (Capa de Aplicación / Orquestación)**
    *   **Propósito**: Contiene la lógica de orquestación de la aplicación. Implementa los casos de uso (use cases) definidos por la capa de dominio. Actúa como un coordinador, dirigiendo el flujo de datos entre el dominio y la infraestructura.
    *   **Dependencias**: Depende de la capa de dominio.
    *   **Archivos clave**:
        *   `service`: Clases que implementan las interfaces `port.input` del dominio, conteniendo la lógica específica de cada caso de uso (ej. `AuthService.java`, `ProductService.java`).
        *   `port`: Interfaces que definen los contratos de la capa de aplicación, a menudo reflejando los `port.input` del dominio.

*   **`com.posapi.infrastructure` (Capa de Infraestructura / Adaptadores)**
    *   **Propósito**: Contiene todos los detalles de implementación externos. Son los "adaptadores" que conectan el dominio y la aplicación con tecnologías específicas (bases de datos, frameworks web, sistemas de mensajería, etc.).
    *   **Dependencias**: Depende de las capas de aplicación y dominio.
    *   **Archivos clave**:
        *   `adapter.input.rest`: Controladores REST (`@RestController`), DTOs de entrada (`@RequestBody`) y salida (`@ResponseBody`), que exponen la API a clientes externos (ej. `AuthController.java`, `LoginRequest.java`, `LoginResponse.java`).
        *   `adapter.output.persistence`: Implementaciones de las interfaces `port.output` del dominio, utilizando tecnologías de persistencia (ej. Spring Data JPA, `UserJpaRepository.java`, `UserEntity.java`).
        *   `security`: Configuración de Spring Security, filtros JWT, servicios JWT (ej. `SecurityConfig.java`, `JwtRequestFilter.java`, `JwtService.java`).
        *   `config`: Otras configuraciones específicas de Spring (ej. `WebConfig.java` si existiera).

### **2. Flujo de una Petición (Ejemplo: Login de Usuario)**

Vamos a seguir el flujo de una petición de login, desde que el cliente envía la solicitud hasta que recibe la respuesta, pasando por cada capa:

#### **A. Petición del Cliente (Frontend/Postman)**

*   **Acción**: El cliente (ej. tu frontend React en `http://localhost:3001`) envía una solicitud `POST` a `http://localhost:9090/api/v1/auth/authenticate`.
*   **Body**: Un JSON con las credenciales del usuario.
*   **Headers**:
    *   `Content-Type: application/json`
    *   `Origin: http://localhost:3001` (automáticamente añadido por el navegador para peticiones CORS)

**Ejemplo de Petición:**
```http
POST http://localhost:9090/api/v1/auth/authenticate
Content-Type: application/json
Origin: http://localhost:3001

{
    "email": "admin@posapi.com",
    "password": "SuperSecretPassword123!"
}
```

#### **B. Capa de Infraestructura (Adaptador de Entrada: REST Controller y Seguridad)**

1.  **`com.posapi.infrastructure.security.SecurityConfig.java`**:
    *   **Propósito**: Configuración principal de Spring Security y CORS.
    *   **Flujo**: Antes de que la petición llegue al `AuthController`, Spring Security intercepta la petición. El `corsConfigurationSource()` bean verifica si el `Origin` de la petición (`http://localhost:3001`) está en su lista de `allowedOrigins`. Si no lo está, la petición es bloqueada aquí con un error CORS.
    *   **Ejemplo**:
        ```java
        @Configuration
        @EnableWebSecurity
        public class SecurityConfig {
            // ... otros beans
            @Bean
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- CORS aplicado aquí
                    .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/api/v1/users/register").permitAll()
                        .anyRequest().authenticated()
                    )
                    // ... otros filtros y manejadores de excepción
                    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
            }

            @Bean
            public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001")); // <-- Permite el origen del frontend
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
            }
        }
        ```
    *   **Resultado**: Si el origen es permitido, la petición continúa. Si no, se devuelve un error CORS al cliente.

2.  **`com.posapi.infrastructure.adapter.input.rest.auth.AuthController.java`**:
    *   **Propósito**: Es el punto de entrada HTTP para las operaciones de autenticación.
    *   **Flujo**:
        *   Recibe la solicitud `POST` en la ruta `/api/v1/auth/authenticate`.
        *   Utiliza `@Valid` para validar el `LoginRequest` (ej. formato de email, campos obligatorios). Si falla, Spring automáticamente devuelve un `400 Bad Request` con detalles del error de validación.
        *   Delega la lógica de autenticación a la capa de aplicación, llamando a `authManagementPort.authenticate(request)`.
    *   **Ejemplo**:
        ```java
        @RestController
        @RequestMapping("/api/v1/auth") // Base path para este controlador
        @RequiredArgsConstructor
        public class AuthController {
            private final AuthManagementPort authManagementPort;

            @PostMapping("/authenticate") // Endpoint específico para el login
            public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest request) {
                // La validación @Valid ocurre aquí. Si falla, se lanza MethodArgumentNotValidException.
                // Pasa la petición al puerto de entrada de la capa de aplicación
                LoginResponse loginResponse = authManagementPort.authenticate(request);
                return new ResponseEntity<>(loginResponse, HttpStatus.OK);
            }
        }
        ```
    *   **`com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest.java`**:
        *   **Propósito**: Data Transfer Object (DTO) que define la estructura de los datos esperados en el cuerpo de la petición de login. Contiene anotaciones de validación de Jakarta.
        *   **Ejemplo**:
            ```java
            package com.posapi.infrastructure.adapter.input.rest.auth.dto;
            import jakarta.validation.constraints.NotBlank;
            public record LoginRequest(
                @NotBlank String email, // <-- @NotBlank asegura que no sea nulo ni vacío
                @NotBlank String password
            ) {}
            ```

#### **C. Capa de Aplicación (Puerto de Entrada e Implementación de Caso de Uso)**

1.  **`com.posapi.application.port.auth.AuthManagementPort.java`**:
    *   **Propósito**: Interfaz (puerto de entrada) que define el contrato para la gestión de autenticación. La capa de dominio define esta interfaz, y la capa de aplicación la implementa.
    *   **Ejemplo**:
        ```java
        package com.posapi.application.port.auth;
        import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
        import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;
        public interface AuthManagementPort {
            LoginResponse authenticate(LoginRequest request);
        }
        ```

2.  **`com.posapi.application.service.auth.AuthService.java`**:
    *   **Propósito**: Implementa la lógica de negocio para el caso de uso de autenticación. Es el "caso de uso" que orquesta las operaciones.
    *   **Dependencias**: Inyecta `AuthenticationManager` (de Spring Security para la verificación de credenciales), `JwtService` (de infraestructura para la creación de tokens) y `UserRepository` (puerto de salida del dominio para obtener detalles del usuario).
    *   **Flujo**:
        *   **Autenticación de Credenciales**: Llama a `authenticationManager.authenticate()` con un `UsernamePasswordAuthenticationToken`. Spring Security se encarga de verificar las credenciales contra el `UserDetailsService` configurado. Si las credenciales son incorrectas, se lanza una `AuthenticationException`.
        *   **Generación de JWT**: Si la autenticación es exitosa, llama a `jwtService.generateToken(authentication)` para crear un nuevo token JWT.
        *   **Obtención de Datos del Usuario**: Consulta `userRepository.findByEmail()` para obtener los detalles completos del usuario autenticado (entidad de dominio `User`).
        *   **Construcción de Respuesta**: Mapea la entidad `User` a un `UserResponse` DTO y luego construye un `LoginResponse` que contiene el token JWT y el `UserResponse`.
    *   **Ejemplo**:
        ```java
        package com.posapi.application.service.auth;
        import com.posapi.application.port.auth.AuthManagementPort;
        import com.posapi.domain.model.user.User;
        import com.posapi.domain.port.output.UserRepository;
        import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginRequest;
        import com.posapi.infrastructure.adapter.input.rest.auth.dto.LoginResponse;
        import com.posapi.infrastructure.adapter.input.rest.user.dto.UserResponse;
        import com.posapi.infrastructure.security.JwtService;
        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.AuthenticationException;
        import org.springframework.stereotype.Service;

        @Service
        @RequiredArgsConstructor
        @Slf4j
        public class AuthService implements AuthManagementPort {
            private final AuthenticationManager authenticationManager;
            private final JwtService jwtService;
            private final UserRepository userRepository; // <-- Puerto de salida del dominio

            @Override
            public LoginResponse authenticate(LoginRequest request) {
                try {
                    // 1. Autenticación de credenciales
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(request.email(), request.password())
                    );
                    // 2. Generación de JWT
                    String jwt = jwtService.generateToken(authentication);

                    // 3. Obtener el usuario autenticado (entidad de dominio)
                    User user = userRepository.findByEmail(request.email())
                            .orElseThrow(() -> new IllegalStateException("Authenticated user not found in repository"));

                    // 4. Construir UserResponse (DTO)
                    UserResponse userResponse = new UserResponse(
                            user.getId(),
                            user.getEmail(),
                            user.getFullName(),
                            user.getRole().getName(), // Asumiendo que el rol está cargado
                            user.getIsActive(),
                            user.getCreatedAt(),
                            user.getUpdatedAt()
                    );
                    // 5. Retornar LoginResponse con token y UserResponse
                    return new LoginResponse(jwt, userResponse);
                } catch (AuthenticationException e) {
                    log.warn("Authentication failed for user {}: {}", request.email(), e.getMessage());
                    throw new IllegalArgumentException("Invalid email or password"); // Mensaje genérico por seguridad
                }
            }
        }
        ```

#### **D. Capa de Dominio (Modelos y Puertos de Salida)**

1.  **`com.posapi.domain.model.user.User.java`**:
    *   **Propósito**: Representa la entidad de negocio "Usuario". Contiene los atributos y la lógica de negocio relacionada con un usuario (ej. `getRole()`, `isActive()`). Es agnóstica a la persistencia.
    *   **Ejemplo**:
        ```java
        package com.posapi.domain.model.user;
        import com.posapi.domain.model.role.Role;
        import lombok.Builder;
        import lombok.Data;
        import java.time.Instant;
        import java.util.UUID;

        @Data
        @Builder
        public class User {
            private UUID id;
            private String email;
            private String password; // Hash de la contraseña
            private String fullName;
            private boolean isActive;
            private Role role; // Entidad de dominio Role
            private Instant createdAt;
            private Instant updatedAt;
            private Instant deletedAt;
            // ... otros campos de auditoría
        }
        ```

2.  **`com.posapi.domain.port.output.UserRepository.java`**:
    *   **Propósito**: Interfaz (puerto de salida) que define el contrato para la persistencia de usuarios. La capa de aplicación/dominio no sabe *cómo* se guardan los usuarios, solo *qué* operaciones se pueden realizar.
    *   **Ejemplo**:
        ```java
        package com.posapi.domain.port.output;
        import com.posapi.domain.model.user.User;
        import java.util.Optional;
        import java.util.UUID;
        public interface UserRepository {
            Optional<User> findByEmail(String email);
            User save(User user);
            // ... otros métodos de acceso a datos
        }
        ```

#### **E. Capa de Infraestructura (Adaptador de Salida: Persistencia)**

1.  **`com.posapi.infrastructure.adapter.output.persistence.repository.UserJpaRepository.java`**:
    *   **Propósito**: Implementa la interfaz `UserRepository` del dominio. Es el adaptador que se comunica con la base de datos usando Spring Data JPA.
    *   **Flujo**: Spring Data JPA genera automáticamente las consultas SQL basadas en los nombres de los métodos (ej. `findByEmailAndDeletedAtIsNull`).
    *   **Mapeo**: Mapea los resultados de la base de datos (ej. `UserEntity`) a la entidad de dominio `User` (usando un método `toDomain()` en `UserEntity` o un mapper dedicado).
    *   **Ejemplo**:
        ```java
        package com.posapi.infrastructure.adapter.output.persistence.repository;
        import com.posapi.domain.model.user.User;
        import com.posapi.domain.port.output.UserRepository;
        import com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity;
        import org.springframework.data.jpa.repository.JpaRepository;
        import java.util.Optional;
        import java.util.UUID;

        public interface UserJpaRepository extends JpaRepository<UserEntity, UUID>, UserRepository {
            // Spring Data JPA genera la implementación de este método
            Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

            // Implementación del método de la interfaz de dominio
            @Override
            default Optional<User> findByEmail(String email) {
                return findByEmailAndDeletedAtIsNull(email).map(UserEntity::toDomain); // Mapea UserEntity a User de dominio
            }
            // ... otros métodos
        }
        ```
    *   **`com.posapi.infrastructure.adapter.output.persistence.entity.user.UserEntity.java`**:
        *   **Propósito**: Representa la entidad de usuario tal como se almacena en la base de datos. Contiene anotaciones JPA (`@Entity`, `@Table`, `@Column`).
        *   **Ejemplo**:
            ```java
            package com.posapi.infrastructure.adapter.output.persistence.entity.user;
            import com.posapi.domain.model.user.User;
            import com.posapi.infrastructure.adapter.output.persistence.entity.role.RoleEntity;
            import jakarta.persistence.*;
            import lombok.Data;
            import java.time.Instant;
            import java.util.UUID;

            @Entity
            @Table(name = "users")
            @Data
            public class UserEntity {
                @Id private UUID id;
                @Column(unique = true, nullable = false) private String email;
                @Column(nullable = false) private String password; // Hash de la contraseña
                @Column(name = "full_name", nullable = false) private String fullName;
                @Column(name = "is_active") private boolean isActive;
                @ManyToOne(fetch = FetchType.LAZY)
                @JoinColumn(name = "role_id", nullable = false)
                private RoleEntity role; // Relación con RoleEntity
                private Instant createdAt;
                private Instant updatedAt;
                private Instant deletedAt;

                public User toDomain() {
                    // Mapeo de UserEntity a User de dominio
                    return User.builder()
                            .id(this.id)
                            .email(this.email)
                            .password(this.password)
                            .fullName(this.fullName)
                            .isActive(this.isActive)
                            .role(this.role != null ? this.role.toDomain() : null) // Mapea RoleEntity a Role de dominio
                            .createdAt(this.createdAt)
                            .updatedAt(this.updatedAt)
                            .deletedAt(this.deletedAt)
                            .build();
                }
            }
            ```

#### **F. Base de Datos (PostgreSQL)**

*   **Acción**: Ejecuta la consulta SQL generada por el adaptador de persistencia.
*   **Ejemplo**:
    ```sql
    SELECT id, email, password, full_name, is_active, role_id, created_at, updated_at, deleted_at
    FROM users
    WHERE email = 'admin@posapi.com' AND deleted_at IS NULL;
    ```
*   **Retorno**: Devuelve los datos del usuario si lo encuentra.

#### **G. Retorno de la Respuesta (Flujo Inverso)**

1.  **`UserJpaRepository`**: Recibe los datos de la BD, los mapea a `UserEntity` y luego a `User` (dominio) usando el método `toDomain()`.
2.  **`AuthService`**: Recibe el `User` del repositorio, genera el JWT, construye el `LoginResponse` (que contiene el token y el `UserResponse` DTO).
3.  **`AuthController.java`**: Recibe el `LoginResponse` del `AuthService` y lo envuelve en un `ResponseEntity` con `HttpStatus.OK (200)`.
4.  **Cliente**: Recibe la respuesta HTTP 200 con el JSON del `LoginResponse`.

**Ejemplo de Respuesta:**
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluQHBvc2FwaS5jb20iLCJpYXQiOjE3ODQ4NTc1MjYsImV4cCI6MTc4NDg1NzYxNn0.wWgiYZBb2A0jOYLIN_5jL_K0Ted_t8xE_RfZ7cmfkEc7yEutWFoIPVX4n8CE1CIT",
    "user": {
        "id": "3868d700-3a07-44c3-bc95-745537bc3bc0",
        "email": "admin@posapi.com",
        "fullName": "Admin User",
        "roleName": "ADMIN",
        "isActive": true,
        "createdAt": "2026-07-24T01:31:53.644058Z",
        "updatedAt": null
    }
}
```

### **3. Archivos y Componentes Clave Adicionales (No Omitidos)**

*   **`com.posapi.PosApiApplication.java`**:
    *   **Propósito**: Clase principal de Spring Boot que arranca la aplicación. Contiene el método `main()`. Es el punto de inicio de la ejecución.
    *   **Ejemplo**:
        ```java
        package com.posapi;
        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        @SpringBootApplication
        public class PosApiApplication {
            public static void main(String[] args) {
                SpringApplication.run(PosApiApplication.class, args);
            }
        }
        ```

*   **`com.posapi.infrastructure.security.JwtRequestFilter.java`**:
    *   **Propósito**: Filtro de Servlet que se ejecuta en cada petición HTTP protegida (después de la autenticación inicial, si aplica). Su rol es validar el token JWT en las peticiones subsiguientes al login.
    *   **Flujo**:
        *   Intercepta la petición.
        *   Extrae el token JWT del encabezado `Authorization` (ej. `Bearer <token>`).
        *   Utiliza `JwtService` para validar el token y extraer el email del usuario.
        *   Carga los detalles del usuario (`UserDetails`) usando `UserDetailsService` (que obtiene los detalles del usuario de la base de datos).
        *   Si el token es válido, establece la autenticación en el `SecurityContextHolder` de Spring Security, permitiendo que la petición continúe con el usuario autenticado.
    *   **Ejemplo**:
        ```java
        package com.posapi.infrastructure.security;
        import jakarta.servlet.FilterChain;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import org.springframework.lang.NonNull;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.core.userdetails.UserDetailsService;
        import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
        import org.springframework.stereotype.Component;
        import org.springframework.web.filter.OncePerRequestFilter;
        import java.io.IOException;

        @Component
        public class JwtRequestFilter extends OncePerRequestFilter {
            private final JwtService jwtService;
            private final UserDetailsService userDetailsService;

            public JwtRequestFilter(JwtService jwtService, UserDetailsService userDetailsService) {
                super();
                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
            }

            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
                final String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response); // No hay token, continúa la cadena de filtros (Spring Security decidirá si es necesario autenticar)
                    return;
                }
                String jwt = authHeader.substring(7); // Extrae el token
                String userEmail = jwtService.extractUsername(jwt); // Extrae el email del token

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // Carga los detalles del usuario
                    if (jwtService.validateToken(jwt, userDetails)) { // Valida el token
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken); // Establece el usuario autenticado en el contexto de seguridad
                    }
                }
                filterChain.doFilter(request, response); // Continúa la cadena de filtros
            }
        }
        ```

*   **`com.posapi.infrastructure.security.JwtService.java`**:
    *   **Propósito**: Servicio para la creación, validación y extracción de información de tokens JWT.
    *   **Flujo**: Utiliza la librería JJWT para firmar y verificar tokens, extraer claims (como el email y los roles) y gestionar la expiración.
    *   **Ejemplo**:
        ```java
        package com.posapi.infrastructure.security;
        import io.jsonwebtoken.Claims;
        import io.jsonwebtoken.Jwts;
        import io.jsonwebtoken.io.Decoders;
        import io.jsonwebtoken.security.Keys;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.GrantedAuthority;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.stereotype.Service;
        import javax.crypto.SecretKey;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.function.Function;
        import java.util.stream.Collectors;

        @Service
        public class JwtService {
            @Value("${app.jwt.secret}") private String secretKey; // Secreto para firmar/verificar tokens
            @Value("${app.jwt.expiration-in-ms}") private long jwtExpiration; // Tiempo de expiración

            public String extractUsername(String token) { /* ... extrae el 'sub' del token ... */ }
            public String generateToken(Authentication authentication) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                // ... construye el token con claims, sujeto, fecha de emisión, expiración y firma
                return Jwts.builder().setClaims(claims).setSubject(authentication.getName()).setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)).signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
            }
            public Boolean validateToken(String token, UserDetails userDetails) { /* ... valida el token ... */ }
            private SecretKey getSignKey() { /* ... obtiene la clave de firma del secreto ... */ }
        }
        ```

*   **`com.posapi.infrastructure.security.JwtAuthenticationEntryPoint.java`**:
    *   **Propósito**: Maneja las excepciones de autenticación (ej. token inválido o ausente) en Spring Security.
    *   **Flujo**: Cuando un usuario no autenticado intenta acceder a un recurso protegido, este componente envía una respuesta `401 Unauthorized`.
    *   **Ejemplo**:
        ```java
        package com.posapi.infrastructure.security;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import org.springframework.security.core.AuthenticationException;
        import org.springframework.security.web.AuthenticationEntryPoint;
        import org.springframework.stereotype.Component;
        import java.io.IOException;

        @Component
        public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"); // Envía un 401
            }
        }
        ```

*   **`com.posapi.application.service.bootstrap.BootstrapService.java`**:
    *   **Propósito**: Se ejecuta al inicio de la aplicación (`CommandLineRunner`) para inicializar datos esenciales como roles y un usuario administrador por defecto.
    *   **Flujo**:
        *   Verifica si los roles básicos ("ADMIN", "USER") existen; si no, los crea.
        *   Verifica si el usuario `admin@posapi.com` existe; si no, lo crea con la contraseña hasheada (`SuperSecretPassword123!`) y el rol "ADMIN".
    *   **Ejemplo**:
        ```java
        package com.posapi.application.service.bootstrap;
        import com.posapi.domain.model.role.Role;
        import com.posapi.domain.model.user.User;
        import com.posapi.domain.port.output.RoleRepository;
        import com.posapi.domain.port.output.UserRepository;
        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.boot.CommandLineRunner;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;
        import java.time.Instant;
        import java.util.Optional;
        import java.util.UUID;

        @Service
        @RequiredArgsConstructor
        @Slf4j
        public class BootstrapService implements CommandLineRunner {
            private final RoleRepository roleRepository;
            private final UserRepository userRepository;
            private final PasswordEncoder passwordEncoder;

            @Value("${app.bootstrap.admin.email}") private String adminEmail; // Valor del application.yml
            @Value("${app.bootstrap.admin.password}") private String adminPassword; // Valor del application.yml

            @Override
            @Transactional
            public void run(String... args) {
                log.info("Starting data bootstrap process...");
                createRoleIfNotFound("ADMIN");
                createRoleIfNotFound("USER");
                createOrUpdateAdminUser(); // Llama a la lógica para crear/actualizar admin
                log.info("Data bootstrap process finished.");
            }

            private void createOrUpdateAdminUser() {
                Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Admin role not found"));
                Optional<User> existingUser = userRepository.findByEmail(adminEmail);

                if (existingUser.isEmpty()) {
                    log.info("Admin user not found. Creating...");
                    User adminUser = User.builder()
                            .id(UUID.randomUUID())
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword)) // <-- Hashea la contraseña
                            .fullName("Admin User")
                            .isActive(true)
                            .role(adminRole)
                            .createdAt(Instant.now())
                            .build();
                    userRepository.save(adminUser);
                    log.info("Admin user created successfully.");
                } else {
                    log.info("Admin user already exists.");
                }
            }
            // ... createRoleIfNotFound
        }
        ```

*   **`src/main/resources/application.yml`**:
    *   **Propósito**: Archivo de configuración principal de Spring Boot. Define propiedades de la aplicación, bases de datos, seguridad, etc.
    *   **Contiene**: Configuración de la base de datos, RabbitMQ, Swagger, propiedades de JWT (secreto, expiración) y las credenciales del usuario administrador para el `BootstrapService`.
    *   **Ejemplo**:
        ```yaml
        server:
          port: ${SERVER_PORT:8080}
        spring:
          datasource:
            url: jdbc:postgresql://${DB_HOST:localhost}:5432/${POSTGRES_DB:pos_db}
            username: ${SPRING_DATASOURCE_USERNAME:product}
            password: ${SPRING_DATASOURCE_PASSWORD:product123}
          jpa:
            hibernate:
              ddl-auto: update # O 'none' si Flyway gestiona todo
        app:
          jwt:
            secret: ${JWT_SECRET:dGhpc2lzYW5leGFtcGxlYmFzZTY0c2VjcmV0Zm9ySFRNQzI1Ng==} # Clave secreta para JWT
            expiration-in-ms: ${JWT_EXPIRATION:3600000} # 1 hora de expiración
          bootstrap:
            admin:
              email: ${ADMIN_EMAIL:admin@posapi.com} # Email del admin por defecto
              password: ${ADMIN_PASSWORD:SuperSecretPassword123!} # Contraseña del admin por defecto
        logging:
          level:
            com.posapi: DEBUG # Nivel de log para tu aplicación
        ```

*   **`src/main/resources/db/migration/V1__create_initial_schema.sql`**:
    *   **Propósito**: Script de migración de base de datos gestionado por Flyway. Se ejecuta automáticamente al inicio de la aplicación si la base de datos está vacía o si hay nuevas migraciones pendientes.
    *   **Flujo**: Define la estructura inicial de todas las tablas de la base de datos (`CREATE TABLE`), tipos ENUM, funciones de auditoría y la inserción de datos semilla iniciales (roles, usuario `system@script.com`).
    *   **Ejemplo**:
        ```sql
        -- ... DROP TABLES, CREATE EXTENSION, CREATE TYPES ...

        CREATE TABLE users (
            id UUID PRIMARY KEY,
            email VARCHAR(255) UNIQUE NOT NULL,
            password TEXT NOT NULL, -- <-- Aquí se guarda el hash de la contraseña
            full_name VARCHAR(255) NOT NULL,
            is_active BOOLEAN DEFAULT TRUE,
            role_id UUID NOT NULL REFERENCES roles(id),
            created_at TIMESTAMPTZ DEFAULT NOW(),
            updated_at TIMESTAMPTZ,
            deleted_at TIMESTAMPTZ,
            -- ... campos de auditoría
        );

        -- ... otras tablas ...

        -- 5. INSERCIÓN SEMILLA (DML)
        INSERT INTO roles (id, name, created_by_user_id)
        VALUES ('00000000-0000-0000-0000-000000000001', 'SYSTEM_ROLE', NULL);

        INSERT INTO users (id, email, password, full_name, is_active, role_id, created_by_user_id)
        VALUES (
            'ffffffff-ffff-ffff-ffff-ffffffffffff',
            'system@script.com',
            'n/a', -- <-- Contraseña en texto plano para el usuario semilla (no el admin@posapi.com)
            'SYSTEM_INIT',
            TRUE,
            '00000000-0000-0000-0000-000000000001',
            NULL
        );
        -- ... actualizaciones y otros inserts
        ```

---

Esta documentación te proporciona una visión completa del comportamiento de tu backend, desde la arquitectura hasta el flujo de una petición específica, con ejemplos en cada capa. Esto debería ser una base sólida para entender cómo funciona tu `pos-api`.
