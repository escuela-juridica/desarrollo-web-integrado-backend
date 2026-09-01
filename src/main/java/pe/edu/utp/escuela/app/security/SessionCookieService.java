package pe.edu.utp.escuela.app.security;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class SessionCookieService {

    private final String name;
    private final boolean secure;
    private final String sameSite;
    private final Duration duration;

    public SessionCookieService(
            @Value("${security.cookie.name}") String name,
            @Value("${security.cookie.secure}") boolean secure,
            @Value("${security.cookie.same-site}") String sameSite,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.name = name;
        this.secure = secure;
        this.sameSite = sameSite;
        this.duration = Duration.ofMinutes(expirationMinutes);
    }

    public ResponseCookie create(String jwt) {
        return baseCookie(jwt).maxAge(duration).build();
    }

    public ResponseCookie delete() {
        return baseCookie("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/");
    }
}
