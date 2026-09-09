/**
 * ⚠️ IMPLEMENTACIÓN TEMPORAL, solo para la Épica 01, pedida explícitamente por el docente: expone
 * {@code /api/admin/usuarios} como un CRUD REST básico, SIN base de datos — los datos viven en una
 * lista en memoria dentro de {@code AdminUsuariosMemoriaServicio} y se reinician con cada arranque
 * del backend.
 *
 * <p>A propósito NO está integrado con el sistema de sesión/roles real: no exige JWT, no consulta
 * {@code CurrentUserService} ni las tablas {@code usuario}/{@code persona}, y por lo tanto no
 * afecta ni depende de los usuarios administradores reales. Es intencionalmente simple y separado
 * de todo lo demás. Reemplaza únicamente al almacén en memoria que antes vivía solo en Angular
 * ({@code UsuariosAdminMockService}), NO implementa HU-008 completa, y las cuentas que crea desde
 * aquí no pueden iniciar sesión de verdad.
 *
 * <p>Cuando se implemente la Épica 02 (EP02-PUBLICACION-Y-MATRICULA, que incluye el panel de
 * administración real con el diseño EP02-PF-010), este paquete completo debe eliminarse —junto
 * con su test y con el código Angular que consume {@code /api/admin/usuarios}— y reemplazarse por
 * una gestión de usuarios respaldada por base de datos y por el sistema de roles real.
 */
package pe.edu.utp.escuela.app.adminusuarios;
