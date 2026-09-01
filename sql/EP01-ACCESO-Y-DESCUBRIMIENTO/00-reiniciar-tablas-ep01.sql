-- ESEJUR - EP01 Reinicio completo de tablas
-- ADVERTENCIA: elimina todas las tablas y todos sus datos del esquema esejur.
-- No elimina la base de datos ni el esquema esejur.
-- Despues de ejecutarlo, correr nuevamente:
--   01-tablas-y-llaves-ep01.sql
--   02-datos-iniciales-ep01.sql

BEGIN;

-- Nivel 1: tablas que dependen de cursos, usuarios, lecciones o recursos.
DROP TABLE IF EXISTS esejur.matricula CASCADE;
DROP TABLE IF EXISTS esejur.material_leccion CASCADE;

-- Nivel 2: contenido academico y asignaciones docentes.
DROP TABLE IF EXISTS esejur.leccion CASCADE;
DROP TABLE IF EXISTS esejur.modulo CASCADE;
DROP TABLE IF EXISTS esejur.curso_docente CASCADE;
DROP TABLE IF EXISTS esejur.recurso CASCADE;

-- Nivel 3: cursos; se elimina despues de todas sus tablas dependientes.
DROP TABLE IF EXISTS esejur.curso CASCADE;

-- Nivel 4: datos dependientes de las cuentas de usuario.
DROP TABLE IF EXISTS esejur.codigo_verificacion_correo CASCADE;
DROP TABLE IF EXISTS esejur.token_recuperacion_acceso CASCADE;
DROP TABLE IF EXISTS esejur.usuario_rol CASCADE;

-- Nivel 5: cuentas y personas.
DROP TABLE IF EXISTS esejur.usuario CASCADE;
DROP TABLE IF EXISTS esejur.persona CASCADE;

-- Nivel 6: catalogos maestros sin dependencias restantes.
DROP TABLE IF EXISTS esejur.estado_curso CASCADE;
DROP TABLE IF EXISTS esejur.entidad_certificadora CASCADE;
DROP TABLE IF EXISTS esejur.categoria_tematica CASCADE;
DROP TABLE IF EXISTS esejur.tipo_curso CASCADE;
DROP TABLE IF EXISTS esejur.rol CASCADE;

COMMIT;
