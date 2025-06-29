package com.passwordmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testy integracyjne (warstwa kontrolera) dla {@link PasswordController}.
 */
@WebMvcTest(PasswordController.class) // Testuje tylko warstwę Web, mockując inne zależności
class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc; // Umożliwia wysyłanie symulowanych żądań HTTP

    @MockBean // Tworzy mocka PasswordService i wstrzykuje go do kontrolera
    private PasswordService passwordService;

    @Autowired
    private ObjectMapper objectMapper; // Do konwersji obiektów na JSON

    @Test
    void testGetAllPasswords() throws Exception {
        PasswordEntry p1 = new PasswordEntry(1L, "google.com", "user1", "pass1");
        PasswordEntry p2 = new PasswordEntry(2L, "facebook.com", "user2", "pass2");
        when(passwordService.getAllPasswords()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/passwords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].website").value("google.com"))
                .andExpect(jsonPath("$[1].username").value("user2"));

        verify(passwordService, times(1)).getAllPasswords();
    }

    @Test
    void testGetAllPasswordsSorted() throws Exception {
        PasswordEntry p1 = new PasswordEntry(1L, "z-site.com", "u1", "p1");
        PasswordEntry p2 = new PasswordEntry(2L, "a-site.com", "u2", "p2");
        List<PasswordEntry> unsorted = Arrays.asList(p1, p2);
        List<PasswordEntry> sorted = Arrays.asList(p2, p1);

        // Kiedy wywołujemy getAllPasswords bez sortowania, zwraca niesortowane
        when(passwordService.getAllPasswords()).thenReturn(new java.util.ArrayList<>(unsorted)); // Zwróć modyfikowalną listę

        // Kiedy wywołujemy sortPasswordsByWebsite, symulujemy sortowanie na liście
        doAnswer(invocation -> {
            List<PasswordEntry> list = invocation.getArgument(0);
            list.sort((e1, e2) -> e1.getWebsite().compareToIgnoreCase(e2.getWebsite()));
            return null;
        }).when(passwordService).sortPasswordsByWebsite(anyList());


        mockMvc.perform(get("/api/passwords?sortByWebsite=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].website").value("a-site.com"))
                .andExpect(jsonPath("$[1].website").value("z-site.com"));

        verify(passwordService, times(1)).getAllPasswords();
        verify(passwordService, times(1)).sortPasswordsByWebsite(anyList());
    }

    @Test
    void testGetPasswordByIdFound() throws Exception {
        PasswordEntry p1 = new PasswordEntry(1L, "google.com", "user1", "pass1");
        when(passwordService.getPasswordById(1L)).thenReturn(Optional.of(p1));

        mockMvc.perform(get("/api/passwords/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.website").value("google.com"));

        verify(passwordService, times(1)).getPasswordById(1L);
    }

    @Test
    void testGetPasswordByIdNotFound() throws Exception {
        when(passwordService.getPasswordById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/passwords/99"))
                .andExpect(status().isNotFound());

        verify(passwordService, times(1)).getPasswordById(99L);
    }

    @Test
    void testCreatePassword() throws Exception {
        PasswordEntry newEntry = new PasswordEntry(null, "new.com", "new_user", "new_pass");
        PasswordEntry savedEntry = new PasswordEntry(1L, "new.com", "new_user", "new_pass"); // Serwis zwraca odszyfrowane hasło
        when(passwordService.savePassword(any(PasswordEntry.class))).thenReturn(savedEntry);

        mockMvc.perform(post("/api/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEntry)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.website").value("new.com"));

        verify(passwordService, times(1)).savePassword(any(PasswordEntry.class));
    }

    @Test
    void testCreatePasswordValidationFail() throws Exception {
        PasswordEntry invalidEntry = new PasswordEntry(null, "ab", "user", "pass"); // Too short website
        mockMvc.perform(post("/api/passwords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEntry)))
                .andExpect(status().isBadRequest()) // Oczekujemy 400 Bad Request
                .andExpect(jsonPath("$.message").exists()); // Sprawdź, czy jest wiadomość o błędzie

        verify(passwordService, never()).savePassword(any(PasswordEntry.class)); // Nie powinno być wywołania serwisu
    }

    @Test
    void testUpdatePassword() throws Exception {
        PasswordEntry updatedData = new PasswordEntry(1L, "updated.com", "updated_user", "updated_pass");
        when(passwordService.updatePassword(eq(1L), any(PasswordEntry.class))).thenReturn(Optional.of(updatedData));

        mockMvc.perform(put("/api/passwords/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.website").value("updated.com"));

        verify(passwordService, times(1)).updatePassword(eq(1L), any(PasswordEntry.class));
    }

    @Test
    void testUpdatePasswordNotFound() throws Exception {
        PasswordEntry updatedData = new PasswordEntry(99L, "updated.com", "updated_user", "updated_pass");
        when(passwordService.updatePassword(eq(99L), any(PasswordEntry.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/passwords/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedData)))
                .andExpect(status().isNotFound());

        verify(passwordService, times(1)).updatePassword(eq(99L), any(PasswordEntry.class));
    }

    @Test
    void testDeletePasswordSuccess() throws Exception {
        when(passwordService.deletePassword(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/passwords/1"))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(passwordService, times(1)).deletePassword(1L);
    }

    @Test
    void testDeletePasswordNotFound() throws Exception {
        when(passwordService.deletePassword(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/passwords/99"))
                .andExpect(status().isNotFound()); // 404 Not Found

        verify(passwordService, times(1)).deletePassword(99L);
    }

    @Test
    void testGenerateSecurePassword() throws Exception {
        String generatedPass = "superSecureGeneratedPass";
        when(passwordService.generateSecurePassword()).thenReturn(generatedPass);

        mockMvc.perform(get("/api/passwords/generate-secure"))
                .andExpect(status().isOk())
                .andExpect(content().string(generatedPass));

        verify(passwordService, times(1)).generateSecurePassword();
    }

    @Test
    void testExportToJson() throws Exception {
        doNothing().when(passwordService).exportPasswordsToJson(anyString());

        mockMvc.perform(get("/api/passwords/export/json")
                        .param("fileName", "test.json"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hasła wyeksportowane do test.json"));

        verify(passwordService, times(1)).exportPasswordsToJson("test.json");
    }

    @Test
    void testExportToJsonError() throws Exception {
        doThrow(new IOException("Test error")).when(passwordService).exportPasswordsToJson(anyString());

        mockMvc.perform(get("/api/passwords/export/json")
                        .param("fileName", "error.json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Błąd podczas eksportu do JSON: Test error"));

        verify(passwordService, times(1)).exportPasswordsToJson("error.json");
    }

    @Test
    void testImportFromJson() throws Exception {
        PasswordEntry importedEntry = new PasswordEntry(1L, "imported.com", "iuser", "ipass");
        when(passwordService.importPasswordsFromJson(anyString())).thenReturn(Collections.singletonList(importedEntry));

        mockMvc.perform(post("/api/passwords/import/json")
                        .param("fileName", "import.json"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].website").value("imported.com"));

        verify(passwordService, times(1)).importPasswordsFromJson("import.json");
    }

    @Test
    void testImportFromXml() throws Exception {
        PasswordEntry importedEntry = new PasswordEntry(1L, "xmlimported.com", "xuser", "xpass");
        when(passwordService.importPasswordsFromXml(anyString())).thenReturn(Collections.singletonList(importedEntry));

        mockMvc.perform(post("/api/passwords/import/xml")
                        .param("fileName", "import.xml"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].website").value("xmlimported.com"));

        verify(passwordService, times(1)).importPasswordsFromXml("import.xml");
    }
}