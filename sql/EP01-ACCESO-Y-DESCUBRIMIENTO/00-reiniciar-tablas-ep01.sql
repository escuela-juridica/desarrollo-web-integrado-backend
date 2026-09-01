-- ESEJUR - EP01 Reinicio completo de tablas
-- ADVERTENCIA: elimina todas las tablas y todos sus datos del esquema public.
-- No elimina la base de datos.
-- Despues de ejecutarlo, correr nuevamente:
--   01-tablas-y-llaves-ep01.sql
--   02-datos-iniciales-ep01.sql

BEGIN;

-- Nivel 1: tablas que dependen de cursos, usuarios, lecciones o recursos.
DROP TABLE IF EXISTS matricula CASCADE;
DROP TABLE IF EXISTS material_leccion CASCADE;

-- Nivel 2: contenido academico y asignaciones docentes.
DROP TABLE IF EXISTS leccion CASCADE;
DROP TABLE IF EXISTS modulo CASCADE;
DROP TABLE IF EXISTS curso_docente CASCADE;
DROP TABLE IF EXISTS recurso CASCADE;

-- Nivel 3: cursos; se elimina despues de todas sus tablas dependientes.
DROP TABLE IF EXISTS curso CASCADE;

-- Nivel 4: datos dependientes de las cuentas de usuario.
DROP TABLE IF EXISTS codigo_verificacion_correo CASCADE;
DROP TABLE IF EXISTS token_recuperacion_acceso CASCADE;
DROP TABLE IF EXISTS usuario_rol CASCADE;

-- Nivel 5: cuentas y personas.
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS persona CASCADE;

-- Nivel 6: catalogos maestros sin dependencias restantes.
DROP TABLE IF EXISTS estado_curso CASCADE;
DROP TABLE IF EXISTS entidad_certificadora CASCADE;
DROP TABLE IF EXISTS categoria_tematica CASCADE;
DROP TABLE IF EXISTS tipo_curso CASCADE;
DROP TABLE IF EXISTS rol CASCADE;

COMMIT;
