# Plan REST API

## 1. Zasoby

- **Użytkownicy**
  - *Tabela bazy danych*: `users`
  - Pola: `id`, `username`, `password`, `roles`.
  - Zarządzane przez Spring Security; operacje takie jak rejestracja i logowanie obsługiwane przez dedykowane endpointy i konfigurację Spring Security.

- **Produkty**
  - *Tabela bazy danych*: `products`
  - Pola: `id`, `name`, `sku`, `description`, `unit`, `minOrderLevel`, `unitPrice`, `deleted`, `quantity`, `createdBy`.
  - Główny zasób reprezentujący pozycje magazynowe ze śledzeniem stanów i minimalnych poziomów zamówienia.

- **Przyjęcia produktów**
  - *Tabela bazy danych*: `product_arrivals`
  - Pola: `id`, `product_id`, `quantity`, `source`, `arrivalDate`, `createdBy`.
  - Śledzi przychodzące dostawy i aktualizuje ilości produktów.

- **Korekty stanów**
  - *Tabela bazy danych*: `stock_corrections`
  - Pola: `id`, `product_id`, `quantity`, `reason`, `correctionDate`, `createdBy`.
  - Rejestruje ręczne korekty magazynowe z obowiązkowym uzasadnieniem dla celów audytowych.

## 2. Endpointy

### 2.1. Uwierzytelnianie

- **GET `/login`**
  - **Opis**: Wyświetla formularz logowania dla uwierzytelnienia użytkownika.
  - **Odpowiedź**: Strona HTML z formularzem logowania.
  - **Logika biznesowa**:
    - Jeśli użytkownik jest już uwierzytelniony, przekierowuje na stronę główną.
  - **Błędy**: Brak (publiczny endpoint).

- **POST `/login`** (obsługiwane przez Spring Security)
  - **Opis**: Uwierzytelnia dane logowania użytkownika.
  - **Parametry formularza**:
    - `username`: Nazwa użytkownika.
    - `password`: Hasło użytkownika.
  - **Odpowiedź**: Przekierowanie na stronę główną w przypadku sukcesu lub powrót do logowania z błędem.
  - **Błędy**: 401 Unauthorized dla nieprawidłowych danych logowania.

- **POST `/logout`** (obsługiwane przez Spring Security)
  - **Opis**: Wylogowuje bieżącego użytkownika.
  - **Odpowiedź**: Przekierowanie na stronę logowania.

- **GET `/profile`**
  - **Opis**: Wyświetla informacje o profilu użytkownika.
  - **Odpowiedź**: Strona HTML z nazwą użytkownika i rolami.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 401 Unauthorized jeśli nie uwierzytelniony.

### 2.2. Produkty

- **GET `/products`**
  - **Opis**: Pobiera listę wszystkich aktywnych (nieusuniętych) produktów.
  - **Odpowiedź**: Strona HTML z listą produktów.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 401 Unauthorized.

- **GET `/products/new`**
  - **Opis**: Wyświetla formularz do utworzenia nowego produktu.
  - **Odpowiedź**: Formularz HTML z pustymi polami produktu.
  - **Uwierzytelnianie**: Wymagane.

- **POST `/products`**
  - **Opis**: Tworzy nowy produkt.
  - **Parametry formularza**:
    - `name` (wymagane): Nazwa produktu.
    - `sku` (wymagane, unikalne): Identyfikator jednostki magazynowej.
    - `description` (opcjonalne): Opis produktu.
    - `unit` (wymagane): Jednostka miary (np. "szt", "kg").
    - `minOrderLevel` (domyślnie: 0, min: 0): Minimalny poziom stanu dla alertów o zamówieniu.
    - `unitPrice` (opcjonalne): Cena za jednostkę.
  - **Odpowiedź**: Przekierowanie na listę produktów z komunikatem sukcesu.
  - **Walidacje**:
    - `name`: Nie może być puste.
    - `sku`: Nie może być puste, musi być unikalne.
    - `unit`: Nie może być puste.
    - `minOrderLevel`: Musi być >= 0.
  - **Logika biznesowa**:
    - Początkowa wartość `quantity` ustawiana jest na 0.
    - `createdBy` ustawiane jest na nazwę uwierzytelnionego użytkownika.
  - **Błędy**: 400 dla błędów walidacji, 401 Unauthorized.

- **GET `/products/{id}`**
  - **Opis**: Wyświetla szczegółowe informacje o konkretnym produkcie.
  - **Odpowiedź**: Strona HTML ze szczegółami produktu.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 404 Not Found, 401 Unauthorized.

