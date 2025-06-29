package com.passwordmanager.utill;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Utility class do generowania bezpiecznych, losowych haseł.
 * Umożliwia konfigurowanie długości oraz typów znaków (duże/małe litery, cyfry, znaki specjalne).
 */
@Component // Oznacza klasę jako komponent Springa
public class PasswordGenerator {

    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";

    private final Random random = new SecureRandom();

    /**
     * Generuje bezpieczne hasło o podanej długości i wymaganiach.
     * Hasło zawiera co najmniej jedną małą literę, dużą literę, cyfrę i znak specjalny.
     *
     * @param length Długość generowanego hasła.
     * @return Wygenerowane, bezpieczne hasło.
     * @throws IllegalArgumentException Jeśli długość jest mniejsza niż 8.
     */
    public String generateSecurePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Długość hasła musi wynosić co najmniej 8 znaków.");
        }

        StringBuilder password = new StringBuilder(length);
        List<Character> charPool = new ArrayList<>();

        // Upewnij się, że hasło zawiera co najmniej po jednym znaku z każdej kategorii
        password.append(getRandomChar(LOWERCASE_CHARS));
        password.append(getRandomChar(UPPERCASE_CHARS));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARS));

        // Zbuduj pulę wszystkich możliwych znaków
        charPool.addAll(LOWERCASE_CHARS.chars().mapToObj(c -> (char) c).toList());
        charPool.addAll(UPPERCASE_CHARS.chars().mapToObj(c -> (char) c).toList());
        charPool.addAll(DIGITS.chars().mapToObj(c -> (char) c).toList());
        charPool.addAll(SPECIAL_CHARS.chars().mapToObj(c -> (char) c).toList());

        // Wypełnij resztę hasła losowymi znakami z puli
        for (int i = password.length(); i < length; i++) {
            password.append(charPool.get(random.nextInt(charPool.size())));
        }

        // Pomieszaj hasło, aby losowe znaki nie były na początku
        List<Character> resultChars = password.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        Collections.shuffle(resultChars, random);

        return resultChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private char getRandomChar(String charSet) {
        return charSet.charAt(random.nextInt(charSet.length()));
    }
}