package pe.edu.utp.escuela.app.util;

import java.util.Locale;
import org.springframework.stereotype.Component;
import pe.edu.utp.escuela.app.exception.BusinessValidationException;

@Component
public class TextNormalizer {

    public String normalizeEmail(String value) {
        String normalized = trimToNull(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    public String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.strip();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public String requireText(String value, String fieldName) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            throw new BusinessValidationException(fieldName + " es obligatorio");
        }
        return normalized;
    }
}
