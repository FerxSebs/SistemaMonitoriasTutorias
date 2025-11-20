# Análisis de Seguridad - Sistema de Tutorías y Monitorías

## 📋 Resumen Ejecutivo

Este documento analiza las medidas de seguridad implementadas en el sistema y proporciona recomendaciones para mejorar la protección contra ataques comunes como SQL Injection, XSS, CSRF, y otros.

---

## ✅ Medidas de Seguridad Actuales

### 1. **Protección contra SQL Injection**

#### ✅ **Implementado Correctamente:**
- **Uso de JPA/Hibernate**: Todas las consultas utilizan JPA Query Language (JPQL) con parámetros posicionales (`?1`, `?2`), lo que previene SQL Injection.
- **Spring Data JPA**: Los métodos de repositorio (`findBy`, `existsBy`) utilizan prepared statements automáticamente.
- **Sin consultas nativas sin parámetros**: No se encontraron consultas SQL nativas con concatenación de strings.

**Ejemplo de consulta segura:**
```java
@Query("SELECT f FROM Factura f WHERE mon.idMonitor = ?1 AND m.fecha BETWEEN ?2 AND ?3")
List<Factura> findByMonitorAndFechaBetween(Integer idMonitor, LocalDateTime fechaInicio, LocalDateTime fechaFin);
```

#### ⚠️ **Áreas de Mejora:**
- No se encontraron consultas nativas vulnerables, pero se recomienda mantener esta práctica.

---

### 2. **Validación de Entrada**

#### ✅ **Implementado:**
- **Bean Validation (JSR-303)**: Uso de anotaciones `@Valid`, `@NotBlank`, `@Email`, `@Size` en los modelos.
- **Validación en controladores**: Uso de `@Valid` con `BindingResult` para validar formularios.
- **Validación manual**: Validaciones adicionales en controladores (ej: `AuthControlador`).

**Ejemplo:**
```java
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
private String nombre;
```

#### ⚠️ **Áreas de Mejora:**
- Falta sanitización de entrada (eliminar caracteres peligrosos).
- No hay validación de tipos en `@PathVariable` y `@RequestParam` (ej: validar que IDs sean números positivos).

---

### 3. **Autenticación y Autorización**

#### ✅ **Implementado:**
- **Spring Security**: Configuración completa de autenticación y autorización.
- **Password Encoding**: Uso de `PasswordEncoder` (BCrypt) para encriptar contraseñas.
- **Role-Based Access Control (RBAC)**: Control de acceso basado en roles.
- **UserDetailsService personalizado**: Implementación para cargar usuarios desde múltiples fuentes.

#### ⚠️ **Vulnerabilidades Críticas:**
1. **CSRF Deshabilitado**: `csrf().disable()` en `SecurityConfig` (línea 104) - **CRÍTICO**
2. **Sin rate limiting**: No hay protección contra ataques de fuerza bruta.
3. **Sin bloqueo de cuenta**: No hay mecanismo para bloquear cuentas después de intentos fallidos.

---

### 4. **Protección de Contraseñas**

#### ✅ **Implementado:**
- **Encriptación**: Contraseñas encriptadas con BCrypt antes de guardar.
- **Validación de longitud**: Mínimo 6 caracteres en el registro.

#### ⚠️ **Áreas de Mejora:**
- No hay validación de complejidad (mayúsculas, números, caracteres especiales).
- No hay política de expiración de contraseñas.
- No hay historial de contraseñas.

---

### 5. **Headers de Seguridad HTTP**

#### ❌ **No Implementado:**
- No se configuran headers de seguridad como:
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `X-XSS-Protection: 1; mode=block`
  - `Strict-Transport-Security` (HSTS)
  - `Content-Security-Policy`

---

### 6. **Manejo de Errores**

#### ✅ **Implementado:**
- Páginas de error personalizadas (`/error/acceso-denegado`).
- Manejo de excepciones en controladores.

