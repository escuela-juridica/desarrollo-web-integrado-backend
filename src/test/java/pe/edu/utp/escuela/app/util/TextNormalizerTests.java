package pe.edu.utp.escuela.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TextNormalizerTests {

    private final TextNormalizer normalizer = new TextNormalizer();

    @Test
    void normalizesEmailAndOptionalTexts() {
        assertEquals("alumno@correo.pe", normalizer.normalizeEmail("  Alumno@Correo.PE "));
        assertNull(normalizer.trimToNull("   "));
        assertNull(normalizer.trimToNull(null));
        assertEquals("999 999 999", normalizer.trimToNull("  999 999 999 "));
    }
}
