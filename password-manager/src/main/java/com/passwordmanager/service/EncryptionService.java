package com.passwordmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Serwis odpowiedzialny za szyfrowanie i deszyfrowanie haseł.
 * Używa algorytmu AES. Klucz szyfrowania jest pobierany z właściwości aplikacji.
 */

@Service
public class EncryptionService {
    @Value("${encryption.secret-key}")
    private String secretKey;



    private SecretKeySpec secretKeySpec;

    public EncryptionService() {
        this.secretKey = secretKey;
    }

    private void setKey() {
        try {
            byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // Używamy tylko pierwszych 16 bajtów dla AES-128
            secretKeySpec = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            // Logowanie błędu jest kluczowe w prawdziwej aplikacji
            throw new RuntimeException("Błąd podczas generowania klucza szyfrowania", e);
        }
    }

    /**
     * Szyfruje podany tekst przy użyciu algorytmu AES.
     * @param strToEncrypt Tekst do zaszyfrowania.
     * @return Zaszyfrowany tekst w formacie Base64.
     */
    public String encrypt(String strToEncrypt) {
        if (secretKeySpec == null) {
            setKey(); // Upewnij się, że klucz jest ustawiony
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // ECB jest prosty, ale w produkcji zalecane są tryby z wektorem inicjalizacji (np. GCM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas szyfrowania", e);
        }
    }

    /**
     * Deszyfruje podany tekst przy użyciu algorytmu AES.
     * @param strToDecrypt Zaszyfrowany tekst w formacie Base64.
     * @return Odszyfrowany tekst.
     */
    public String decrypt(String strToDecrypt) {
        if (secretKeySpec == null) {
            setKey(); // Upewnij się, że klucz jest ustawiony
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas deszyfrowania", e);
        }
    }
}