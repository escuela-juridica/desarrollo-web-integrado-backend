package pe.edu.utp.escuela.app.security;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import pe.edu.utp.escuela.app.exception.UnauthorizedException;

@Service
public class CurrentUserService {

    public CurrentUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new UnauthorizedException();
        }

        try {
            Long userId = Long.valueOf(jwt.getSubject());
            String email = jwt.getClaimAsString("email");
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority().replaceFirst("^ROLE_", ""))
                    .collect(Collectors.toUnmodifiableSet());
            return new CurrentUser(userId, email, roles);
        } catch (RuntimeException exception) {
            throw new UnauthorizedException();
        }
    }

    public record CurrentUser(Long userId, String email, Set<String> roles) {
        public boolean hasRole(String role) {
            return roles.contains(role);
        }
    }
}