#### ⚠️ **Áreas de Mejora:**
- Los mensajes de error pueden exponer información sensible (stack traces).
- No hay logging de intentos de acceso no autorizados.

---

### 7. **Protección contra XSS (Cross-Site Scripting)**

#### ⚠️ **Parcialmente Implementado:**
- **Thymeleaf**: Escapa automáticamente el contenido en templates (`th:text`).
- **No verificado**: No se confirma que todos los inputs se escapen correctamente.
- **Falta sanitización**: No hay sanitización explícita de entrada HTML.

---

### 8. **Configuración de Base de Datos**

#### ⚠️ **Vulnerabilidades:**
- **Credenciales en texto plano**: Usuario y contraseña de BD en `application.properties`.
- **SSL deshabilitado**: `useSSL=false` en la URL de conexión.
- **Permisos amplios**: Usuario `root` con todos los privilegios.

---

## 🔴 Vulnerabilidades Críticas Identificadas

### 1. **CSRF Deshabilitado** (CRÍTICO)
- **Ubicación**: `SecurityConfig.java`, línea 104
- **Riesgo**: Permite ataques Cross-Site Request Forgery
- **Impacto**: Un atacante puede realizar acciones en nombre del usuario autenticado

### 2. **Falta de Rate Limiting** (ALTO)
- **Riesgo**: Ataques de fuerza bruta en login y registro
- **Impacto**: Compromiso de cuentas mediante intentos repetidos

### 3. **Headers de Seguridad Faltantes** (MEDIO)
- **Riesgo**: Vulnerabilidades de clickjacking, MIME sniffing, XSS
- **Impacto**: Exposición a ataques comunes del navegador

### 4. **Credenciales en Texto Plano** (MEDIO)
- **Riesgo**: Exposición de credenciales de base de datos
- **Impacto**: Acceso no autorizado a la base de datos

### 5. **Sin Validación de Tipos en PathVariables** (BAJO-MEDIO)
- **Riesgo**: Posibles errores o inyección indirecta
- **Impacto**: Errores de aplicación o comportamiento inesperado

---

## 🛡️ Recomendaciones de Mejora

### Prioridad ALTA (Implementar Inmediatamente)

1. **Habilitar CSRF Protection**
   ```java
   .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
   ```

2. **Implementar Rate Limiting**
   - Usar `spring-boot-starter-cache` con Redis o implementar contador en memoria
   - Limitar intentos de login (ej: 5 intentos por 15 minutos)

3. **Configurar Headers de Seguridad**
   - Crear un filtro o usar `HttpSecurity.headers()`

4. **Mover Credenciales a Variables de Entorno**
   - Usar `@Value` o Spring Cloud Config
   - Nunca commitear credenciales al repositorio

### Prioridad MEDIA

5. **Implementar Validación de Tipos en PathVariables**
   - Crear validadores personalizados o usar `@Min`, `@Max` en modelos

6. **Mejorar Validación de Contraseñas**
   - Agregar validación de complejidad
   - Implementar política de contraseñas

7. **Sanitización de Entrada**
   - Usar librerías como OWASP Java HTML Sanitizer para HTML
   - Validar y limpiar todos los inputs de usuario

8. **Logging de Seguridad**
   - Registrar intentos de login fallidos
   - Registrar accesos no autorizados
   - Implementar auditoría de acciones críticas

### Prioridad BAJA

9. **Implementar Bloqueo de Cuenta**
   - Bloquear cuenta después de N intentos fallidos
   - Requerir captcha después de varios intentos

10. **Mejorar Manejo de Errores**
    - No exponer stack traces en producción
    - Mensajes de error genéricos para usuarios

11. **Implementar HTTPS**
    - Configurar SSL/TLS en producción
    - Habilitar HSTS

12. **Auditoría y Monitoreo**
    - Implementar logs estructurados
    - Monitoreo de intentos sospechosos

---

