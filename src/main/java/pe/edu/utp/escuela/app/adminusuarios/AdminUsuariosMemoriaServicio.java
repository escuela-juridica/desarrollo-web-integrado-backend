package pe.edu.utp.escuela.app.adminusuarios;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import pe.edu.utp.escuela.app.exception.DuplicateResourceException;
import pe.edu.utp.escuela.app.exception.OperationNotAllowedException;
import pe.edu.utp.escuela.app.exception.ResourceNotFoundException;
import pe.edu.utp.escuela.app.util.TextNormalizer;

/** Almacén en memoria, sin base de datos y sin ninguna dependencia del sistema de sesión/roles
 * real (ver package-info): un CRUD básico, separado de todo lo demás. Por eso no hay protección
 * de "no te elimines a ti mismo" — este servicio no sabe quién está llamando. */
@Service
public class AdminUsuariosMemoriaServicio {

    private final TextNormalizer textos;
    private final Clock clock;

    private final Object lock = new Object();
    private final List<AdminUsuarioMemoria> usuarios = new ArrayList<>();
    private final AtomicLong siguienteId = new AtomicLong(100);

    public AdminUsuariosMemoriaServicio(TextNormalizer textos, Clock clock) {
        this.textos = textos;
        this.clock = clock;
        sembrarDatosIniciales();
    }

    public List<UsuarioAdminRespuesta> listar(String busqueda, RolUsuarioAdmin rol) {
        String termino = busqueda == null ? "" : busqueda.strip().toLowerCase(Locale.ROOT);
        synchronized (lock) {
            return usuarios.stream()
                    .filter(u -> rol == null || u.getRolPrincipal() == rol)
                    .filter(u -> termino.isEmpty()
                            || u.nombreCompleto().toLowerCase(Locale.ROOT).contains(termino)
                            || u.getCorreo().toLowerCase(Locale.ROOT).contains(termino))
                    .map(UsuarioAdminRespuesta::de)
                    .toList();
        }
    }

    public UsuarioAdminRespuesta obtener(Long usuarioId) {
        return UsuarioAdminRespuesta.de(buscarOLanzar(usuarioId));
    }

    public CrearUsuarioAdminRespuesta crear(CrearUsuarioAdminPeticion peticion) {
        String correo = textos.normalizeEmail(peticion.correo());

        synchronized (lock) {
            Optional<AdminUsuarioMemoria> existente = usuarios.stream()
                    .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                    .findFirst();
            if (existente.isPresent()) {
                return new CrearUsuarioAdminRespuesta(UsuarioAdminRespuesta.de(existente.get()), true, null);
            }

            AdminUsuarioMemoria nuevo = new AdminUsuarioMemoria();
            nuevo.setUsuarioId(siguienteId.getAndIncrement());
            nuevo.setNombres(peticion.nombres().strip());
            nuevo.setApellidoPaterno(peticion.apellidoPaterno().strip());
            nuevo.setApellidoMaterno(textos.trimToNull(peticion.apellidoMaterno()));
            nuevo.setCorreo(correo);
            nuevo.setTelefono(textos.trimToNull(peticion.telefono()));
            nuevo.setDocumentoIdentidad(textos.trimToNull(peticion.documentoIdentidad()));
            nuevo.setRolPrincipal(peticion.rol());
            nuevo.setOrigenRegistro(OrigenUsuarioAdmin.ADMINISTRATIVO);
            nuevo.setActivo(true);
            nuevo.setCondicion(CondicionCuentaAdmin.AMBAS_PENDIENTES);
            nuevo.setOtorgadoPor(OtorgadoPorAdmin.EQUIPO_ADMINISTRACION);
            nuevo.setCreadoEn(clock.instant());
            // Sin sesión real no hay quién lo conceda; el dato solo existe en los registros
            // sembrados de demostración.
            nuevo.setConcedidoPorNombre(null);

            usuarios.add(0, nuevo);
            // Contraseña de demostración fija: esta cuenta no existe en la base de datos real, así
            // que no puede iniciar sesión de verdad y no se envía ningún correo.
            return new CrearUsuarioAdminRespuesta(UsuarioAdminRespuesta.de(nuevo), false, "Escuela1415@");
        }
    }

    public UsuarioAdminRespuesta actualizar(Long usuarioId, ActualizarUsuarioAdminPeticion peticion) {
        String documento = textos.trimToNull(peticion.documentoIdentidad());

        synchronized (lock) {
            AdminUsuarioMemoria objetivo = buscarOLanzar(usuarioId);

            if (documento != null && usuarios.stream().anyMatch(
                    u -> !u.getUsuarioId().equals(usuarioId) && documento.equals(u.getDocumentoIdentidad()))) {
                throw new DuplicateResourceException("Ese documento ya está registrado en otra cuenta.");
            }

            objetivo.setNombres(peticion.nombres().strip());
            objetivo.setApellidoPaterno(peticion.apellidoPaterno().strip());
            objetivo.setApellidoMaterno(textos.trimToNull(peticion.apellidoMaterno()));
            objetivo.setTelefono(textos.trimToNull(peticion.telefono()));
            objetivo.setDocumentoIdentidad(documento);
            return UsuarioAdminRespuesta.de(objetivo);
        }
    }

