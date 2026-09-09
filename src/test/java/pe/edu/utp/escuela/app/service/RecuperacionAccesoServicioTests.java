package pe.edu.utp.escuela.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.utp.escuela.app.dto.NuevaContrasenaPeticion;
import pe.edu.utp.escuela.app.entity.Persona;
import pe.edu.utp.escuela.app.entity.TokenRecuperacionAcceso;
import pe.edu.utp.escuela.app.entity.Usuario;
import pe.edu.utp.escuela.app.exception.BusinessValidationException;
import pe.edu.utp.escuela.app.exception.InvalidTokenException;
import pe.edu.utp.escuela.app.exception.MailDeliveryException;
import pe.edu.utp.escuela.app.mail.MailService;
import pe.edu.utp.escuela.app.repository.TokenRecuperacionAccesoRepositorio;
import pe.edu.utp.escuela.app.repository.UsuarioRepositorio;
import pe.edu.utp.escuela.app.security.HashTokenServicio;
import pe.edu.utp.escuela.app.security.TokenAleatorioServicio;
import pe.edu.utp.escuela.app.util.TextNormalizer;

@ExtendWith(MockitoExtension.class)
class RecuperacionAccesoServicioTests {

    @Mock private UsuarioRepositorio usuarios;
    @Mock private TokenRecuperacionAccesoRepositorio repositorio;
    @Mock private TokenAleatorioServicio tokenAleatorioServicio;
    @Mock private HashTokenServicio hashTokenServicio;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordPolicyService passwordPolicyService;
    @Mock private MailService mailService;

