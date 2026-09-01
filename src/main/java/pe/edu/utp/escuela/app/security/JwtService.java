package pe.edu.utp.escuela.app.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final Clock clock;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder,
            Clock clock,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.clock = clock;
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public String issue(Long userId, String email, Collection<String> roles) {
        Instant issuedAt = clock.instant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(Duration.ofMinutes(expirationMinutes)))
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", List.copyOf(roles))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }
}