    public void cambiarEstado(Long usuarioId, boolean activo) {
        synchronized (lock) {
            AdminUsuarioMemoria objetivo = buscarOLanzar(usuarioId);
            if (!activo) {
                exigirQueNoSeaElUltimoAdministrador(objetivo, "desactivar");
            }
            objetivo.setActivo(activo);
        }
    }

    /**
     * Borrado permanente, sin recuperación. Ojo: HU-008 dice explícitamente que una cuenta "se
     * deshabilita para impedir nuevos accesos, pero no se elimina ni pierde matrículas, pagos,
     * progreso, intentos, certificados o historial relacionados" — esto va contra esa regla y solo
     * existe porque se pidió explícitamente (igual que en el mock de Angular que reemplaza). No
     * rompe nada porque este almacén no tiene esas relaciones.
     */
    public void eliminar(Long usuarioId) {
        synchronized (lock) {
            AdminUsuarioMemoria objetivo = buscarOLanzar(usuarioId);
            exigirQueNoSeaElUltimoAdministrador(objetivo, "eliminar");
            usuarios.remove(objetivo);
        }
    }

    /** No depende de quién llama (este servicio no tiene esa noción): solo evita dejar el
     * listado sin ningún ADMINISTRADOR activo. */
    private void exigirQueNoSeaElUltimoAdministrador(AdminUsuarioMemoria objetivo, String accion) {
        if (objetivo.getRolPrincipal() == RolUsuarioAdmin.ADMINISTRADOR
                && objetivo.isActivo()
                && administradoresActivos() <= 1) {
            throw new OperationNotAllowedException("No puedes " + accion + " al último administrador habilitado.");
        }
    }

    private long administradoresActivos() {
        synchronized (lock) {
            return usuarios.stream()
                    .filter(u -> u.getRolPrincipal() == RolUsuarioAdmin.ADMINISTRADOR && u.isActivo())
                    .count();
        }
    }

    private AdminUsuarioMemoria buscarOLanzar(Long usuarioId) {
        synchronized (lock) {
            return usuarios.stream()
                    .filter(u -> u.getUsuarioId().equals(usuarioId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("La cuenta ya no existe."));
        }
    }

    /** Mismos tres registros de demostración que tenía {@code UsuariosAdminMockService} en
     * Angular, para que el listado no aparezca vacío al abrir el panel por primera vez. */
    private void sembrarDatosIniciales() {
        AdminUsuarioMemoria admin = new AdminUsuarioMemoria();
        admin.setUsuarioId(1L);
        admin.setNombres("ADMINISTRADOR");
        admin.setApellidoPaterno("GENERAL");
        admin.setCorreo("admin@escuelajuridica.edu.pe");
        admin.setRolPrincipal(RolUsuarioAdmin.ADMINISTRADOR);
        admin.setOrigenRegistro(OrigenUsuarioAdmin.ADMINISTRATIVO);
        admin.setActivo(true);
        admin.setCondicion(CondicionCuentaAdmin.NINGUNA);
        admin.setOtorgadoPor(OtorgadoPorAdmin.ADMINISTRADOR_INICIAL);
        admin.setCreadoEn(Instant.parse("2026-01-15T14:00:00Z"));

        AdminUsuarioMemoria jason = new AdminUsuarioMemoria();
        jason.setUsuarioId(12L);
        jason.setNombres("JASON");
        jason.setApellidoPaterno("DUVAL");
        jason.setCorreo("jason.duval@esejur.pe");
        jason.setRolPrincipal(RolUsuarioAdmin.ADMINISTRADOR);
        jason.setOrigenRegistro(OrigenUsuarioAdmin.ADMINISTRATIVO);
        jason.setActivo(true);
        jason.setCondicion(CondicionCuentaAdmin.AMBAS_PENDIENTES);
        jason.setOtorgadoPor(OtorgadoPorAdmin.EQUIPO_ADMINISTRACION);
        jason.setCreadoEn(Instant.parse("2026-09-01T15:15:00Z"));
        jason.setConcedidoPorNombre("ADMINISTRADOR GENERAL");

        AdminUsuarioMemoria lucia = new AdminUsuarioMemoria();
        lucia.setUsuarioId(21L);
        lucia.setNombres("LUCIA");
        lucia.setApellidoPaterno("CAMINOS");
        lucia.setApellidoMaterno("QUIROZ");
        lucia.setCorreo("lucia.caminos@example.com");
        lucia.setTelefono("987654321");
        lucia.setDocumentoIdentidad("48123456");
        lucia.setRolPrincipal(RolUsuarioAdmin.ALUMNO);
        lucia.setOrigenRegistro(OrigenUsuarioAdmin.GOOGLE);
        lucia.setActivo(false);
        lucia.setCondicion(CondicionCuentaAdmin.NINGUNA);
        lucia.setOtorgadoPor(OtorgadoPorAdmin.AUTOSERVICIO);
        lucia.setCreadoEn(Instant.parse("2026-05-10T13:00:00Z"));

        usuarios.add(admin);
        usuarios.add(jason);
        usuarios.add(lucia);
    }
}
