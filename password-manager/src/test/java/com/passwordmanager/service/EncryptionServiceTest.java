package com.passwordmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla {@link EncryptionService}.
 */
class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        String TEST_SECRET_KEY = "mySuperSecretKey123";
        ReflectionTestUtils.setField(encryptionService, "secretKey", TEST_SECRET_KEY);
    }

    @Test
    void testEncryptDecrypt() {
        String originalText = "Hello, secure world!";
        String encryptedText = encryptionService.encrypt(originalText);
        String decryptedText = encryptionService.decrypt(encryptedText);

        assertNotNull(encryptedText);
        assertNotEquals(originalText, encryptedText); // Zaszyfrowany tekst powinien być inny
        assertEquals(originalText, decryptedText);    // Odszyfrowany tekst powinien być identyczny z oryginalnym
    }

    @Test
    void testEncryptDecryptEmptyString() {
        String originalText = "";
        String encryptedText = encryptionService.encrypt(originalText);
        String decryptedText = encryptionService.decrypt(encryptedText);

        assertNotNull(encryptedText);
        assertEquals("", encryptedText);
        assertEquals(originalText, decryptedText);
    }

    @Test
    void testEncryptDecryptSpecialCharacters() {
        String originalText = "P@ssw0rd!@#$^&*()_+";
        String encryptedText = encryptionService.encrypt(originalText);
        String decryptedText = encryptionService.decrypt(encryptedText);

        assertNotNull(encryptedText);
        assertNotEquals(originalText, encryptedText);
        assertEquals(originalText, decryptedText);
    }

    @Test
    void testInvalidEncryptedTextThrowsException() {
        String invalidEncryptedText = "thisIsNotValidBase64!";
        assertThrows(RuntimeException.class, () -> encryptionService.decrypt(invalidEncryptedText));
    }
}