- **GET `/products/{id}/edit`**
  - **Opis**: Wyświetla formularz do edycji istniejącego produktu.
  - **Odpowiedź**: Formularz HTML wypełniony danymi produktu.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 404 Not Found, 401 Unauthorized.

- **POST `/products/{id}`**
  - **Opis**: Aktualizuje istniejący produkt.
  - **Parametry formularza**: Takie same jak POST `/products`.
  - **Odpowiedź**: Przekierowanie na listę produktów z komunikatem sukcesu.
  - **Logika biznesowa**:
    - Ilość nie może być edytowana bezpośrednio (tylko przez przyjęcia lub korekty).
  - **Walidacje**: Takie same jak POST `/products`.
  - **Błędy**: 400 dla błędów walidacji, 404 Not Found, 401 Unauthorized.

- **POST `/products/{id}/delete`**
  - **Opis**: Miękkie usunięcie produktu (oznacza jako usunięty, nie usuwa z bazy danych).
  - **Odpowiedź**: Przekierowanie na listę produktów z komunikatem sukcesu.
  - **Logika biznesowa**:
    - Ustawia flagę `deleted` na true zamiast usuwać rekord.
  - **Błędy**: 404 Not Found, 401 Unauthorized.

### 2.3. Przyjęcia produktów

- **GET `/arrivals/new/{productId}`**
  - **Opis**: Wyświetla formularz do rejestracji nowego przyjęcia produktu.
  - **Odpowiedź**: Formularz HTML z informacjami o produkcie.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 404 jeśli produkt nie został znaleziony, 401 Unauthorized.

- **POST `/arrivals`**
  - **Opis**: Rejestruje nowe przyjęcie produktu i aktualizuje stan magazynowy.
  - **Parametry formularza**:
    - `productId` (wymagane, min: 1): ID produktu.
    - `quantity` (wymagane, min: 1): Otrzymana ilość.
    - `source` (wymagane): Dostawca lub źródło przyjęcia.
  - **Odpowiedź**: Przekierowanie na stronę historii produktu z komunikatem sukcesu.
  - **Walidacje**:
    - `productId`: Musi być >= 1.
    - `quantity`: Musi być >= 1.
    - `source`: Nie może być puste.
  - **Logika biznesowa**:
    - Aktualizuje ilość produktu poprzez dodanie ilości przyjęcia.
    - Automatycznie rejestruje datę przyjęcia.
    - Rejestruje `createdBy` jako nazwę uwierzytelnionego użytkownika.
  - **Błędy**: 400 dla błędów walidacji, 404 jeśli produkt nie został znaleziony, 401 Unauthorized.

- **GET `/arrivals/product/{productId}`**
  - **Opis**: Wyświetla wszystkie przyjęcia dla konkretnego produktu.
  - **Odpowiedź**: Strona HTML z historią przyjęć.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 404 jeśli produkt nie został znaleziony, 401 Unauthorized.

### 2.4. Korekty stanów

- **GET `/corrections/new/{productId}`**
  - **Opis**: Wyświetla formularz do rejestracji korekty stanu.
  - **Odpowiedź**: Formularz HTML z informacjami o produkcie.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 404 jeśli produkt nie został znaleziony, 401 Unauthorized.

- **POST `/corrections`**
  - **Opis**: Rejestruje korektę stanu i aktualizuje ilość produktu.
  - **Parametry formularza**:
    - `productId` (wymagane): ID produktu.
    - `quantity` (wymagane): Wartość korekty (może być dodatnia lub ujemna).
    - `reason` (wymagane): Wyjaśnienie powodu korekty.
  - **Odpowiedź**: Przekierowanie na stronę historii produktu z komunikatem sukcesu.
  - **Walidacje**:
    - `productId`: Musi być prawidłowe.
    - `quantity`: Dowolna liczba całkowita (dodatnia lub ujemna).
    - `reason`: Nie może być puste, maksymalnie 500 znaków.
  - **Logika biznesowa**:
    - Aktualizuje ilość produktu poprzez dodanie wartości korekty.
    - Automatycznie rejestruje datę korekty.
    - Rejestruje `createdBy` jako nazwę uwierzytelnionego użytkownika.
    - Obowiązkowy ślad audytowy z podaniem powodu.
  - **Błędy**: 400 dla błędów walidacji (obsługiwane przez ControllerAdvice), 404 jeśli produkt nie został znaleziony, 401 Unauthorized.

