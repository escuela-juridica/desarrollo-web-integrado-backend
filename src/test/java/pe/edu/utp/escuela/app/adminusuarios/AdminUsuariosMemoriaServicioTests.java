package pe.edu.utp.escuela.app.adminusuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.utp.escuela.app.exception.DuplicateResourceException;
import pe.edu.utp.escuela.app.exception.OperationNotAllowedException;
import pe.edu.utp.escuela.app.exception.ResourceNotFoundException;
import pe.edu.utp.escuela.app.util.TextNormalizer;

/** CRUD básico en memoria, sin ninguna dependencia del sistema de sesión/roles real (ver
 * package-info) — por eso estos tests no mockean CurrentUserService ni UsuarioRepositorio. */
class AdminUsuariosMemoriaServicioTests {

    private AdminUsuariosMemoriaServicio servicio;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-09T12:00:00Z"), ZoneId.of("America/Lima"));
        servicio = new AdminUsuariosMemoriaServicio(new TextNormalizer(), clock);
    }

    @Test
    void listarSinFiltrosDevuelveLosTresSembrados() {
        List<UsuarioAdminRespuesta> resultado = servicio.listar("", null);

        assertEquals(3, resultado.size());
    }

    @Test
    void listarFiltraPorTerminoYPorRol() {
        List<UsuarioAdminRespuesta> porCorreo = servicio.listar("lucia.caminos", null);
        assertEquals(1, porCorreo.size());
        assertEquals("LUCIA CAMINOS QUIROZ", porCorreo.get(0).nombreCompleto());

        List<UsuarioAdminRespuesta> soloAdmins = servicio.listar("", RolUsuarioAdmin.ADMINISTRADOR);
        assertEquals(2, soloAdmins.size());
    }

    @Test
    void obtenerConIdInexistenteLanzaNoEncontrado() {
        assertThrows(ResourceNotFoundException.class, () -> servicio.obtener(999L));
    }

    @Test
    void crearConCorreoNuevoAgregaUsuarioConContrasenaTemporal() {
        CrearUsuarioAdminRespuesta respuesta = servicio.crear(new CrearUsuarioAdminPeticion(
                "Maria", "Torres", null, "maria.torres@example.com", null, null, RolUsuarioAdmin.ALUMNO));

        assertFalse(respuesta.reutilizada());
        assertEquals("Escuela1415@", respuesta.contrasenaTemporal());
        assertNull(respuesta.usuario().concedidoPorNombre());
        assertEquals(4, servicio.listar("", null).size());
    }

    @Test
    void crearConCorreoExistenteReutilizaYNoGeneraContrasena() {
        CrearUsuarioAdminRespuesta respuesta = servicio.crear(new CrearUsuarioAdminPeticion(
                "Lucia", "Caminos", "Quiroz", "LUCIA.CAMINOS@example.com", null, null, RolUsuarioAdmin.ALUMNO));

        assertTrue(respuesta.reutilizada());
        assertNull(respuesta.contrasenaTemporal());
        assertEquals(3, servicio.listar("", null).size());
    }

    @Test
    void actualizarConDocumentoYaUsadoPorOtraCuentaLanzaDuplicado() {
        assertThrows(DuplicateResourceException.class, () -> servicio.actualizar(1L,
                new ActualizarUsuarioAdminPeticion("ADMINISTRADOR", "GENERAL", null, null, "48123456")));
    }

    @Test
    void actualizarConDatosValidosModificaElUsuario() {
        UsuarioAdminRespuesta actualizado = servicio.actualizar(21L,
                new ActualizarUsuarioAdminPeticion("LUCIA", "CAMINOS", "QUIROZ", "999111222", "48123456"));

        assertEquals("999111222", actualizado.telefono());
    }

    @Test
    void cambiarEstadoParaDesactivarAlUltimoAdministradorActivoLanzaOperacionNoPermitida() {
        servicio.cambiarEstado(12L, false);

        assertThrows(OperationNotAllowedException.class, () -> servicio.cambiarEstado(1L, false));
    }

    @Test
    void cambiarEstadoParaDesactivarOtraCuentaNoAdministradoraFunciona() {
        servicio.cambiarEstado(21L, true);

        assertTrue(servicio.obtener(21L).activo());
    }

    @Test
    void eliminarAlUltimoAdministradorActivoLanzaOperacionNoPermitida() {
        servicio.cambiarEstado(12L, false);

        assertThrows(OperationNotAllowedException.class, () -> servicio.eliminar(1L));
    }

    @Test
    void eliminarUnaCuentaValidaLaQuitaDelListado() {
        servicio.eliminar(21L);

        assertEquals(2, servicio.listar("", null).size());
        assertThrows(ResourceNotFoundException.class, () -> servicio.obtener(21L));
    }
}
