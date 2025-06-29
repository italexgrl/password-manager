package com.passwordmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.model.PasswordListWrapper;
import org.springframework.stereotype.Service;
import jakarta.xml.bind.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Serwis odpowiedzialny za odczyt i zapis danych haseł do/z plików JSON i XML.
 */
@Service
public class FileStorageService {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // Formatowanie JSON dla czytelności

    private static final String DATA_DIRECTORY = "data/"; // Katalog do przechowywania plików
    private final Path dataDirPath = Paths.get(DATA_DIRECTORY);

    private final JAXBContext jaxbContext;

    public FileStorageService() {
        try {
            jaxbContext = JAXBContext.newInstance(PasswordListWrapper.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Błąd inicjalizacji JAXBContext", e);
        }
    }

    /**
     * Odczytuje wpisy haseł z pliku JSON.
     * @param fileName Nazwa pliku JSON (np. "passwords.json").
     * @return Lista wpisów haseł.
     * @throws IOException Jeśli wystąpi błąd wejścia/wyjścia.
     */
    public List<PasswordEntry> readPasswordsFromJson(String fileName) throws IOException {
        File file = dataDirPath.resolve(fileName).toFile();
        if (!file.exists()) {
            return new ArrayList<>(); // Zwróć pustą listę, jeśli plik nie istnieje
        }
        return Arrays.asList(objectMapper.readValue(file, PasswordEntry[].class));
    }

    /**
     * Zapisuje wpisy haseł do pliku JSON.
     * @param passwordEntries Lista wpisów haseł do zapisania.
     * @param fileName Nazwa pliku JSON (np. "passwords.json").
     * @throws IOException Jeśli wystąpi błąd wejścia/wyjścia.
     */
    public void writePasswordsToJson(List<PasswordEntry> passwordEntries, String fileName) throws IOException {
        objectMapper.writeValue(dataDirPath.resolve(fileName).toFile(), passwordEntries);
    }

    /**
     * Odczytuje wpisy haseł z pliku XML.
     * @param fileName Nazwa pliku XML (np. "passwords.xml").
     * @return Lista wpisów haseł.
     * @throws Exception Jeśli wystąpi błąd podczas operacji JAXB.
     */
    public List<PasswordEntry> readPasswordsFromXml(String fileName) throws Exception {
        File file = dataDirPath.resolve(fileName).toFile();
        if (!file.exists()) {
            return new ArrayList<>(); // Zwróć pustą listę, jeśli plik nie istnieje
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(PasswordListWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        PasswordListWrapper wrapper = (PasswordListWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper.getPasswords();
    }

    /**
     * Zapisuje wpisy haseł do pliku XML.
     * @param passwordEntries Lista wpisów haseł do zapisania.
     * @param fileName Nazwa pliku XML (np. "passwords.xml").
     * @throws Exception Jeśli wystąpi błąd podczas operacji JAXB.
     */
    public void writePasswordsToXml(List<PasswordEntry> passwordEntries, String fileName) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(PasswordListWrapper.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // Formatowanie XML dla czytelności

        PasswordListWrapper wrapper;
        wrapper = new PasswordListWrapper(passwordEntries);
        jaxbMarshaller.marshal(wrapper,
                dataDirPath.resolve(fileName).toFile());
    }
}