### 2.5. Historia

- **GET `/history/{productId}`**
  - **Opis**: Wyświetla połączoną historię przyjęć i korekt dla konkretnego produktu.
  - **Odpowiedź**: Strona HTML z chronologiczną listą wszystkich ruchów magazynowych.
  - **Uwierzytelnianie**: Wymagane.
  - **Logika biznesowa**:
    - Wyświetla zarówno przyjęcia produktów jak i korekty stanów w jednym widoku.
  - **Błędy**: 404 jeśli produkt nie został znaleziony, 401 Unauthorized.

### 2.6. Alerty niskich stanów

- **GET `/alerts/low-stock`**
  - **Opis**: Wyświetla formularz do generowania raportu niskich stanów.
  - **Odpowiedź**: Strona HTML z formularzem wprowadzania progu.
  - **Uwierzytelnianie**: Wymagane.

- **POST `/alerts/low-stock`**
  - **Opis**: Generuje raport produktów poniżej określonego progu.
  - **Parametry formularza**:
    - `threshold` (wymagane): Minimalny poziom stanu do sprawdzenia.
  - **Odpowiedź**: Strona HTML z listą produktów poniżej progu.
  - **Logika biznesowa**:
    - Porównuje bieżącą ilość produktu z podanym progiem.
    - Zwraca produkty gdzie `quantity < threshold`.
    - **Główna funkcjonalność biznesowa**: Umożliwia proaktywne zarządzanie magazynem.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 401 Unauthorized.

- **GET `/alerts/low-stock/export`**
  - **Opis**: Eksportuje raport niskich stanów jako plik CSV.
  - **Parametry zapytania**:
    - `threshold` (wymagane): Minimalny poziom stanu do sprawdzenia.
  - **Odpowiedź**: Pobranie pliku CSV z nagłówkami: Nazwa produktu, SKU, Bieżąca ilość, Minimalny poziom zamówienia.
  - **Logika biznesowa**:
    - Generuje raport CSV produktów poniżej progu.
    - Nazwa pliku zawiera znacznik czasu: `low_stock_report_YYYYMMDD_HHmmss.csv`.
    - **Główna funkcjonalność biznesowa**: Dostarcza dane eksportowalne dla decyzji zakupowych.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 401 Unauthorized.

### 2.7. Health Check

- **GET `/health`**
  - **Opis**: Prosty endpoint sprawdzania zdrowia aplikacji do monitorowania dostępności.
  - **Odpowiedź JSON**:
    ```json
    {
      "status": "OK"
    }
    ```
  - **Uwierzytelnianie**: Nie wymagane (publiczny endpoint).
  - **Przypadek użycia**: Używany przez pipeline wdrożeniowy i systemy monitorujące do weryfikacji, czy aplikacja działa.

### 2.8. Strona główna

- **GET `/`**
  - **Opis**: Strona główna z przeglądem aplikacji i opisem funkcjonalności.
  - **Odpowiedź**: Strona HTML z nawigacją i podsumowaniem funkcji.
  - **Uwierzytelnianie**: Wymagane.
  - **Błędy**: 401 Unauthorized (przekierowanie do logowania).

## 3. Uwierzytelnianie i autoryzacja

- **Mechanizm**: Uwierzytelnianie oparte na formularzach przy użyciu Spring Security z zarządzaniem sesją.
- **Proces**:
  - Użytkownicy uwierzytelniają się przez formularz `/login`.
  - Dane logowania są weryfikowane względem tabeli `users`.
  - Hasła są przechowywane przy użyciu hashowania BCrypt.
  - Sesja jest utrzymywana przez ciasteczka sesji HTTP.
  - Chronione endpointy wymagają uwierzytelnionej sesji.
- **Zarządzanie użytkownikami**:
  - Użytkownicy są przechowywani w tabeli `users` z rolami.
  - Obecnie obsługiwana rola `ROLE_USER`.
  - Wszyscy uwierzytelnieni użytkownicy mają dostęp do wszystkich funkcji (ograniczenia oparte na rolach jeszcze nie zaimplementowane).
- **Dodatkowe kwestie bezpieczeństwa**:
  - HTTPS wymuszany w produkcji przez proxy Cloudflare.
  - Ochrona CSRF włączona dla wszystkich operacji zmieniających stan (POST, PUT, DELETE).
  - Wylogowanie unieważnia sesję.

## 4. Walidacja i logika biznesowa

