-- =============================================
-- Script para Corregir AUTO_INCREMENT
-- Sistema de Tutorías y Monitorías
-- =============================================
-- 
-- Este script corrige el AUTO_INCREMENT en todas las tablas
-- y elimina las tablas intermedias incorrectas creadas por Hibernate
-- =============================================

USE sistema_tutorias;

-- Desactivar verificaciones de foreign keys temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- Eliminar tablas intermedias incorrectas creadas por Hibernate
DROP TABLE IF EXISTS estudiantes_monitores;
DROP TABLE IF EXISTS estudiantes_tutorias;
DROP TABLE IF EXISTS profesores_tutores;
DROP TABLE IF EXISTS materias_tutorias;

-- Corregir AUTO_INCREMENT en todas las tablas
ALTER TABLE roles MODIFY COLUMN id_rol INT NOT NULL AUTO_INCREMENT;
ALTER TABLE usuarios MODIFY COLUMN id_usuario INT NOT NULL AUTO_INCREMENT;
ALTER TABLE estudiantes MODIFY COLUMN id_estudiante INT NOT NULL AUTO_INCREMENT;
ALTER TABLE profesores MODIFY COLUMN id_profesor INT NOT NULL AUTO_INCREMENT;
ALTER TABLE tutores MODIFY COLUMN id_tutor INT NOT NULL AUTO_INCREMENT;
ALTER TABLE monitores MODIFY COLUMN id_monitor INT NOT NULL AUTO_INCREMENT;
ALTER TABLE materias MODIFY COLUMN id_materia INT NOT NULL AUTO_INCREMENT;
ALTER TABLE tutorias MODIFY COLUMN id_tutoria INT NOT NULL AUTO_INCREMENT;
ALTER TABLE monitorias MODIFY COLUMN id_monitoria INT NOT NULL AUTO_INCREMENT;

-- Reactivar verificaciones de foreign keys
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'AUTO_INCREMENT corregido en todas las tablas' AS Mensaje;

