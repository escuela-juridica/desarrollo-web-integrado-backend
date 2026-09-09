package pe.edu.utp.escuela.app.adminusuarios;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** ⚠️ Temporal, sin base de datos y sin autenticación — ver package-info de {@code
 * adminusuarios}. CRUD básico, deliberadamente separado del sistema de sesión/roles real: no
 * exige JWT ni nada relacionado con los usuarios administradores reales. */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@Tag(name = "Admin — Usuarios (temporal, sin BD)",
        description = "CRUD básico para el panel de administración. Implementación provisional "
                + "en memoria pedida por el docente para la Épica 01, separada del sistema de "
                + "sesión/roles real; se elimina en la Épica 02.")
public class AdminUsuariosControlador {

    private final AdminUsuariosMemoriaServicio servicio;

    @GetMapping
    @Operation(summary = "Listar usuarios (búsqueda y filtro por rol opcionales)")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios")
    public ResponseEntity<List<UsuarioAdminRespuesta>> listar(
            @RequestParam(defaultValue = "") String busqueda,
            @RequestParam(required = false) RolUsuarioAdmin rol) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(servicio.listar(busqueda, rol));
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Consultar el detalle de un usuario")
    @ApiResponse(responseCode = "200", description = "Detalle del usuario")
    @ApiResponse(responseCode = "404", description = "La cuenta ya no existe")
    public ResponseEntity<UsuarioAdminRespuesta> obtener(@PathVariable Long usuarioId) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(servicio.obtener(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear un usuario",
            description = "Si el correo ya existe, se reutiliza esa cuenta (no se genera contraseña temporal).")
    @ApiResponse(responseCode = "200", description = "Cuenta creada o reutilizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<CrearUsuarioAdminRespuesta> crear(@Valid @RequestBody CrearUsuarioAdminPeticion p) {
        return ResponseEntity.ok(servicio.crear(p));
    }

    @PutMapping("/{usuarioId}")
    @Operation(summary = "Actualizar los datos personales de un usuario",
            description = "No modifica correo, rol ni estado.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "La cuenta ya no existe")
    @ApiResponse(responseCode = "409", description = "El documento de identidad ya está en uso")
    public ResponseEntity<UsuarioAdminRespuesta> actualizar(
            @PathVariable Long usuarioId, @Valid @RequestBody ActualizarUsuarioAdminPeticion p) {
        return ResponseEntity.ok(servicio.actualizar(usuarioId, p));
    }

    @PutMapping("/{usuarioId}/estado")
    @Operation(summary = "Habilitar o deshabilitar un usuario")
    @ApiResponse(responseCode = "204", description = "Estado actualizado")
    @ApiResponse(responseCode = "404", description = "La cuenta ya no existe")
    @ApiResponse(responseCode = "409", description = "No puedes desactivar al último administrador")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long usuarioId, @Valid @RequestBody CambiarEstadoUsuarioAdminPeticion p) {
        servicio.cambiarEstado(usuarioId, p.activo());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{usuarioId}")
    @Operation(summary = "Eliminar permanentemente un usuario",
            description = "Borrado sin recuperación; ver el comentario del servicio.")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "La cuenta ya no existe")
    @ApiResponse(responseCode = "409", description = "No puedes eliminar al último administrador")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId) {
        servicio.eliminar(usuarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