- **Reguły walidacji**:
  - **Produkty**:
    - `name`: Nie może być puste.
    - `sku`: Nie może być puste, musi być unikalne wśród wszystkich produktów.
    - `unit`: Nie może być puste.
    - `minOrderLevel`: Musi być >= 0.
    - `quantity`: Musi być >= 0, nie może być edytowane bezpośrednio.
  - **Przyjęcia produktów**:
    - `productId`: Musi być >= 1 i musi odnosić się do istniejącego produktu.
    - `quantity`: Musi być >= 1.
    - `source`: Nie może być puste.
  - **Korekty stanów**:
    - `productId`: Musi odnosić się do istniejącego produktu.
    - `quantity`: Dowolna liczba całkowita (może być ujemna dla redukcji).
    - `reason`: Nie może być puste, maksymalnie 500 znaków.
  - **Raporty niskich stanów**:
    - `threshold`: Musi być prawidłową liczbą całkowitą.

- **Implementacja logiki biznesowej**:
  - **Zarządzanie produktami**:
    - Nowe produkty rozpoczynają z ilością 0.
    - Ilość może być zmieniana tylko przez przyjęcia lub korekty, nie przez bezpośrednią edycję.
    - Miękkie usuwanie zachowuje dane historyczne.
    - Pole `createdBy` śledzi, kto utworzył produkt.
  
  - **Przyjęcia produktów**:
    - Automatycznie zwiększa ilość produktu.
    - Rejestruje znacznik czasu i użytkownika, który zarejestrował przyjęcie.
    - Tworzy wpis śladu audytowego w tabeli `product_arrivals`.
  
  - **Korekty stanów**:
    - Może dostosować ilość w górę lub w dół.
    - Obowiązkowe pole powodu dla zgodności z audytem.
    - Rejestruje znacznik czasu i użytkownika, który dokonał korekty.
    - Tworzy wpis śladu audytowego w tabeli `stock_corrections`.
  
  - **Alerty niskich stanów** (główna funkcjonalność biznesowa):
    - Porównuje bieżące poziomy stanów z progiem zdefiniowanym przez użytkownika.
    - Identyfikuje produkty wymagające ponownego zamówienia.
    - Obsługuje eksport CSV do integracji z systemami zaopatrzenia.
    - Umożliwia proaktywne zarządzanie zapasami, aby zapobiec brakom w magazynie.
  
  - **Śledzenie historii**:
    - Połączony widok wszystkich ruchów magazynowych dla produktu.
    - Obejmuje zarówno przyjęcia jak i korekty.
    - Posortowane chronologicznie dla śladu audytowego.

## 5. Obsługa błędów

- **ControllerAdvice**: Globalny handler wyjątków dla spójnych odpowiedzi błędów.
- **Błędy walidacji**: Powrót do formularza z komunikatami błędów do korekty przez użytkownika.
- **Błędy Not Found**: Status 404 z odpowiednią stroną błędu.
- **Nieautoryzowany dostęp**: Przekierowanie do strony logowania ze statusem 401.
- **Błędy serwera**: Ogólna strona błędu bez ujawniania wrażliwych szczegółów.

## 6. Stos technologiczny

- **Framework backendowy**: Spring Boot 3.x (Kotlin)
- **Silnik szablonów**: Thymeleaf do renderowania HTML po stronie serwera
- **Bezpieczeństwo**: Spring Security z uwierzytelnianiem opartym na formularzach
- **Baza danych**: PostgreSQL (konfigurowalne przez application.properties)
- **ORM**: Spring Data JPA z Hibernate
- **Narzędzie budowania**: Gradle
- **Walidacja**: Jakarta Bean Validation (JSR-380)

## 7. Przyszłe ulepszenia API

- **RESTful JSON API**: Dodanie endpointów REST obok widoków HTML dla klientów mobilnych/SPA.
- **Paginacja**: Dodanie wsparcia paginacji dla list produktów.
- **Wyszukiwanie i filtrowanie**: Zaawansowane możliwości wyszukiwania produktów.
- **Operacje wsadowe**: Masowy import/eksport produktów.
- **Wersjonowanie API**: Wersjonowanie endpointów API dla zachowania kompatybilności wstecznej.
- **Kontrola dostępu oparta na rolach**: Implementacja ról admin, manager i viewer z różnymi uprawnieniami.
- **Powiadomienia email**: Automatyczne alerty gdy stan spadnie poniżej minimalnego poziomu.
- **Dokumentacja API**: Dokumentacja OpenAPI/Swagger dla endpointów REST.