## 📊 Resumen de Seguridad por Categoría

| Categoría | Estado | Nivel de Protección |
|-----------|--------|---------------------|
| SQL Injection | ✅ Bueno | Alto |
| XSS | ⚠️ Parcial | Medio |
| CSRF | ❌ Crítico | Ninguno |
| Autenticación | ✅ Bueno | Alto |
| Autorización | ✅ Bueno | Alto |
| Validación de Entrada | ⚠️ Parcial | Medio |
| Headers de Seguridad | ❌ Faltante | Ninguno |
| Rate Limiting | ❌ Faltante | Ninguno |
| Encriptación | ✅ Bueno | Alto |
| Logging de Seguridad | ❌ Faltante | Ninguno |

---

## 🔧 Próximos Pasos

1. ✅ **CSRF Protection** - IMPLEMENTADO
2. ✅ **Headers de Seguridad Básicos** - IMPLEMENTADO
3. ✅ **Utilidades de Sanitización** - IMPLEMENTADO
4. ⏳ Realizar pruebas de penetración básicas
5. ⏳ Configurar monitoreo de seguridad
6. ⏳ Documentar políticas de seguridad
7. ⏳ Capacitar al equipo en mejores prácticas

---

## ✅ Mejoras Implementadas

### 1. **CSRF Protection Habilitado**
- **Archivo**: `SecurityConfig.java`
- **Cambios**: 
  - Habilitado CSRF con `CookieCsrfTokenRepository`
  - Tokens CSRF almacenados en cookies HTTP-only
  - Configuración lista para producción (cambiar `setSecure(true)` con HTTPS)

### 2. **Headers de Seguridad HTTP**
- **Archivo**: `SecurityConfig.java` y `SecurityHeadersConfig.java`
- **Cambios**:
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `Content-Security-Policy` (configurado en filtro personalizado)
  - `Referrer-Policy`
  - `Permissions-Policy`

### 3. **Utilidades de Sanitización**
- **Archivo**: `InputSanitizer.java`
- **Funcionalidades**:
  - Sanitización de entrada HTML
  - Validación de IDs
  - Validación de emails
  - Detección de patrones SQL Injection
  - Limpieza de teléfonos

### 4. **Validadores Personalizados**
- **Archivos**: `ValidId.java`, `ValidIdValidator.java`
- **Funcionalidad**: Validación de IDs para prevenir inyección indirecta

---

## ⚠️ Mejoras Pendientes (Prioridad Media-Alta)

1. **Rate Limiting**: Implementar límite de intentos de login
2. **Bloqueo de Cuenta**: Bloquear después de N intentos fallidos
3. **Validación de Contraseñas**: Agregar complejidad requerida
4. **Variables de Entorno**: Mover credenciales de BD a variables de entorno
5. **Logging de Seguridad**: Registrar intentos de acceso fallidos
6. **HTTPS**: Configurar SSL/TLS en producción
7. **Sanitización de Entrada**: Implementar uso de `InputSanitizer` en controladores
8. **Validación de PathVariables**: Usar `@ValidId` en todos los `@PathVariable` de tipo Integer

---

## 📝 Resumen Detallado de Medidas de Seguridad

### 🔒 Protección contra SQL Injection

**Estado: ✅ EXCELENTE**

#### Medidas Implementadas:

1. **JPA/Hibernate con JPQL Parametrizado**
   - Todas las consultas usan JPQL con parámetros posicionales (`?1`, `?2`)
   - Hibernate genera automáticamente prepared statements
   - Ejemplo seguro:
   ```java
   @Query("SELECT f FROM Factura f WHERE mon.idMonitor = ?1 AND m.fecha BETWEEN ?2 AND ?3")
   List<Factura> findByMonitorAndFechaBetween(Integer idMonitor, LocalDateTime fechaInicio, LocalDateTime fechaFin);
   ```

