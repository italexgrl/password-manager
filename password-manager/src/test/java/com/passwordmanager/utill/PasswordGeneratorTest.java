package com.passwordmanager.utill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link PasswordGenerator}.
 */
class PasswordGeneratorTest {

    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
    }

    @Test
    void testGenerateSecurePasswordLength() {
        int length = 12;
        String password = passwordGenerator.generateSecurePassword(length);
        assertNotNull(password);
        assertEquals(length, password.length());
    }

    @Test
    void testGenerateSecurePasswordContent() {
        String password = passwordGenerator.generateSecurePassword(20);
        assertNotNull(password);
        assertTrue(password.matches(".*[a-z].*"), "Hasło powinno zawierać małe litery");
        assertTrue(password.matches(".*[A-Z].*"), "Hasło powinno zawierać duże litery");
        assertTrue(password.matches(".*[0-9].*"), "Hasło powinno zawierać cyfry");
        assertTrue(password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>/?].*"), "Hasło powinno zawierać znaki specjalne");
    }

    @Test
    void testGenerateSecurePasswordMinimumLength() {
        assertThrows(IllegalArgumentException.class, () -> passwordGenerator.generateSecurePassword(7));
        assertDoesNotThrow(() -> passwordGenerator.generateSecurePassword(8));
    }

    @Test
    void testPasswordsAreRandom() {
        String password1 = passwordGenerator.generateSecurePassword(10);
        String password2 = passwordGenerator.generateSecurePassword(10);
        assertNotEquals(password1, password2, "Dwa wygenerowane hasła powinny być różne");
    }
}