package com.passwordmanager.repository;

import com.passwordmanager.model.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium do zarządzania operacjami CRUD na encjach {@link PasswordEntry}.
 */
@Repository // Oznacza interfejs jako komponent repozytorium Springa
public interface PasswordRepository extends JpaRepository<PasswordEntry, Long> {
    // Spring Data JPA automatycznie udostępnia metody CRUD
    // np. save(), findById(), findAll(), deleteById()
}