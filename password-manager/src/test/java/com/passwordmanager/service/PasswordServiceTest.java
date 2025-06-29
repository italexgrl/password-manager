package com.passwordmanager.service;

import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.repository.PasswordRepository;
import com.passwordmanager.utill.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe dla {@link PasswordService}.
 */
class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private FileStorageService fileStorageService; // Mockujemy serwis plików

    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicjalizuje mocki
        // Ustaw domyślne zachowanie mocka encryptionService
        // Zawsze zwracaj "encrypted_" + input przy szyfrowaniu
        when(encryptionService.encrypt(anyString())).thenAnswer(invocation -> "encrypted_" + invocation.getArgument(0));
        // Zawsze zwracaj "decrypted_" + input (z usunięciem "encrypted_") przy deszyfrowaniu
        when(encryptionService.decrypt(anyString())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return arg.startsWith("encrypted_") ? arg.substring("encrypted_".length()) : arg;
        });
    }

    @Test
    void testGetAllPasswords() {
        PasswordEntry p1 = new PasswordEntry(1L, "google.com", "user1", "encrypted_pass1");
        PasswordEntry p2 = new PasswordEntry(2L, "facebook.com", "user2", "encrypted_pass2");
        when(passwordRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<PasswordEntry> passwords = passwordService.getAllPasswords();

        assertNotNull(passwords);
        assertEquals(2, passwords.size());
        assertEquals("pass1", passwords.get(0).getEncryptedPassword()); // Sprawdź, czy są odszyfrowane
        assertEquals("pass2", passwords.get(1).getEncryptedPassword());
        verify(passwordRepository, times(1)).findAll();
        verify(encryptionService, times(2)).decrypt(anyString());
    }

    @Test
    void testGetPasswordByIdFound() {
        PasswordEntry p1 = new PasswordEntry(1L, "google.com", "user1", "encrypted_pass1");
        when(passwordRepository.findById(1L)).thenReturn(Optional.of(p1));

        Optional<PasswordEntry> result = passwordService.getPasswordById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("google.com", result.get().getWebsite());
        assertEquals("pass1", result.get().getEncryptedPassword()); // Sprawdź, czy odszyfrowane
        verify(passwordRepository, times(1)).findById(1L);
        verify(encryptionService, times(1)).decrypt(anyString());
    }

    @Test
    void testGetPasswordByIdNotFound() {
        when(passwordRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PasswordEntry> result = passwordService.getPasswordById(99L);

        assertFalse(result.isPresent());
        verify(passwordRepository, times(1)).findById(99L);
        verify(encryptionService, never()).decrypt(anyString()); // Nie powinno być wywołane
    }

    @Test
    void testSavePassword() {
        PasswordEntry newEntry = new PasswordEntry(null, "linkedin.com", "user3", "raw_pass3");
        PasswordEntry savedEntryInDb = new PasswordEntry(3L, "linkedin.com", "user3", "encrypted_raw_pass3");

        when(passwordRepository.save(any(PasswordEntry.class))).thenReturn(savedEntryInDb);

        PasswordEntry result = passwordService.savePassword(newEntry);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("linkedin.com", result.getWebsite());
        assertEquals("user3", result.getUsername());
        assertEquals("raw_pass3", result.getEncryptedPassword()); // Sprawdź, czy zwrócone hasło jest odszyfrowane
        verify(encryptionService, times(1)).encrypt("raw_pass3");
        verify(passwordRepository, times(1)).save(any(PasswordEntry.class)); // Sprawdź, czy save zostało wywołane
        verify(encryptionService, times(1)).decrypt("encrypted_raw_pass3"); // Sprawdź, czy decrypt zostało wywołane
    }

    @Test
    void testUpdatePasswordSuccess() {
        PasswordEntry existingEntry = new PasswordEntry(1L, "old.com", "old_user", "encrypted_old_pass");
        PasswordEntry updatedData = new PasswordEntry(1L, "new.com", "new_user", "new_raw_pass");
        PasswordEntry savedEntryInDb = new PasswordEntry(1L, "new.com", "new_user", "encrypted_new_raw_pass");

        when(passwordRepository.findById(1L)).thenReturn(Optional.of(existingEntry));
        when(passwordRepository.save(any(PasswordEntry.class))).thenReturn(savedEntryInDb);

        Optional<PasswordEntry> result = passwordService.updatePassword(1L, updatedData);

        assertTrue(result.isPresent());
        assertEquals("new.com", result.get().getWebsite());
        assertEquals("new_user", result.get().getUsername());
        assertEquals("new_raw_pass", result.get().getEncryptedPassword()); // Sprawdź, czy odszyfrowane
        verify(passwordRepository, times(1)).findById(1L);
        verify(encryptionService, times(1)).encrypt("new_raw_pass");
        verify(passwordRepository, times(1)).save(any(PasswordEntry.class));
        verify(encryptionService, times(1)).decrypt("encrypted_new_raw_pass");
    }

    @Test
    void testUpdatePasswordNotFound() {
        PasswordEntry updatedData = new PasswordEntry(99L, "nonexistent.com", "user", "pass");
        when(passwordRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<PasswordEntry> result = passwordService.updatePassword(99L, updatedData);

        assertFalse(result.isPresent());
        verify(passwordRepository, times(1)).findById(99L);
        verify(passwordRepository, never()).save(any(PasswordEntry.class)); // Save nie powinno być wywołane
    }

    @Test
    void testDeletePasswordSuccess() {
        when(passwordRepository.existsById(1L)).thenReturn(true);
        doNothing().when(passwordRepository).deleteById(1L);

        boolean result = passwordService.deletePassword(1L);

        assertTrue(result);
        verify(passwordRepository, times(1)).existsById(1L);
        verify(passwordRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePasswordNotFound() {
        when(passwordRepository.existsById(anyLong())).thenReturn(false);

        boolean result = passwordService.deletePassword(99L);

        assertFalse(result);
        verify(passwordRepository, times(1)).existsById(99L);
        verify(passwordRepository, never()).deleteById(anyLong()); // Delete nie powinno być wywołane
    }

    @Test
    void testGenerateSecurePassword() {
        String generatedPass = "GeneratedSecurePass123!";
        when(passwordGenerator.generateSecurePassword(anyInt())).thenReturn(generatedPass);

        String result = passwordService.generateSecurePassword();

        assertEquals(generatedPass, result);
        verify(passwordGenerator, times(1)).generateSecurePassword(16); // Sprawdź, czy domyślna długość jest użyta
    }

    @Test
    void testSortPasswordsByWebsite() {
        List<PasswordEntry> passwords = new ArrayList<>(Arrays.asList(
                new PasswordEntry(1L, "google.com", "u1", "p1"),
                new PasswordEntry(2L, "apple.com", "u2", "p2"),
                new PasswordEntry(3L, "amazon.com", "u3", "p3")
        ));

        // Metoda sortująca modyfikuje listę bezpośrednio
        passwordService.sortPasswordsByWebsite(passwords);

        assertEquals("amazon.com", passwords.get(0).getWebsite());
        assertEquals("apple.com", passwords.get(1).getWebsite());
        assertEquals("google.com", passwords.get(2).getWebsite());
    }

    @Test
    void testExportPasswordsToJson() throws IOException {
        List<PasswordEntry> passwords = Arrays.asList(new PasswordEntry(1L, "test.com", "user", "encrypted_pass"));
        when(passwordRepository.findAll()).thenReturn(passwords);

        passwordService.exportPasswordsToJson("test_export.json");

        verify(fileStorageService, times(1)).writePasswordsToJson(anyList(), eq("test_export.json"));
        verify(encryptionService, times(1)).decrypt("encrypted_pass");
    }

    @Test
    void testExportPasswordsToXml() throws Exception {
        List<PasswordEntry> passwords = Arrays.asList(new PasswordEntry(1L, "test.com", "user", "encrypted_pass"));
        when(passwordRepository.findAll()).thenReturn(passwords);

        passwordService.exportPasswordsToXml("test_export.xml");

        verify(fileStorageService, times(1)).writePasswordsToXml(anyList(), eq("test_export.xml"));
        verify(encryptionService, times(1)).decrypt("encrypted_pass");
    }

    @Test
    void testImportPasswordsFromJson() throws IOException {
        List<PasswordEntry> imported = Arrays.asList(
                new PasswordEntry(null, "import.com", "iuser", "raw_imported_pass")
        );
        when(fileStorageService.readPasswordsFromJson(anyString())).thenReturn(imported);
        when(passwordRepository.save(any(PasswordEntry.class))).thenAnswer(invocation -> {
            PasswordEntry arg = invocation.getArgument(0);
            arg.setId(100L); // Symulacja przypisania ID przez bazę
            return arg;
        });

        List<PasswordEntry> result = passwordService.importPasswordsFromJson("import.json");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("import.com", result.get(0).getWebsite());
        assertEquals("raw_imported_pass", result.get(0).getEncryptedPassword());
        verify(fileStorageService, times(1)).readPasswordsFromJson("import.json");
        verify(encryptionService, times(1)).encrypt("raw_imported_pass");
        verify(passwordRepository, times(1)).save(any(PasswordEntry.class));
    }

    @Test
    void testImportPasswordsFromXml() throws Exception {
        List<PasswordEntry> imported = Arrays.asList(
                new PasswordEntry(null, "xmlimport.net", "xuser", "raw_xml_pass")
        );
        when(fileStorageService.readPasswordsFromXml(anyString())).thenReturn(imported);
        when(passwordRepository.save(any(PasswordEntry.class))).thenAnswer(invocation -> {
            PasswordEntry arg = invocation.getArgument(0);
            arg.setId(200L);
            return arg;
        });

        List<PasswordEntry> result = passwordService.importPasswordsFromXml("import.xml");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("xmlimport.net", result.get(0).getWebsite());
        assertEquals("raw_xml_pass", result.get(0).getEncryptedPassword());
        verify(fileStorageService, times(1)).readPasswordsFromXml("import.xml");
        verify(encryptionService, times(1)).encrypt("raw_xml_pass");
        verify(passwordRepository, times(1)).save(any(PasswordEntry.class));
    }
}