    private RecuperacionAccesoServicio servicio;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-09-09T12:00:00Z"), ZoneId.of("America/Lima"));
        servicio = new RecuperacionAccesoServicio(
                usuarios, repositorio, tokenAleatorioServicio, hashTokenServicio, passwordEncoder,
                passwordPolicyService, mailService, new TextNormalizer(), clock,
                "http://localhost:4200");
    }

    private Usuario usuarioRecuperable(Long id, String hash) {
        Persona persona = new Persona();
        persona.setNombres("Ana");
        persona.setApellidoPaterno("Pérez");
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setPersona(persona);
        usuario.setCorreo("ana@example.com");
        usuario.setActivo(true);
        usuario.setContrasenaHash(hash);
        return usuario;
    }

    private TokenRecuperacionAcceso tokenVigente(Long id, Usuario usuario) {
        TokenRecuperacionAcceso token = new TokenRecuperacionAcceso();
        token.setId(id);
        token.setUsuario(usuario);
        token.setTokenHash("hash-token");
        token.setSolicitadoEn(Instant.parse("2026-09-09T11:00:00Z"));
        token.setExpiraEn(Instant.parse("2026-09-09T12:30:00Z"));
        return token;
    }

    @Test
    void solicitarConCorreoInexistenteNoHaceNadaNiLanzaExcepcion() {
        when(usuarios.findByCorreoIgnoreCase("nadie@example.com")).thenReturn(Optional.empty());

        servicio.solicitar("nadie@example.com");

        verify(repositorio, never()).invalidarPendientes(anyLong(), any());
        verify(mailService, never()).sendHtml(any());
    }

    @Test
    void solicitarConCuentaInactivaNoEnviaNada() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        usuario.setActivo(false);
        when(usuarios.findByCorreoIgnoreCase("ana@example.com")).thenReturn(Optional.of(usuario));

        servicio.solicitar("ANA@example.com");

        verify(repositorio, never()).invalidarPendientes(anyLong(), any());
        verify(mailService, never()).sendHtml(any());
    }

    @Test
    void solicitarConCuentaSoloGoogleNoEnviaNada() {
        Usuario usuario = usuarioRecuperable(1L, null);
        when(usuarios.findByCorreoIgnoreCase("ana@example.com")).thenReturn(Optional.of(usuario));

        servicio.solicitar("ana@example.com");

        verify(repositorio, never()).invalidarPendientes(anyLong(), any());
        verify(mailService, never()).sendHtml(any());
    }

    @Test
    void solicitarConCuentaRecuperableInvalidaAnterioresYEnviaCorreo() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        when(usuarios.findByCorreoIgnoreCase("ana@example.com")).thenReturn(Optional.of(usuario));
        when(tokenAleatorioServicio.generar()).thenReturn("token-visible");
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");

        servicio.solicitar("ana@example.com");

        verify(repositorio).invalidarPendientes(eq(1L), eq(Instant.parse("2026-09-09T12:00:00Z")));
        verify(mailService).sendHtml(any());
        verify(repositorio).saveAndFlush(any(TokenRecuperacionAcceso.class));
    }

    @Test
    void solicitarConFalloDeEnvioNoLanzaExcepcionYMarcaError() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        when(usuarios.findByCorreoIgnoreCase("ana@example.com")).thenReturn(Optional.of(usuario));
        when(tokenAleatorioServicio.generar()).thenReturn("token-visible");
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        org.mockito.Mockito.doThrow(new MailDeliveryException()).when(mailService).sendHtml(any());

        servicio.solicitar("ana@example.com");

        verify(repositorio).saveAndFlush(any(TokenRecuperacionAcceso.class));
    }

    @Test
    void esValidoConTokenVigenteDevuelveTrue() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        TokenRecuperacionAcceso token = tokenVigente(10L, usuario);
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        when(repositorio.findByTokenHash("hash-token")).thenReturn(Optional.of(token));

        assertTrue(servicio.esValido("token-visible"));
    }

    @Test
    void esValidoConTokenInexistenteDevuelveFalse() {
        when(hashTokenServicio.sha256("token-random")).thenReturn("hash-random");
        when(repositorio.findByTokenHash("hash-random")).thenReturn(Optional.empty());

        assertFalse(servicio.esValido("token-random"));
    }

    @Test
    void esValidoConTokenVencidoDevuelveFalse() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        TokenRecuperacionAcceso token = tokenVigente(10L, usuario);
        token.setExpiraEn(Instant.parse("2026-09-09T11:59:59Z"));
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        when(repositorio.findByTokenHash("hash-token")).thenReturn(Optional.of(token));

        assertFalse(servicio.esValido("token-visible"));
    }

    @Test
    void esValidoConTokenYaUtilizadoDevuelveFalse() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        TokenRecuperacionAcceso token = tokenVigente(10L, usuario);
        token.setUtilizadoEn(Instant.parse("2026-09-09T11:30:00Z"));
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        when(repositorio.findByTokenHash("hash-token")).thenReturn(Optional.of(token));

        assertFalse(servicio.esValido("token-visible"));
    }

    @Test
    void cambiarConContrasenasDiferentesLanzaValidacionSinConsultarToken() {
        NuevaContrasenaPeticion peticion = new NuevaContrasenaPeticion("Clave123", "Otra123");

        assertThrows(BusinessValidationException.class, () -> servicio.cambiar("token", peticion));

        verify(repositorio, never()).bloquearPorHash(anyString());
    }

    @Test
    void cambiarConTokenInexistenteLanzaTokenInvalido() {
        NuevaContrasenaPeticion peticion = new NuevaContrasenaPeticion("Clave123", "Clave123");
        when(hashTokenServicio.sha256("token-random")).thenReturn("hash-random");
        when(repositorio.bloquearPorHash("hash-random")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> servicio.cambiar("token-random", peticion));
    }

    @Test
    void cambiarConTokenVencidoLanzaTokenInvalido() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        TokenRecuperacionAcceso token = tokenVigente(10L, usuario);
        token.setExpiraEn(Instant.parse("2026-09-09T11:59:59Z"));
        NuevaContrasenaPeticion peticion = new NuevaContrasenaPeticion("Clave123", "Clave123");
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        when(repositorio.bloquearPorHash("hash-token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> servicio.cambiar("token-visible", peticion));
    }

    @Test
    void cambiarConTokenYaUtilizadoLanzaTokenInvalido() {
        Usuario usuario = usuarioRecuperable(1L, "hash-real");
        TokenRecuperacionAcceso token = tokenVigente(10L, usuario);
        token.setUtilizadoEn(Instant.parse("2026-09-09T11:30:00Z"));
        NuevaContrasenaPeticion peticion = new NuevaContrasenaPeticion("Clave123", "Clave123");
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        when(repositorio.bloquearPorHash("hash-token")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> servicio.cambiar("token-visible", peticion));
    }

    @Test
    void cambiarConTokenVigenteActualizaContrasenaYConsumeElToken() {
        Usuario usuario = usuarioRecuperable(1L, "hash-viejo");
        TokenRecuperacionAcceso token = tokenVigente(10L, usuario);
        NuevaContrasenaPeticion peticion = new NuevaContrasenaPeticion("Clave123", "Clave123");
        when(hashTokenServicio.sha256("token-visible")).thenReturn("hash-token");
        when(repositorio.bloquearPorHash("hash-token")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("Clave123")).thenReturn("hash-nuevo");

        servicio.cambiar("token-visible", peticion);

        assertEquals("hash-nuevo", usuario.getContrasenaHash());
        assertFalse(usuario.isRequiereCambioContrasena());
        assertNotNull(token.getUtilizadoEn());
        verify(repositorio).invalidarOtrosPendientes(eq(1L), eq(10L), eq(Instant.parse("2026-09-09T12:00:00Z")));
    }
}
