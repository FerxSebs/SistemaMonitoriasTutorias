# Sistema de Tutorías y Monitorías - UTS

## 📋 Descripción

Sistema web desarrollado con Spring Boot para la gestión integral de tutorías y monitorías académicas con sistema de facturación. El sistema permite a estudiantes solicitar servicios de tutoría y monitoría, mientras que profesores, tutores y monitores pueden gestionar sus asignaciones. Incluye un sistema completo de roles y permisos con Spring Security.

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 2.7.18**
- **Java 1.8**
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Autenticación y autorización
- **MySQL 8.0** - Base de datos
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5.3.0** - Framework CSS
- **Font Awesome 6.4.0** - Iconos
- **Spring Boot Validation** - Validación de formularios
- **Maven** - Gestión de dependencias

## 🗄️ Base de Datos

### Configuración
- **Base de datos**: `sistema_tutorias`
- **Puerto**: 3306 (MySQL)
- **Usuario**: `root`
- **Contraseña**: `root` (configurar en `application.properties`)
- **Puerto de la aplicación**: `8091`

### Estructura de Tablas Principales
- `usuarios` - Usuarios del sistema
- `roles` - Roles del sistema (ADMIN, ESTUDIANTE, MONITOR, PROFESOR, TUTOR)
- `estudiantes` - Información de estudiantes
- `profesores` - Información de profesores
- `tutores` - Profesores que dictan tutorías
- `monitores` - Estudiantes que son monitores
- `materias` - Catálogo de materias académicas
- `tutorias` - Registro de sesiones de tutoría
- `monitorias` - Registro de sesiones de monitoría
- `facturas` - Sistema de facturación para monitorías

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java 1.8 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

### Pasos de Instalación

1. **Clonar o descargar el proyecto**

2. **Configurar la base de datos MySQL**
   ```sql
   CREATE DATABASE IF NOT EXISTS sistema_tutorias
   CHARACTER SET utf8mb4
   COLLATE utf8mb4_unicode_ci;
   ```

3. **Configurar la conexión a la base de datos**
   Editar el archivo `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sistema_tutorias?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=root
   server.port=8091
   ```

4. **Ejecutar el proyecto**
   ```bash
   mvn spring-boot:run
   ```
   
   O desde el IDE ejecutar la clase `ProyectoSistemasMonitoriasTutoriasApplication`

5. **Acceder a la aplicación**
   - URL: `http://localhost:8091`
   - Página principal: `http://localhost:8091/index`
   - Login: `http://localhost:8091/login`
   - Registro público: `http://localhost:8091/registro`

## 👥 Roles y Permisos del Sistema

### 🔐 Administrador (ADMIN)
- Acceso completo a todos los CRUDs del sistema
- Gestión de usuarios, roles y permisos
- Visualización de todas las tutorías y monitorías
- Gestión de facturación completa

### 🎓 Estudiante (ESTUDIANTE)
- Crear tutorías y monitorías
- Ver solo sus propias tutorías y monitorías
- Ver detalles de facturación al crear monitorías
- **No puede** modificar o eliminar tutorías/monitorías existentes
- Editar su propio perfil (excepto ID, programa académico y correo)

### 👨‍🏫 Profesor (PROFESOR)
- Ver y modificar materias
- Ver tutorías y monitorías
- Editar su propio perfil

### 🎯 Tutor (TUTOR)
- Ver tutorías asignadas
- Modificar **solo el estado** de tutorías asignadas
- **No puede** crear nuevas tutorías (son asignadas por el administrador)
- Si también es profesor, puede ver y modificar materias
- Editar su propio perfil

### 📚 Monitor (MONITOR)
- Ver y modificar monitorías asignadas
- Ver su facturación
- Editar su propio perfil

## 📱 Funcionalidades Principales

### ✅ Gestión de Tutorías
- **Crear** nuevas tutorías (Estudiantes y Administradores)
- **Listar** tutorías con filtrado por rol
- **Editar** tutorías (con restricciones según rol)
- **Eliminar** tutorías (solo Administradores)
- **Ver detalles** completos de tutorías

