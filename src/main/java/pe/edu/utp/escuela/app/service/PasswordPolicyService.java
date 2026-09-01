package pe.edu.utp.escuela.app.service;

import org.springframework.stereotype.Service;
import pe.edu.utp.escuela.app.exception.BusinessValidationException;

@Service
public class PasswordPolicyService {

    private static final int MINIMUM_LENGTH = 8;

    public void validate(String password) {
        if (password == null || password.length() < MINIMUM_LENGTH) {
            throw new BusinessValidationException(
                    "La contraseña debe tener al menos ocho caracteres");
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            throw new BusinessValidationException(
                    "La contraseña debe incluir una letra mayúscula");
        }
        if (password.chars().noneMatch(Character::isLowerCase)) {
            throw new BusinessValidationException(
                    "La contraseña debe incluir una letra minúscula");
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            throw new BusinessValidationException(
                    "La contraseña debe incluir un número");
        }
    }
}
