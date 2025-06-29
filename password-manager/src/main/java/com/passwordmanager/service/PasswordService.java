//package com.passwordmanager.service;
//
//import com.passwordmanager.model.PasswordEntry;
//import com.passwordmanager.repository.PasswordRepository;
//import com.passwordmanager.utill.PasswordGenerator;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Serwis odpowiedzialny za logikę biznesową związaną z zarządzaniem hasłami.
// * Obsługuje operacje CRUD, szyfrowanie/deszyfrowanie oraz generowanie haseł.
// */
//@Service // Oznacza klasę jako komponent serwisowy Springa
//public class PasswordService {
//
//    private final PasswordRepository passwordRepository;
//    private final EncryptionService encryptionService;
//    private final PasswordGenerator passwordGenerator;
//
//    /**
//     * Konstruktor serwisu. Spring automatycznie wstrzykuje zależności.
//     *
//     * @param passwordRepository Repozytorium do dostępu do danych haseł.
//     * @param encryptionService  Serwis do szyfrowania i deszyfrowania haseł.
//     * @param passwordGenerator  Serwis do generowania bezpiecznych haseł.
//     */
//    public PasswordService(PasswordRepository passwordRepository, EncryptionService encryptionService, PasswordGenerator passwordGenerator, FileStorageService fileStorageService) {
//        this.passwordRepository = passwordRepository;
//        this.encryptionService = encryptionService;
//        this.passwordGenerator = passwordGenerator;
//        this.fileStorageService = fileStorageService;
//    }
//
//    /**
//     * Pobiera wszystkie wpisy haseł z bazy danych.
//     * Każde zaszyfrowane hasło jest deszyfrowane przed zwróceniem.
//     *
//     * @return Lista wszystkich wpisów haseł, z odszyfrowanymi hasłami.
//     */
//    public List<PasswordEntry> getAllPasswords() {
//        List<PasswordEntry> passwords = passwordRepository.findAll();
//        // Deszyfruj każde hasło przed zwróceniem
//        passwords.forEach(p -> p.setEncryptedPassword(encryptionService.decrypt(p.getEncryptedPassword())));
//        return passwords;
//    }
//
//    /**
//     * Pobiera wpis hasła po jego ID.
//     * Jeśli wpis zostanie znaleziony, zaszyfrowane hasło jest deszyfrowane.
//     *
//     * @param id ID wpisu hasła.
//     * @return Optional zawierający wpis hasła z odszyfrowanym hasłem, jeśli znaleziono, w przeciwnym razie pusty Optional.
//     */
//    public Optional<PasswordEntry> getPasswordById(Long id) {
//        return passwordRepository.findById(id)
//                .map(p -> {
//                    // Deszyfruj hasło przed zwróceniem
//                    p.setEncryptedPassword(encryptionService.decrypt(p.getEncryptedPassword()));
//                    return p;
//                });
//    }
//
//    /**
//     * Zapisuje nowy wpis hasła lub aktualizuje istniejący.
//     * Hasło jest szyfrowane przed zapisaniem w bazie danych.
//     *
//     * @param passwordEntry Wpis hasła do zapisania (nowy lub zaktualizowany).
//     * @return Zapisany wpis hasła (z zaszyfrowanym hasłem w bazie, ale z odszyfrowanym w zwróconym obiekcie).
//     */
//    public PasswordEntry savePassword(PasswordEntry passwordEntry) {
//        // Szyfruj hasło przed zapisaniem
//        String encryptedPass = encryptionService.encrypt(passwordEntry.getEncryptedPassword());
//        passwordEntry.setEncryptedPassword(encryptedPass);
//        PasswordEntry savedEntry = passwordRepository.save(passwordEntry);
//        // Zwróć odszyfrowane hasło w obiekcie, który zostanie zwrócony do klienta
//        savedEntry.setEncryptedPassword(encryptionService.decrypt(savedEntry.getEncryptedPassword()));
//        return savedEntry;
//    }
//
//    /**
//     * Aktualizuje istniejący wpis hasła.
//     *
//     * @param id                   ID wpisu hasła do zaktualizowania.
//     * @param updatedPasswordEntry Nowe dane wpisu hasła.
//     * @return Optional zawierający zaktualizowany wpis hasła, jeśli znaleziono, w przeciwnym razie pusty Optional.
//     */
//    public Optional<PasswordEntry> updatePassword(Long id, PasswordEntry updatedPasswordEntry) {
//        return passwordRepository.findById(id).map(existingEntry -> {
//            existingEntry.setWebsite(updatedPasswordEntry.getWebsite());
//            existingEntry.setUsername(updatedPasswordEntry.getUsername());
//            // Szyfruj nowe hasło przed zapisaniem
//            String encryptedPass = encryptionService.encrypt(updatedPasswordEntry.getEncryptedPassword());
//            existingEntry.setEncryptedPassword(encryptedPass);
//            PasswordEntry savedEntry = passwordRepository.save(existingEntry);
//            // Deszyfruj hasło przed zwróceniem
//            savedEntry.setEncryptedPassword(encryptionService.decrypt(savedEntry.getEncryptedPassword()));
//            return savedEntry;
//        });
//    }
//
//    /**
//     * Usuwa wpis hasła po jego ID.
//     *
//     * @param id ID wpisu hasła do usunięcia.
//     * @return True, jeśli wpis został usunięty, false, jeśli nie znaleziono wpisu.
//     */
//    public boolean deletePassword(Long id) {
//        if (passwordRepository.existsById(id)) {
//            passwordRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Generuje bezpieczne hasło o domyślnej długości (np. 16 znaków).
//     *
//     * @return Wygenerowane bezpieczne hasło.
//     */
//    public String generateSecurePassword() {
//        return passwordGenerator.generateSecurePassword(16); // Domyślna długość 16
//    }
//
//    // ... inne metody
//
//    /**
//     * Sortuje listę wpisów haseł alfabetycznie według nazwy strony internetowej
//     * przy użyciu algorytmu QuickSort.
//     *
//     * @param passwordEntries Lista wpisów haseł do posortowania.
//     */
//    public void sortPasswordsByWebsite(List<PasswordEntry> passwordEntries) {
//        quickSort(passwordEntries, 0, passwordEntries.size() - 1);
//    }
//
//    // --- Implementacja QuickSort (Algorytm Nieliniowy) ---
//    private void quickSort(List<PasswordEntry> arr, int low, int high) {
//        if (low < high) {
//            int pi = partition(arr, low, high);
//            quickSort(arr, low, pi - 1);
//            quickSort(arr, pi + 1, high);
//        }
//    }
//
//    private int partition(List<PasswordEntry> arr, int low, int high) {
//        PasswordEntry pivot = arr.get(high); // Wybieramy ostatni element jako pivot
//        int i = (low - 1); // Indeks mniejszego elementu
//
//        for (int j = low; j < high; j++) {
//            // Porównujemy nazwy stron internetowych bez względu na wielkość liter
//            if (arr.get(j).getWebsite().compareToIgnoreCase(pivot.getWebsite()) < 0) {
//                i++;
//                // Zamień arr[i] i arr[j]
//                PasswordEntry temp = arr.get(i);
//                arr.set(i, arr.get(j));
//                arr.set(j, temp);
//            }
//        }
//
//        // Zamień arr[i+1] i arr[high] (pivot)
//        PasswordEntry temp = arr.get(i + 1);
//        arr.set(i + 1, arr.get(high));
//        arr.set(high, temp);
//
//        return i + 1;
//    }
//
//    /**
//     * Eksportuje wszystkie aktualnie przechowywane hasła do pliku XML.
//     * Hasła są deszyfrowane przed eksportem.
//     *
//     * @param fileName Nazwa pliku XML do zapisu (np. "export.xml").
//     * @throws Exception Jeśli wystąpi błąd podczas zapisu pliku.
//     */
//    public void exportPasswordsToXml(String fileName) throws Exception {
//        List<PasswordEntry> passwords = getAllPasswords(); // Pobierz odszyfrowane hasła
//        fileStorageService.writePasswordsToXml(passwords, fileName);
//    }
//
//    public void exportPasswordsToJson(String fileName) {
//    }
//
//    public List<PasswordEntry> importPasswordsFromJson(String fileName) {
//        return null;
//    }
//
//    public List<PasswordEntry> importPasswordsFromXml(String s) {
//        return null;
//    }
//
//    private final FileStorageService fileStorageService;
//}

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