### ✅ Gestión de Monitorías
- **Crear** nuevas monitorías (Estudiantes y Administradores)
- **Listar** monitorías con filtrado por rol
- **Editar** monitorías (Monitores y Administradores)
- **Eliminar** monitorías (solo Administradores)
- **Ver detalles** completos de monitorías

### ✅ Gestión de Usuarios
- **Estudiantes**: CRUD completo (Administradores)
- **Profesores**: CRUD completo (Administradores)
- **Tutores**: CRUD completo (Administradores)
- **Monitores**: CRUD completo (Administradores)
- **Registro público** de estudiantes

### ✅ Gestión de Materias
- CRUD completo de materias
- Acceso para Profesores y Administradores

### ✅ Sistema de Facturación
- Generación automática de facturas para monitorías
- Visualización de facturas por monitor
- Cálculo de totales y detalles de facturación

### ✅ Autenticación y Autorización
- **Login** con Spring Security
- **Registro público** de estudiantes
- **Contraseñas encriptadas** con BCrypt
- **Control de acceso basado en roles** (RBAC)
- **Navbar dinámico** según el rol del usuario

### ✅ Perfil de Usuario
- Edición de perfil personal
- Restricciones según el rol (no se puede modificar ID, programa académico/departamento, correo)

## 🔒 Seguridad Implementada

### Medidas de Seguridad
- **Spring Security** con autenticación basada en formularios
- **BCrypt** para encriptación de contraseñas
- **CSRF Protection** habilitado
- **HTTP Security Headers**:
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `Content-Security-Policy`
  - `Referrer-Policy`
  - `Permissions-Policy`
- **Input Sanitization** para prevenir XSS y SQL Injection
- **Validación de entrada** con Bean Validation (JSR-303)
- **Validadores personalizados** para IDs y campos específicos

### Control de Acceso
- Rutas públicas: `/`, `/index`, `/contacto`, `/login`, `/registro`
- Rutas protegidas según roles
- Redirección a página de error personalizada para accesos no autorizados

## 🌐 Estructura de Rutas Principales

### Públicas
- `/` - Página principal (redirige a index)
- `/index` - Página de inicio pública
- `/contacto` - Información del equipo de desarrollo
- `/login` - Formulario de inicio de sesión
- `/registro` - Registro público de estudiantes

### Protegidas por Rol

#### Tutorías
- `/views/tutorias/` - Lista de tutorías
- `/views/tutorias/nuevo` - Crear tutoría
- `/views/tutorias/editar/{id}` - Editar tutoría
- `/views/tutorias/ver/{id}` - Detalles de tutoría

#### Monitorías
- `/views/monitorias/` - Lista de monitorías
- `/views/monitorias/nuevo` - Crear monitoría
- `/views/monitorias/editar/{id}` - Editar monitoría

#### Gestión de Usuarios
- `/views/estudiantes/` - Gestión de estudiantes
- `/views/profesores/` - Gestión de profesores
- `/views/tutores/` - Gestión de tutores
- `/views/monitores/` - Gestión de monitores

#### Otros
- `/views/materias/` - Gestión de materias
- `/views/facturas/` - Gestión de facturas
- `/perfil/editar` - Editar perfil personal
- `/home` - Página de inicio según rol

## 🎨 Interfaz de Usuario

### Características del Diseño
- **Diseño responsivo** que se adapta a móviles, tablets y desktop
- **Navbar dinámico** que cambia según el rol del usuario
- **Colores corporativos** con gradientes modernos
- **Iconos intuitivos** de Font Awesome
- **Mensajes de feedback** para acciones del usuario
- **Validación visual** en tiempo real
- **Página de error personalizada** para accesos no autorizados

### Componentes UI
- **Navbar público** para páginas no autenticadas
- **Navbar dinámico** con menús según rol
- **Tarjetas** para organizar información
- **Tablas responsivas** para listados
- **Formularios** con validación visual
- **Botones** de acción contextual
- **Carrusel de equipo** en página de contacto

## 📊 Estructura del Proyecto

