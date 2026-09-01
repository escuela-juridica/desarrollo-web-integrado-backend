package pe.edu.utp.escuela.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SecurityComponentsTests {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SessionCookieService sessionCookieService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void jwtAndSessionCookieShareTheConfiguredSession() {
        String token = jwtService.issue(10L, "alumno@esejur.pe", List.of("ALUMNO"));
        Jwt decoded = jwtService.decode(token);
        ResponseCookie cookie = sessionCookieService.create(token);

        assertEquals("10", decoded.getSubject());
        assertEquals("alumno@esejur.pe", decoded.getClaimAsString("email"));
        assertTrue(decoded.getClaimAsStringList("roles").contains("ALUMNO"));
        assertEquals("ESEJUR_SESION", cookie.getName());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
    }

    @Test
    void passwordEncoderCreatesAndReadsPrefixedBcryptHashes() {
        String hash = passwordEncoder.encode("Marco1415@");

        assertTrue(hash.startsWith("{bcrypt}"));
        assertTrue(passwordEncoder.matches("Marco1415@", hash));
    }
}
