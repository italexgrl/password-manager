
# 🔐 Password Manager API

REST API do bezpiecznego zarządzania hasłami. Aplikacja umożliwia przechowywanie, szyfrowanie, import/eksport danych, generowanie silnych haseł oraz wykonywanie operacji CRUD. Projekt stworzony w technologii **Spring Boot** z wykorzystaniem zasad **OOP**, wzorców projektowych i algorytmu **QuickSort**.

---

## 🧩 Funkcjonalności

- ✅ Przechowywanie danych logowania (strona, login, hasło)
- ✅ Szyfrowanie/odszyfrowywanie haseł (jeśli zaimplementowane)
- ✅ Algorytm QuickSort do sortowania wpisów
- ✅ Import/eksport haseł w formacie JSON i XML
- ✅ Generowanie bezpiecznych haseł
- ✅ REST API – pełen zestaw operacji CRUD
- ✅ Dokumentacja kodu (Javadoc)
- ✅ Struktura projektu oparta na Mavenie
- ✅ Gotowość do wdrożenia lub rozwoju frontendu

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
│   │   │   │           └── util/            # Klasy pomocnicze (np. en/dekryptory)
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

---

## 🧪 Testy jednostkowe

- Zrealizowane w JUnit 5 (jeśli dostępne).

```powershell
mvn test
```

---

## 📚 Technologie i biblioteki

- Spring Boot
- Spring Data JPA
- H2 / PostgreSQL / MySQL (dowolna baza)
- Jackson (JSON)
- JAXB (XML)
- Lombok (opcjonalnie)
- JUnit 5 (testy)

---

## 🧠 Autor

Projekt stworzony jako aplikacja zaliczeniowa przedmiot .  
Repozytorium: [github.com/twoj-login/twoje-repozytorium](https://github.com/java-classroom-wit/tzs-zaliczenie-italexgrl)

---
