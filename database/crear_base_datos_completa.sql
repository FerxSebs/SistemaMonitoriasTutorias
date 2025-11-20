-- =============================================
-- SCRIPT COMPLETO DE CREACIÓN DE BASE DE DATOS
-- Sistema de Tutorías y Monitorías
-- =============================================
-- Este script crea la base de datos completa con todos los requerimientos:
-- - IDs sin AUTO_INCREMENT (estudiantes, profesores, tutores, monitores)
-- - Campo password en estudiantes y profesores
-- - Campo valor_por_hora en materias
-- - Tabla facturas para monitorías
-- - Todas las relaciones y restricciones

-- =============================================
-- CREAR BASE DE DATOS
-- =============================================

DROP DATABASE IF EXISTS sistema_tutorias;
CREATE DATABASE sistema_tutorias CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_tutorias;

SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- TABLA: roles
-- =============================================

DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
    id_rol INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    PRIMARY KEY (id_rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: usuarios
-- =============================================

DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: usuario_rol (relación muchos a muchos)
-- =============================================

DROP TABLE IF EXISTS usuario_rol;
CREATE TABLE usuario_rol (
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT FK_usuario_rol_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    CONSTRAINT FK_usuario_rol_rol FOREIGN KEY (id_rol)
        REFERENCES roles(id_rol) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: estudiantes
-- =============================================
-- NOTA: id_estudiante NO tiene AUTO_INCREMENT
-- El ID debe ser proporcionado manualmente (número único por persona)

DROP TABLE IF EXISTS estudiantes;
CREATE TABLE estudiantes (
    id_estudiante INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    programa_academico VARCHAR(100),
    es_monitor BOOLEAN DEFAULT FALSE,
    password VARCHAR(255) NULL,
    PRIMARY KEY (id_estudiante),
    INDEX idx_correo (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: profesores
-- =============================================
-- NOTA: id_profesor NO tiene AUTO_INCREMENT
-- El ID debe ser proporcionado manualmente (número único por persona)

DROP TABLE IF EXISTS profesores;
CREATE TABLE profesores (
    id_profesor INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    departamento VARCHAR(100),
    es_tutor BOOLEAN DEFAULT FALSE,
    password VARCHAR(255) NULL,
    PRIMARY KEY (id_profesor),
    INDEX idx_correo (correo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: tutores
-- =============================================
-- NOTA: id_tutor NO tiene AUTO_INCREMENT
-- El ID debe ser proporcionado manualmente

DROP TABLE IF EXISTS tutores;
CREATE TABLE tutores (
    id_tutor INT NOT NULL,
    id_profesor INT NOT NULL,
    area_expertise VARCHAR(100),
    PRIMARY KEY (id_tutor),
    CONSTRAINT FK_tutor_profesor FOREIGN KEY (id_profesor)
        REFERENCES profesores(id_profesor) ON DELETE CASCADE,
    UNIQUE KEY UK_tutor_profesor (id_profesor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: monitores
-- =============================================
-- NOTA: id_monitor NO tiene AUTO_INCREMENT
-- El ID debe ser proporcionado manualmente
-- NOTA: Un monitor comparte el mismo ID que su estudiante (es la misma persona)

DROP TABLE IF EXISTS monitores;
CREATE TABLE monitores (
    id_monitor INT NOT NULL,
    id_estudiante INT NOT NULL,
    area_expertise VARCHAR(100) NOT NULL,
    estado VARCHAR(50) DEFAULT 'Activo',
    PRIMARY KEY (id_monitor),
    CONSTRAINT FK_monitor_estudiante FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    UNIQUE KEY UK_monitor_estudiante (id_estudiante)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: materias
-- =============================================

DROP TABLE IF EXISTS materias;
CREATE TABLE materias (
    id_materia INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    descripcion TEXT,
    valor_por_hora DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id_materia),
    UNIQUE KEY UK_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: tutorias
-- =============================================

DROP TABLE IF EXISTS tutorias;
CREATE TABLE tutorias (
    id_tutoria INT NOT NULL AUTO_INCREMENT,
    id_tutor INT NOT NULL,
    id_estudiante INT NOT NULL,
    id_materia INT NOT NULL,
    fecha DATETIME NOT NULL,
    duracion_horas DECIMAL(5,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente',
    observaciones TEXT,
    PRIMARY KEY (id_tutoria),
    CONSTRAINT FK_tutoria_tutor FOREIGN KEY (id_tutor)
        REFERENCES tutores(id_tutor) ON DELETE CASCADE,
    CONSTRAINT FK_tutoria_estudiante FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    CONSTRAINT FK_tutoria_materia FOREIGN KEY (id_materia)
        REFERENCES materias(id_materia) ON DELETE CASCADE,
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: monitorias
-- =============================================

DROP TABLE IF EXISTS monitorias;
CREATE TABLE monitorias (
    id_monitoria INT NOT NULL AUTO_INCREMENT,
    id_estudiante INT NOT NULL,
    id_monitor INT NOT NULL,
    id_materia INT NOT NULL,
    fecha DATETIME NOT NULL,
    duracion_horas DECIMAL(5,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente',
    observaciones TEXT,
    PRIMARY KEY (id_monitoria),
    CONSTRAINT FK_monitoria_estudiante FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id_estudiante) ON DELETE CASCADE,
    CONSTRAINT FK_monitoria_monitor FOREIGN KEY (id_monitor)
        REFERENCES monitores(id_monitor) ON DELETE CASCADE,
    CONSTRAINT FK_monitoria_materia FOREIGN KEY (id_materia)
        REFERENCES materias(id_materia) ON DELETE CASCADE,
    INDEX idx_fecha (fecha),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TABLA: facturas
-- =============================================

DROP TABLE IF EXISTS facturas;
CREATE TABLE facturas (
    id_factura INT NOT NULL AUTO_INCREMENT,
    id_monitoria INT NOT NULL,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision DATETIME NOT NULL,
    horas DECIMAL(5,2) NOT NULL,
    valor_por_hora DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    iva DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    PRIMARY KEY (id_factura),
    CONSTRAINT FK_factura_monitoria FOREIGN KEY (id_monitoria)
        REFERENCES monitorias(id_monitoria) ON DELETE CASCADE,
    INDEX idx_numero_factura (numero_factura),
    INDEX idx_estado (estado),
    INDEX idx_fecha_emision (fecha_emision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- INSERTAR DATOS INICIALES
-- =============================================

-- Insertar roles
INSERT IGNORE INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('USER', 'Usuario estándar'),
('PROFESOR', 'Profesor/Tutor'),
('ESTUDIANTE', 'Estudiante'),
('MONITOR', 'Monitor'),
('TUTOR', 'Tutor');

-- Insertar materias de ejemplo con valores por hora
INSERT IGNORE INTO materias (nombre, codigo, descripcion, valor_por_hora) VALUES
('Programación I', 'PROG101', 'Fundamentos de programación', 50000.00),
('Base de Datos', 'BD201', 'Diseño e implementación de bases de datos', 60000.00),
('Estructuras de Datos', 'ED301', 'Algoritmos y estructuras de datos', 55000.00),
('Matemáticas Discretas', 'MAT101', 'Matemáticas para ciencias de la computación', 45000.00),
('Programación Orientada a Objetos', 'POO201', 'Principios de POO y diseño de software', 60000.00),
('Ingeniería de Software', 'IS301', 'Metodologías y procesos de desarrollo de software', 65000.00);

-- =============================================
-- VERIFICACIÓN
-- =============================================

SELECT 'Base de datos creada exitosamente' AS mensaje;

-- Mostrar estructura de tablas creadas
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'sistema_tutorias'
ORDER BY TABLE_NAME;

