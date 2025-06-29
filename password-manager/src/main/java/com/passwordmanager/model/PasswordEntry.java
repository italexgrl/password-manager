//package com.passwordmanager.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//
///**
// * Encja reprezentująca pojedynczy wpis hasła w systemie.
// * Zawiera info o stronie internetowej, nazwie użytkownika
// * i zaszyfrowanym haśle.
// * Dodano adnotacje JAXB do serializacji/deserializacji XML.
// */
//@Entity
//@Table(name = "password_entries")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@XmlRootElement(name = "passwordEntry") // Określa nazwę elementu głównego dla XML
//@XmlAccessorType(XmlAccessType.FIELD) // Określa, że JAXB będzie używał pól klasy
//public class PasswordEntry {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotBlank(message = "Nazwa strony internetowej nie może być pusta")
//    @Size(min = 3, max = 100, message = "Nazwa strony internetowej musi mieć od 3 do 100 znaków")
//    @Column(nullable = false)
//    @XmlElement(name = "website") // Określa nazwę elementu XML dla tego pola
//    private String website;
//
//    @NotBlank(message = "Nazwa użytkownika nie może być pusta")
//    @Size(min = 3, max = 100, message = "Nazwa użytkownika musi mieć od 3 do 100 znaków")
//    @Column(nullable = false)
//    @XmlElement(name = "username")
//    private String username;
//
//    @NotBlank(message = "Hasło nie może być puste")
//    @Size(min = 8, max = 255, message = "Zaszyfrowane hasło musi mieć od 8 do 255 znaków")
//    @Column(nullable = false)
//    @XmlElement(name = "encryptedPassword")
//    private String encryptedPassword;
//
//    public PasswordEntry(String website, String username, String encryptedPassword) {
//        this.website = website;
//        this.username = username;
//        this.encryptedPassword = encryptedPassword;
//    }
//}
package com.passwordmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@Entity
@XmlRootElement(name = "passwordEntry")
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String website;
    private String password; // zaszyfrowane hasło

    public PasswordEntry() {
    }

    public PasswordEntry(String username, String website, String password) {
        this.username = username;
        this.website = website;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @XmlElement
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return password;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.password = encryptedPassword;
    }

    private String encryptedPassword;
    public PasswordEntry(Long id, String website, String username, String encryptedPassword) {
        this.id = id;
        this.website = website;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }
}