# Sistema de Tutorías - Spring Boot CRUD

## 📋 Descripción

Sistema web desarrollado con Spring Boot para la gestión de tutorías gratuitas dictadas por profesores. Incluye un CRUD completo para la tabla `tutorias` con interfaz web moderna usando Thymeleaf y Bootstrap.

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **MySQL Database**
- **Thymeleaf** (Motor de plantillas)
- **Bootstrap 5.3.0** (Framework CSS)
- **Font Awesome 6.0.0** (Iconos)
- **Lombok** (Reducción de código)
- **Maven** (Gestión de dependencias)

## 🗄️ Base de Datos

### Configuración
- **Base de datos**: `sistema_tutorias`
- **Puerto**: 3306 (MySQL)
- **Usuario**: `root`
- **Contraseña**: `tu_password` (configurar en `application.properties`)

### Estructura de Tablas
- `profesores` - Información de profesores
- `tutores` - Profesores que dictan tutorías
- `estudiantes` - Información de estudiantes
- `materias` - Materias disponibles
- `tutorias` - Registro de sesiones de tutoría

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java 17 o superior
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
   spring.datasource.username=tu_usuario_mysql
   spring.datasource.password=tu_contraseña_mysql
   ```

4. **Ejecutar el proyecto**
   ```bash
   mvn spring-boot:run
   ```
   
   O desde el IDE ejecutar la clase `ProyectoSistemasMonitoriasTutoriasApplication`

5. **Acceder a la aplicación**
   - URL: `http://localhost:8090`
   - La aplicación redirige automáticamente a `/tutorias`

## 📱 Funcionalidades

### ✅ Operaciones CRUD Completas
- **Crear** nuevas tutorías
- **Leer/Listar** todas las tutorías
- **Actualizar** tutorías existentes
- **Eliminar** tutorías

### 🔍 Características Principales

#### Formulario de Tutoría
- **Validación en tiempo real** del ID del estudiante
- **Lista desplegable** de profesores/tutores disponibles
- **Lista desplegable** de materias
- **Selector de fecha y hora** con validación visual
- **Campos de validación** para duración y estado

#### Lista de Tutorías
- **Tabla responsiva** con información completa
- **Filtros de búsqueda** por estudiante y estado
- **Cambio de estado** rápido (pendiente/realizada/cancelada)
- **Acciones** para ver, editar y eliminar
- **Interfaz moderna** con Bootstrap y Font Awesome

#### Detalles de Tutoría
- **Vista completa** de toda la información
- **Información relacionada** (estudiante, tutor, materia)
- **Acciones disponibles** desde la vista de detalles

### 🔒 Validaciones Implementadas

#### Validaciones de Negocio
- El estudiante debe existir en el sistema
- El tutor debe estar registrado
- La materia debe estar disponible
- La fecha debe ser futura
- La duración debe estar entre 0.5 y 8 horas

#### Validaciones de Formulario
- Campos obligatorios
- Formato de correo electrónico
- Longitud máxima de campos
- Validación de duración con decimales

## 📊 Datos de Prueba

El sistema incluye datos de ejemplo que se cargan automáticamente:

### Profesores
- Carlos Ramírez (Matemáticas)
- Laura Gómez (Física)
- Jorge Martínez (Ingeniería)

### Estudiantes
- Juan Martínez (ID: 1001) - Ingeniería Civil
- María Torres (ID: 1002) - Ingeniería Electrónica
- Andrés Pérez (ID: 1003) - Matemáticas
- Lucía Morales (ID: 1004) - Ingeniería de Sistemas

### Materias
- Cálculo Diferencial (MAT101)
- Física I (FIS102)
- Programación I (INF103)
- Bases de Datos (INF104)

## 🌐 Rutas de la Aplicación

- `/` - Página principal (redirige a tutorías)
- `/tutorias` - Lista de todas las tutorías
- `/tutorias/nueva` - Formulario para crear nueva tutoría
- `/tutorias/editar/{id}` - Formulario para editar tutoría
- `/tutorias/ver/{id}` - Detalles de una tutoría
- `/tutorias/eliminar/{id}` - Eliminar tutoría
- `/tutorias/buscar/estudiante` - Buscar por estudiante
- `/tutorias/buscar/estado` - Buscar por estado

## 🔧 Configuración Adicional

### Puerto del Servidor
Por defecto la aplicación corre en el puerto 8090. Para cambiarlo, editar `application.properties`:
```properties
server.port=8080
```

### Configuración JPA
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🎨 Interfaz de Usuario

### Características del Diseño
- **Diseño responsivo** que se adapta a móviles y tablets
- **Colores corporativos** con gradientes modernos
- **Iconos intuitivos** de Font Awesome
- **Mensajes de feedback** para acciones del usuario
- **Validación visual** en tiempo real

### Componentes UI
- **Header** con navegación principal
- **Tarjetas** para organizar información
- **Tablas** responsivas para listados
- **Formularios** con validación visual
- **Botones** de acción contextual
- **Modales** para confirmaciones

## 🐛 Solución de Problemas

### Error de Conexión a Base de Datos
1. Verificar que MySQL esté ejecutándose
2. Confirmar credenciales en `application.properties`
3. Verificar que la base de datos `sistema_tutorias` exista

### Error de Puerto en Uso
```bash
# Cambiar puerto en application.properties
server.port=8091
```

### Problemas con Lombok
Si hay errores de compilación relacionados con Lombok:
1. Instalar el plugin de Lombok en el IDE
2. Habilitar anotaciones en el IDE

## 📝 Notas de Desarrollo

### Estructura del Proyecto
```
src/main/java/uts/edu/java/proyecto/
├── controlador/     # Controladores REST
├── modelo/          # Entidades JPA
├── repositorio/     # Repositorios de datos
├── servicio/        # Lógica de negocio
└── configuracion/   # Configuraciones
```

### Patrones Utilizados
- **MVC** (Model-View-Controller)
- **Repository Pattern**
- **Service Layer Pattern**
- **Dependency Injection**

## 👥 Uso del Sistema

### Para Estudiantes
1. Ingresar al sistema
2. Crear nueva tutoría con su ID de estudiante
3. Seleccionar profesor, materia y fecha
4. Ver el estado de sus tutorías

### Para Administradores
1. Gestionar todas las tutorías del sistema
2. Cambiar estados de tutorías
3. Editar información cuando sea necesario
4. Eliminar tutorías canceladas

## 🔮 Funcionalidades Futuras

- Autenticación y autorización de usuarios
- Reportes y estadísticas
- Notificaciones por correo
- Calendario de tutorías
- Sistema de calificaciones
- Chat en tiempo real

---

**Desarrollado con ❤️ usando Spring Boot**
