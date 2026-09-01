package pe.edu.utp.escuela.app.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import pe.edu.utp.escuela.app.exception.BusinessValidationException;

class PasswordPolicyServiceTests {

    private final PasswordPolicyService service = new PasswordPolicyService();

    @Test
    void acceptsPasswordThatMeetsBusinessRules() {
        assertDoesNotThrow(() -> service.validate("Marco1415@"));
    }

    @Test
    void rejectsMissingRequiredComposition() {
        assertThrows(BusinessValidationException.class, () -> service.validate("Corta1"));
        assertThrows(BusinessValidationException.class, () -> service.validate("marco1415"));
        assertThrows(BusinessValidationException.class, () -> service.validate("MARCO1415"));
        assertThrows(BusinessValidationException.class, () -> service.validate("MarcoClave"));
    }
}