2. **Spring Data JPA**
   - Métodos derivados (`findBy`, `existsBy`) usan prepared statements automáticamente
   - No hay concatenación de strings en consultas

3. **Sin Consultas Nativas Vulnerables**
   - No se encontraron consultas SQL nativas con concatenación
   - Todas las consultas nativas (si existen) usan parámetros

#### Nivel de Protección: **ALTO** ✅

---

### 🛡️ Protección contra XSS (Cross-Site Scripting)

**Estado: ⚠️ PARCIAL**

#### Medidas Implementadas:

1. **Thymeleaf Auto-Escape**
   - Thymeleaf escapa automáticamente contenido en `th:text`
   - Protección básica contra XSS en templates

2. **Utilidad InputSanitizer (Creada pero NO Usada)**
   - Clase `InputSanitizer` con métodos para sanitizar entrada
   - Elimina etiquetas `<script>`, `javascript:`, eventos `on*`
   - Escapa caracteres HTML especiales
   - **PROBLEMA**: No se está usando en ningún controlador

#### Mejoras Necesarias:

1. **Implementar Sanitización en Controladores**
   ```java
   // Ejemplo de uso necesario:
   estudiante.setNombre(InputSanitizer.sanitize(estudiante.getNombre()));
   estudiante.setApellido(InputSanitizer.sanitize(estudiante.getApellido()));
   ```

2. **Validar Uso de th:utext**
   - Verificar que no se use `th:utext` (unescaped) sin sanitización previa

#### Nivel de Protección: **MEDIO** ⚠️

---

### 🔐 Protección CSRF (Cross-Site Request Forgery)

**Estado: ✅ IMPLEMENTADO**

#### Medidas Implementadas:

1. **CSRF Habilitado**
   - Configurado en `SecurityConfig.java` (línea 107-110)
   - Usa `CookieCsrfTokenRepository`
   - Tokens almacenados en cookies HTTP-only

2. **Configuración Actual:**
   ```java
   .csrf()
       .csrfTokenRepository(csrfTokenRepository())
       .ignoringAntMatchers("/api/public/**")
   ```

#### Nivel de Protección: **ALTO** ✅

---

### ✅ Validación de Entrada

**Estado: ⚠️ PARCIAL**

#### Medidas Implementadas:

1. **Bean Validation (JSR-303)**
   - Anotaciones `@Valid`, `@NotBlank`, `@Email`, `@Size` en modelos
   - Validación automática en controladores con `@Valid` y `BindingResult`

2. **Validación Manual en Controladores**
   - Validaciones adicionales en `AuthControlador`:
     - ID del estudiante positivo
     - Contraseña no nula y mínimo 6 caracteres
     - Email único

3. **Validadores Personalizados**
   - `@ValidId` y `ValidIdValidator` creados
   - **PROBLEMA**: No se están usando en `@PathVariable`

#### Mejoras Necesarias:

1. **Usar @ValidId en PathVariables**
   ```java
   // Actual (vulnerable):
   public String editarMonitoria(@PathVariable Integer id, Model model)
   
   // Mejorado (seguro):
   public String editarMonitoria(@PathVariable @ValidId Integer id, Model model)
   ```

2. **Implementar Sanitización**
   - Usar `InputSanitizer` en todos los campos de texto de entrada

#### Nivel de Protección: **MEDIO** ⚠️

---

### 🔑 Autenticación y Autorización

**Estado: ✅ BUENO**

#### Medidas Implementadas:

1. **Spring Security**
   - Configuración completa de autenticación
   - `DaoAuthenticationProvider` configurado
   - `UserDetailsService` personalizado

2. **Password Encoding**
   - BCrypt para encriptar contraseñas
   - Contraseñas nunca almacenadas en texto plano

3. **Role-Based Access Control (RBAC)**
   - Control de acceso basado en roles
   - Rutas protegidas por rol en `SecurityConfig`

#### Mejoras Necesarias:

