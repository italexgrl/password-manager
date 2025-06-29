
package com.passwordmanager.service;

import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.repository.PasswordRepository;
import com.passwordmanager.utill.PasswordGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final EncryptionService encryptionService;
    private final PasswordGenerator passwordGenerator;
    private final FileStorageService fileStorageService;

    public PasswordService(PasswordRepository passwordRepository,
                           EncryptionService encryptionService,
                           PasswordGenerator passwordGenerator,
                           FileStorageService fileStorageService) {
        this.passwordRepository = passwordRepository;
        this.encryptionService = encryptionService;
        this.passwordGenerator = passwordGenerator;
        this.fileStorageService = fileStorageService;
    }

    public List<PasswordEntry> getAllPasswords() {
        List<PasswordEntry> passwords = passwordRepository.findAll();
        passwords.forEach(p -> p.setEncryptedPassword(encryptionService.decrypt(p.getEncryptedPassword())));
        return passwords;
    }

    public Optional<PasswordEntry> getPasswordById(Long id) {
        return passwordRepository.findById(id)
                .map(p -> {
                    p.setEncryptedPassword(encryptionService.decrypt(p.getEncryptedPassword()));
                    return p;
                });
    }

    public PasswordEntry savePassword(PasswordEntry passwordEntry) {
        String encryptedPass = encryptionService.encrypt(passwordEntry.getEncryptedPassword());
        passwordEntry.setEncryptedPassword(encryptedPass);
        PasswordEntry savedEntry = passwordRepository.save(passwordEntry);
        savedEntry.setEncryptedPassword(encryptionService.decrypt(savedEntry.getEncryptedPassword()));
        return savedEntry;
    }

    public Optional<PasswordEntry> updatePassword(Long id, PasswordEntry updatedPasswordEntry) {
        return passwordRepository.findById(id).map(existingEntry -> {
            existingEntry.setWebsite(updatedPasswordEntry.getWebsite());
            existingEntry.setUsername(updatedPasswordEntry.getUsername());
            String encryptedPass = encryptionService.encrypt(updatedPasswordEntry.getEncryptedPassword());
            existingEntry.setEncryptedPassword(encryptedPass);
            PasswordEntry savedEntry = passwordRepository.save(existingEntry);
            savedEntry.setEncryptedPassword(encryptionService.decrypt(savedEntry.getEncryptedPassword()));
            return savedEntry;
        });
    }

    public boolean deletePassword(Long id) {
        if (passwordRepository.existsById(id)) {
            passwordRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public String generateSecurePassword() {
        return passwordGenerator.generateSecurePassword(16);
    }

    public void sortPasswordsByWebsite(List<PasswordEntry> passwordEntries) {
        quickSort(passwordEntries, 0, passwordEntries.size() - 1);
    }

    private void quickSort(List<PasswordEntry> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(List<PasswordEntry> arr, int low, int high) {
        PasswordEntry pivot = arr.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr.get(j).getWebsite().compareToIgnoreCase(pivot.getWebsite()) < 0) {
                i++;
                PasswordEntry temp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, temp);
            }
        }
        PasswordEntry temp = arr.get(i + 1);
        arr.set(i + 1, arr.get(high));
        arr.set(high, temp);
        return i + 1;
    }

    // ---------- Export / Import ----------

    public void exportPasswordsToJson(String fileName) throws IOException {
        List<PasswordEntry> passwords = getAllPasswords(); // already decrypted
        fileStorageService.writePasswordsToJson(passwords, fileName);
    }

    public void exportPasswordsToXml(String fileName) throws Exception {
        List<PasswordEntry> passwords = getAllPasswords(); // already decrypted
        fileStorageService.writePasswordsToXml(passwords, fileName);
    }

    public List<PasswordEntry> importPasswordsFromJson(String fileName) throws IOException {
        List<PasswordEntry> imported = fileStorageService.readPasswordsFromJson(fileName);
        // Zapisz każde hasło z szyfrowaniem
        imported.forEach(this::savePassword);
        return getAllPasswords();
    }

    public List<PasswordEntry> importPasswordsFromXml(String fileName) throws Exception {
        List<PasswordEntry> imported = fileStorageService.readPasswordsFromXml(fileName);
        imported.forEach(this::savePassword);
        return getAllPasswords();
    }
}



