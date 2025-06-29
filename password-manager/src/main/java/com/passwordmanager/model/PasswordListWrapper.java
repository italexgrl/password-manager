//package com.passwordmanager.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//import java.util.List;
//
///**
// * Klasa pomocnicza służąca do opakowywania listy obiektów {@link PasswordEntry}
// */
//@XmlRootElement(name = "passwordEntries") // Główny element dla kolekcji
//@XmlAccessorType(XmlAccessType.FIELD)
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class PasswordListWrapper {
//
//    @XmlElement(name = "passwordEntry") // Element dla każdego PasswordEntry w liście
//    private List<PasswordEntry> passwords;
//}
package com.passwordmanager.model;

import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "passwords")
public class PasswordListWrapper {

    private List<PasswordEntry> passwords;

    public PasswordListWrapper() {
    }

    public PasswordListWrapper(List<PasswordEntry> passwords) {
        this.passwords = passwords;
    }

    @XmlElement(name = "passwordEntry")
    public List<PasswordEntry> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<PasswordEntry> passwords) {
        this.passwords = passwords;
    }
}