1. **Rate Limiting**
   - No hay protección contra fuerza bruta
   - Implementar límite de intentos de login

2. **Bloqueo de Cuenta**
   - No hay mecanismo para bloquear cuentas después de intentos fallidos

#### Nivel de Protección: **ALTO** ✅

---

### 🌐 Headers de Seguridad HTTP

**Estado: ✅ IMPLEMENTADO**

#### Medidas Implementadas:

1. **Headers Básicos en Spring Security**
   - `X-Content-Type-Options: nosniff`
   - `X-Frame-Options: DENY`

2. **Headers Adicionales en SecurityHeadersConfig**
   - `Content-Security-Policy` (CSP)
   - `Referrer-Policy: strict-origin-when-cross-origin`
   - `Permissions-Policy`

#### Nivel de Protección: **ALTO** ✅

---

### 🔍 Validación de Tipos y Parámetros

**Estado: ⚠️ PARCIAL**

#### Problemas Identificados:

1. **PathVariables sin Validación**
   - Los `@PathVariable Integer id` no validan que sean positivos
   - Podrían aceptar valores negativos o cero

2. **RequestParams sin Validación**
   - Parámetros de búsqueda no se sanitizan
   - Posible inyección indirecta

#### Mejoras Necesarias:

1. **Validar todos los PathVariables**
   ```java
   @GetMapping("/editar/{id}")
   public String editar(@PathVariable @ValidId Integer id) {
       // ...
   }
   ```

2. **Sanitizar RequestParams**
   ```java
   @RequestParam(required = false) String busqueda
   // Debe sanitizarse antes de usar
   ```

#### Nivel de Protección: **MEDIO** ⚠️

---

### 📊 Resumen de Vulnerabilidades por Categoría

| Categoría | Estado | Nivel | Acción Requerida |
|-----------|--------|-------|------------------|
| SQL Injection | ✅ | Alto | Mantener prácticas actuales |
| XSS | ⚠️ | Medio | Implementar sanitización en controladores |
| CSRF | ✅ | Alto | Mantener configuración actual |
| Autenticación | ✅ | Alto | Agregar rate limiting |
| Autorización | ✅ | Alto | Mantener RBAC |
| Validación de Entrada | ⚠️ | Medio | Usar InputSanitizer y @ValidId |
| Headers de Seguridad | ✅ | Alto | Mantener configuración |
| Rate Limiting | ❌ | Ninguno | **IMPLEMENTAR** |
| Logging de Seguridad | ❌ | Ninguno | **IMPLEMENTAR** |
| Variables de Entorno | ❌ | Bajo | Mover credenciales |

---

## 🚨 Acciones Inmediatas Recomendadas

### Prioridad CRÍTICA:

1. **Implementar Sanitización en Controladores**
   - Usar `InputSanitizer.sanitize()` en todos los campos de texto
   - Especialmente en `AuthControlador`, `EstudianteControlador`, etc.

2. **Validar PathVariables**
   - Agregar `@ValidId` a todos los `@PathVariable Integer`
   - Crear handler para `ConstraintViolationException`

### Prioridad ALTA:

3. **Rate Limiting**
   - Implementar límite de 5 intentos de login por 15 minutos
   - Usar Spring Cache o Redis

4. **Logging de Seguridad**
   - Registrar intentos de login fallidos
   - Registrar accesos no autorizados
   - Implementar auditoría de acciones críticas

### Prioridad MEDIA:

5. **Variables de Entorno**
   - Mover credenciales de BD a variables de entorno
   - Usar `@Value("${db.password}")` o Spring Cloud Config

6. **Validación de Contraseñas**
   - Agregar complejidad requerida (mayúsculas, números, caracteres especiales)
   - Política de contraseñas más estricta

---

**Fecha de Análisis**: 2025-01-20
**Última Actualización**: 2025-01-20
**Versión del Sistema**: 0.0.1-SNAPSHOT
**Framework**: Spring Boot 2.7.18