```
src/main/java/uts/edu/java/proyecto/
├── config/              # Configuraciones
│   ├── SecurityConfig.java
│   ├── SecurityHeadersConfig.java
│   ├── PasswordEncoderConfig.java
│   └── DataInitializer.java
├── controlador/         # Controladores MVC
│   ├── PrincipalControlador.java
│   ├── AuthControlador.java
│   ├── TutoriaControlador.java
│   ├── MonitoriaControlador.java
│   ├── EstudianteControlador.java
│   ├── ProfesorControlador.java
│   ├── TutorControlador.java
│   ├── MonitorControlador.java
│   ├── MateriaControlador.java
│   └── PerfilControlador.java
├── modelo/              # Entidades JPA
│   ├── Usuario.java
│   ├── Rol.java
│   ├── Estudiante.java
│   ├── Profesor.java
│   ├── Tutor.java
│   ├── Monitor.java
│   ├── Materia.java
│   ├── Tutoria.java
│   ├── Monitoria.java
│   └── Factura.java
├── repositorio/         # Repositorios Spring Data JPA
├── servicio/            # Lógica de negocio
├── util/                # Utilidades
│   └── InputSanitizer.java
└── validacion/          # Validadores personalizados
    ├── ValidId.java
    └── ValidIdValidator.java
```

## 👥 Equipo de Desarrollo

### Integrante 1: SEBASTIAN DAVID FERREIRA ARANDA
- **Rol**: Desarrollador Full Stack
- **Responsabilidades**: 
  - Supervisión de integración frontend-backend
  - Desarrollo completo del CRUD de Monitorías

### Integrante 2: ANA CAROLINA MAYORGA FONSECA
- **Rol**: Desarrollador Full Stack
- **Responsabilidades**: 
  - Desarrollo e implementación del CRUD de Tutores y Profesores
  - Gestión de lógica de negocio y validaciones

### Integrante 3
- **Rol**: Desarrollador Backend & Security
- **Responsabilidades**: 
  - Desarrollo e implementación del CRUD de Monitores y Estudiantes
  - Configuración e implementación de Spring Security
  - Gestión de usuarios y roles

### Integrante 4
- **Rol**: Desarrollador Backend
- **Responsabilidades**: 
  - Desarrollo e implementación del CRUD de Facturación y Materias
  - Gestión de información académica y financiera

## 🔧 Configuración Adicional

### Puerto del Servidor
Por defecto la aplicación corre en el puerto 8091. Para cambiarlo, editar `application.properties`:
```properties
server.port=8091
```

### Configuración JPA
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Configuración de Seguridad
- Las contraseñas se encriptan automáticamente con BCrypt
- Los usuarios se crean con roles asignados
- El registro público crea usuarios con rol ESTUDIANTE

## 🐛 Solución de Problemas

### Error de Conexión a Base de Datos
1. Verificar que MySQL esté ejecutándose
2. Confirmar credenciales en `application.properties`
3. Verificar que la base de datos `sistema_tutorias` exista
4. Verificar que el puerto 3306 esté disponible

### Error de Puerto en Uso
```properties
# Cambiar puerto en application.properties
server.port=8091
```

### Problemas con Spring Security
- Verificar que las rutas públicas estén correctamente configuradas
- Revisar los roles asignados a los usuarios
- Verificar la configuración de CSRF si hay problemas con formularios

## 📝 Notas de Desarrollo

### Patrones Utilizados
- **MVC** (Model-View-Controller)
- **Repository Pattern**
- **Service Layer Pattern**
- **Dependency Injection**
- **Role-Based Access Control (RBAC)**

### Validaciones Implementadas
- **Bean Validation (JSR-303)**: `@NotBlank`, `@Email`, `@Size`
- **Validadores personalizados**: `@ValidId` para validar IDs positivos
- **Sanitización de entrada**: Prevención de XSS y SQL Injection

## 🔮 Funcionalidades Futuras

- Reportes y estadísticas avanzadas
- Notificaciones por correo electrónico
- Calendario de tutorías y monitorías
- Sistema de calificaciones
- Chat en tiempo real
- Exportación de datos a Excel/PDF
- API REST para integraciones externas

## 📄 Licencia

Este proyecto es desarrollado para la Universidad Tecnológica de Santander (UTS). Todos los derechos reservados.

---


**Universidad Tecnológica de Santander (UTS) - 2025**
