-- =============================================
-- SCRIPT PARA MODIFICAR LA BASE DE DATOS
-- =============================================
-- Este script modifica la estructura de la base de datos para:
-- 1. Quitar AUTO_INCREMENT de los IDs principales
-- 2. Agregar campo password a estudiantes y profesores
-- 3. Agregar campo valor_por_hora a materias
-- 4. Crear tabla de facturas

USE sistema_tutorias;

SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. QUITAR AUTO_INCREMENT DE IDs
-- =============================================

-- Modificar tabla estudiantes
ALTER TABLE estudiantes MODIFY COLUMN id_estudiante INT NOT NULL;

-- Modificar tabla profesores
ALTER TABLE profesores MODIFY COLUMN id_profesor INT NOT NULL;

-- Modificar tabla tutores
ALTER TABLE tutores MODIFY COLUMN id_tutor INT NOT NULL;

-- Modificar tabla monitores
ALTER TABLE monitores MODIFY COLUMN id_monitor INT NOT NULL;

-- =============================================
-- 2. AGREGAR CAMPO PASSWORD
-- =============================================

-- Agregar password a estudiantes (ignorar error si ya existe)
-- Si la columna ya existe, este comando fallará pero el script continuará
ALTER TABLE estudiantes 
ADD COLUMN password VARCHAR(255) NULL;

-- Agregar password a profesores (ignorar error si ya existe)
ALTER TABLE profesores 
ADD COLUMN password VARCHAR(255) NULL;

-- =============================================
-- 3. AGREGAR VALOR_POR_HORA A MATERIAS
-- =============================================

-- Agregar valor_por_hora a materias (ignorar error si ya existe)
ALTER TABLE materias 
ADD COLUMN valor_por_hora DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- =============================================
-- 4. CREAR TABLA FACTURAS
-- =============================================

CREATE TABLE IF NOT EXISTS facturas (
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
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 5. ACTUALIZAR VALORES POR HORA DE MATERIAS EXISTENTES (ejemplo)
-- =============================================

-- Actualizar materias existentes con valores por defecto
UPDATE materias SET valor_por_hora = 50000.00 WHERE valor_por_hora = 0.00 OR valor_por_hora IS NULL;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- VERIFICACIÓN
-- =============================================

SELECT 'Base de datos modificada exitosamente' AS mensaje;

