package com.passwordmanager.controller;

import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * Kontroler REST API do zarządzania wpisami haseł.
 * Obsługuje operacje CRUD (Create, Read, Update, Delete)
 * oraz generowanie bezpiecznych haseł, import/eksport danych
 * do/z plików JSON/XML i sortowanie.
 */
@RestController
@RequestMapping("/api/passwords")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    /**
     * Endpoint GET do pobierania wszystkich wpisów haseł.
     * Dostępna opcja sortowania alfabetycznego po nazwie strony internetowej.
     * @param sortByWebsite Jeśli true, lista zostanie posortowana algorytmem QuickSort.
     * @return Lista wszystkich wpisów haseł z odszyfrowanymi hasłami.
     */
    @GetMapping
    public List<PasswordEntry> getAllPasswords(@RequestParam(required = false, defaultValue = "false") boolean sortByWebsite) {
        List<PasswordEntry> passwords = passwordService.getAllPasswords();
        if (sortByWebsite) {
            passwordService.sortPasswordsByWebsite(passwords); // Wywołanie QuickSort
        }
        return passwords;
    }

    // ... (pozostałe metody CRUD: getPasswordById, createPassword, updatePassword, deletePassword, generateSecurePassword) ...
    // Skopiuj je tutaj z poprzedniego kroku, jeśli pominąłeś

    @GetMapping("/{id}")
    public ResponseEntity<PasswordEntry> getPasswordById(@PathVariable Long id) {
        return passwordService.getPasswordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PasswordEntry createPassword(@Valid @RequestBody PasswordEntry passwordEntry) {
        return passwordService.savePassword(passwordEntry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasswordEntry> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordEntry passwordEntry) {
        return passwordService.updatePassword(id, passwordEntry)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(@PathVariable Long id) {
        if (passwordService.deletePassword(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/generate-secure")
    public String generateSecurePassword() {
        return passwordService.generateSecurePassword();
    }


    // --- Nowe endpointy do obsługi plików ---

    /**
     * Endpoint GET do eksportowania wszystkich haseł do pliku JSON.
     * @param fileName Nazwa pliku do zapisu (np. "my_passwords.json").
     * @return ResponseEntity z komunikatem sukcesu lub błędu.
     */
    @GetMapping("/export/json")
    public ResponseEntity<String> exportToJson(@RequestParam String fileName) throws IOException {
        passwordService.exportPasswordsToJson(fileName);
        return ResponseEntity.ok("Hasła wyeksportowane do " + fileName);
    }

    /**
     * Endpoint GET do eksportowania wszystkich haseł do pliku XML.
     * @param fileName Nazwa pliku do zapisu (np. "my_passwords.xml").
     * @return ResponseEntity z komunikatem sukcesu lub błędu.
     */
    @GetMapping("/export/xml")
    public ResponseEntity<String> exportToXml(@RequestParam String fileName) {
        try {
            passwordService.exportPasswordsToXml(fileName);
            return ResponseEntity.ok("Hasła wyeksportowane do " + fileName);
        } catch (Exception e) { // Używamy ogólnego Exception ze względu na JAXBException
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Błąd podczas eksportu do XML: " + e.getMessage());
        }
    }

    /**
     * Endpoint POST do importowania haseł z pliku JSON.
     * @param fileName Nazwa pliku JSON do odczytu (np. "import_data.json").
     * @return Lista zaimportowanych haseł.
     */
    @PostMapping("/import/json")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PasswordEntry> importFromJson(@RequestParam String fileName) throws IOException {
        return passwordService.importPasswordsFromJson(fileName);
    }

    /**
     * Endpoint POST do importowania haseł z pliku XML.
     * @param fileName Nazwa pliku XML do odczytu (np. "import_data.xml").
     * @return Lista zaimportowanych haseł.
     */
    @PostMapping("/import/xml")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PasswordEntry> importFromXml(@RequestParam String fileName) throws Exception {
        return passwordService.importPasswordsFromXml(fileName);
    }
}