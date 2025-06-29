
# 🔐 Password Manager API

Aplikacja webowa typu **REST API** do bezpiecznego przechowywania i zarządzania hasłami. Umożliwia operacje CRUD, szyfrowanie danych, generowanie bezpiecznych haseł oraz zapis danych do plików JSON i XML.
---

## 🧩 Funkcjonalności
- ✅ Przechowywanie danych logowania do momentu zamknięcia aplikacji (strona, login, hasło)
- ✅ Szyfrowanie/odszyfrowywanie haseł (jeśli zaimplementowane)
- ✅ Algorytm QuickSort do sortowania wpisów
- ✅ Import/eksport haseł w formacie JSON i XML
- ✅ Generowanie bezpiecznych haseł
- ✅ REST API – pełen zestaw operacji CRUD
- ✅ Dokumentacja kodu (Javadoc)
- ✅ Struktura projektu oparta na Mavenie
- ✅ Gotowość do wdrożenia lub rozwoju frontendu/ warstwy wizualnej

---

## 📁 Struktura projektu (Spring Boot)

```
password-manager/
├── .idea/
├── password-manager/
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── passwordmanager/
│   │   │   │           ├── config/          # Konfiguracje aplikacji (np. bezpieczeństwo, szyfrowanie)
│   │   │   │           ├── controller/      # Kontrolery REST API
│   │   │   │           ├── model/           # Modele danych (np. PasswordEntry)
│   │   │   │           ├── repository/      # Repozytoria JPA (interfejsy do bazy danych)
│   │   │   │           ├── service/         # Logika biznesowa (np. szyfrowanie, generowanie haseł)
│   │   │   │           └── utill/            # Klasy pomocnicze (np. en/dekryptory)
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/                        # Testy jednostkowe (JUnit 5)
│
├── target/                                  # Katalog wygenerowany po budowaniu
├── .gitignore
├── .gitattributes
├── mvnw                                     # Wrapper Maven (Linux/Mac)
├── mvnw.cmd                                 # Wrapper Maven (Windows)
├── pom.xml                                  # Plik konfiguracyjny Maven
├── README.md
├── HELP.md

```

---
## ⚙️ Wymagania techniczne

- Java 17+
- Maven 3.8+
- Spring Boot 3.x
- IntelliJ / Eclipse / VS Code
- PowerShell lub terminal

---

## 🚀 Jak uruchomić projekt (PowerShell)

### 1. Sklonuj repozytorium

```powershell
git clone https://github.com/java-classroom-wit/tzs-zaliczenie-italexgrl.git
cd https://github.com/java-classroom-wit/tzs-zaliczenie-italexgrl
```

### 2. Zbuduj projekt Mavenem

```powershell
mvn clean install
```

### 3. Uruchom aplikację lokalnie

```powershell
mvn spring-boot:run
```

### 4. Aplikacja będzie dostępna pod adresem:

```
http://localhost:8080
```
---

## 📡 Endpointy REST API

| Metoda | Endpoint                     | Opis                                      |
|--------|------------------------------|-------------------------------------------|
| GET    | `/api/passwords`            | Pobierz wszystkie hasła                   |
| GET    | `/api/passwords?sortByWebsite=true` | Pobierz posortowane hasła (QuickSort) |
| GET    | `/api/passwords/{id}`       | Pobierz hasło po ID                       |
| POST   | `/api/passwords`            | Dodaj nowe hasło                          |
| PUT    | `/api/passwords/{id}`       | Zaktualizuj hasło                         |
| DELETE | `/api/passwords/{id}`       | Usuń hasło                                |
| GET    | `/api/passwords/generate-secure` | Wygeneruj silne hasło                |
| GET    | `/api/passwords/export/json?fileName=plik.json` | Eksport do JSON         |
| GET    | `/api/passwords/export/xml?fileName=plik.xml`   | Eksport do XML          |
| POST   | `/api/passwords/import/json?fileName=plik.json` | Import z JSON            |
| POST   | `/api/passwords/import/xml?fileName=plik.xml`   | Import z XML             |

## 📚 Technologie i biblioteki

- Spring Boot
- Spring Data JPA
- H2 
- Jackson (JSON)
- JAXB (XML)
- JUnit 5 (testy)
---

## 🧠 Autor
aleksandra napierska

Projekt stworzony jako aplikacja zaliczeniowa przedmiot .  
Repozytorium: (https://github.com/italexgrl/password-manager)

---
