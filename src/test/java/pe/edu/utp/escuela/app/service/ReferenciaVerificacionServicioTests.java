package pe.edu.utp.escuela.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import pe.edu.utp.escuela.app.exception.ResourceNotFoundException;

class ReferenciaVerificacionServicioTests {

    private static final String ISSUER = "esejur-api";
    private SecretKey secretKey;
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        secretKey = new SecretKeySpec(
                "esejur-clave-entornos-no-productivos-2026-para-pruebas".getBytes(), "HmacSHA256");
        jwtEncoder = NimbusJwtEncoder.withSecretKey(secretKey).build();
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        jwtDecoder = decoder;
    }

    private ReferenciaVerificacionServicio servicioConClock(Clock clock) {
        return new ReferenciaVerificacionServicio(jwtEncoder, jwtDecoder, clock, ISSUER);
    }

    @Test
    void emitirYResolverDevuelvenElMismoUsuarioId() {
        ReferenciaVerificacionServicio servicio =
                servicioConClock(Clock.fixed(Instant.now(), ZoneId.of("America/Lima")));

        String referencia = servicio.emitir(42L);

        assertEquals(42L, servicio.resolverUsuarioId(referencia));
    }

    @Test
    void resolverRechazaUnaReferenciaVencida() {
        Clock clockPasado = Clock.fixed(
                Instant.now().minus(Duration.ofMinutes(120)), ZoneId.of("America/Lima"));
        ReferenciaVerificacionServicio servicio = servicioConClock(clockPasado);
        String referencia = servicio.emitir(42L);

        ReferenciaVerificacionServicio servicioActual =
                servicioConClock(Clock.fixed(Instant.now(), ZoneId.of("America/Lima")));

        assertThrows(ResourceNotFoundException.class,
                () -> servicioActual.resolverUsuarioId(referencia));
    }

    @Test
    void resolverRechazaUnTokenSinElPropositoCorrecto() {
        Instant ahora = Instant.now();
        var claims = org.springframework.security.oauth2.jwt.JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(Duration.ofMinutes(60)))
                .subject("42")
                .claim("proposito", "otro-proposito")
                .build();
        var header = org.springframework.security.oauth2.jwt.JwsHeader.with(MacAlgorithm.HS256).build();
        String tokenConOtroProposito = jwtEncoder
                .encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(header, claims))
                .getTokenValue();

        ReferenciaVerificacionServicio servicio =
                servicioConClock(Clock.fixed(ahora, ZoneId.of("America/Lima")));

        assertThrows(ResourceNotFoundException.class,
                () -> servicio.resolverUsuarioId(tokenConOtroProposito));
    }

    @Test
    void resolverRechazaUnaCadenaQueNoEsUnJwtValido() {
        ReferenciaVerificacionServicio servicio =
                servicioConClock(Clock.fixed(Instant.now(), ZoneId.of("America/Lima")));

        assertThrows(ResourceNotFoundException.class, () -> servicio.resolverUsuarioId("no-es-un-jwt"));
    }
}
