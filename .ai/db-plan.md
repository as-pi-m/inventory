# Schemat bazy danych Inventory

## 1. Tabele

### 1.1. users

Tabela zarządza uwierzytelnianiem i autoryzacją użytkowników przez Spring Security.

- **id**: BIGSERIAL PRIMARY KEY
- **username**: VARCHAR(255) NOT NULL UNIQUE
- **password**: VARCHAR NOT NULL (hashowane BCrypt)
- **roles**: VARCHAR NOT NULL DEFAULT 'ROLE_USER'

*Ograniczenia:*
- Ograniczenie UNIQUE na `username`

### 1.2. products

Główna tabela przechowująca pozycje magazynowe z poziomami stanów i minimalnymi progami zamówienia.

- **id**: BIGSERIAL PRIMARY KEY
- **name**: VARCHAR NOT NULL
- **sku**: VARCHAR NOT NULL UNIQUE
- **description**: TEXT NULLABLE
- **unit**: VARCHAR NOT NULL
- **min_order_level**: INTEGER NOT NULL DEFAULT 0 CHECK (min_order_level >= 0)
- **unit_price**: DECIMAL(19, 4) NULLABLE
- **deleted**: BOOLEAN NOT NULL DEFAULT false
- **quantity**: INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0)
- **created_by**: VARCHAR NOT NULL

*Ograniczenia:*
- Ograniczenie UNIQUE na `sku`
- Ograniczenie CHECK na `min_order_level` (>= 0)
- Ograniczenie CHECK na `quantity` (>= 0)

*Reguły biznesowe:*
- `quantity` nie może być edytowane bezpośrednio - tylko przez przyjęcia lub korekty
- Miękkie usuwanie przez flagę `deleted` zachowuje dane historyczne
- `created_by` przechowuje nazwę użytkownika, który utworzył produkt

### 1.3. product_arrivals

Śledzi przychodzące dostawy i tworzy ślad audytowy dla zwiększeń zapasów.

- **id**: BIGSERIAL PRIMARY KEY
- **product_id**: BIGINT NOT NULL REFERENCES products(id)
- **quantity**: INTEGER NOT NULL CHECK (quantity >= 1)
- **source**: VARCHAR NOT NULL
- **arrival_date**: TIMESTAMPTZ NOT NULL DEFAULT now()
- **created_by**: VARCHAR NOT NULL

*Ograniczenia:*
- FOREIGN KEY `product_id` REFERENCES products(id)
- Ograniczenie CHECK na `quantity` (>= 1)

*Reguły biznesowe:*
- Każde przyjęcie automatycznie zwiększa ilość powiązanego produktu
- Pole `source` przechowuje nazwę dostawcy lub źródło dostawy
- `created_by` przechowuje nazwę użytkownika, który zarejestrował przyjęcie
- `arrival_date` jest automatycznie ustawiany na bieżący znacznik czasu

### 1.4. stock_corrections

Rejestruje ręczne korekty zapasów z obowiązkowym uzasadnieniem dla zgodności z audytem.

- **id**: BIGSERIAL PRIMARY KEY
- **product_id**: BIGINT NOT NULL REFERENCES products(id)
- **quantity**: INTEGER NOT NULL
- **reason**: VARCHAR(500) NOT NULL
- **correction_date**: TIMESTAMPTZ NOT NULL DEFAULT now()
- **created_by**: VARCHAR NOT NULL

*Ograniczenia:*
- FOREIGN KEY `product_id` REFERENCES products(id)
- `reason` ma maksymalną długość 500 znaków

*Reguły biznesowe:*
- `quantity` może być dodatnie (zwiększenie) lub ujemne (zmniejszenie)
- Każda korekta automatycznie dostosowuje ilość powiązanego produktu
- `reason` jest obowiązkowe dla celów śladu audytowego
- `created_by` przechowuje nazwę użytkownika, który dokonał korekty
- `correction_date` jest automatycznie ustawiany na bieżący znacznik czasu

## 2. Relacje

- Jeden użytkownik (users) może utworzyć wiele produktów (products) - relacja przez pole `created_by` (bez foreign key, przechowywane jako string).
- Jeden produkt (products) ma wiele przyjęć (product_arrivals) - relacja ONE-TO-MANY przez `product_id`.
- Jeden produkt (products) ma wiele korekt (stock_corrections) - relacja ONE-TO-MANY przez `product_id`.
- Jeden użytkownik (users) może rejestrować wiele przyjęć (product_arrivals) - relacja przez pole `created_by` (bez foreign key, przechowywane jako string).
- Jeden użytkownik (users) może rejestrować wiele korekt (stock_corrections) - relacja przez pole `created_by` (bez foreign key, przechowywane jako string).

## 3. Indeksy

### Automatyczne indeksy (Primary Keys):
- Indeks na kolumnie `id` w każdej tabeli (PRIMARY KEY)

### Indeksy zdefiniowane w aplikacji:
- **products**: UNIQUE indeks na kolumnie `sku`
- **products**: UNIQUE indeks na kolumnie `username` w tabeli users
- **product_arrivals**: Indeks na kolumnie `product_id` (foreign key - automatycznie tworzony przez JPA)
- **stock_corrections**: Indeks na kolumnie `product_id` (foreign key - automatycznie tworzony przez JPA)

### Zalecane dodatkowe indeksy (dla optymalizacji):
- Indeks na kolumnie `created_by` w tabeli products (dla filtrowania po twórcy)
- Indeks na kolumnie `deleted` w tabeli products (dla szybkiego filtrowania aktywnych produktów)
- Indeks na kolumnie `arrival_date` w tabeli product_arrivals (dla sortowania chronologicznego)
- Indeks na kolumnie `correction_date` w tabeli stock_corrections (dla sortowania chronologicznego)
- Indeks kompozytowy na (`product_id`, `arrival_date`) w tabeli product_arrivals (dla historii produktu)
- Indeks kompozytowy na (`product_id`, `correction_date`) w tabeli stock_corrections (dla historii produktu)

## 4. Zasady bezpieczeństwa i dostępu

Aplikacja korzysta z Spring Security do zarządzania autoryzacją:

- **Uwierzytelnianie**: Uwierzytelnianie oparte na formularzach z sesją HTTP
- **Autoryzacja**: Wszystkie operacje wymagają zalogowania (z wyjątkiem `/login` i `/health`)
- **Bezpieczeństwo na poziomie aplikacji**:
  - Użytkownicy widzą wszystkie produkty (brak RLS na poziomie bazy danych)
  - Operacje zapisu (tworzenie, aktualizacja, usuwanie) są ograniczone przez Spring Security
  - Wszystkie operacje zmieniające stan są chronione tokenem CSRF
  - Pole `created_by` jest automatycznie wypełniane przez aplikację na podstawie zalogowanego użytkownika

*Uwaga: Brak Row-Level Security (RLS) na poziomie bazy danych. Wszystkie ograniczenia dostępu są zarządzane przez warstwę aplikacji (Spring Security).*

## 5. Triggery i automatyzacja

### Obecne w aplikacji:
- **Automatyczne ustawianie timestampów**: `arrival_date` i `correction_date` są automatycznie ustawiane przez JPA (domyślna wartość @Column)
- **Automatyczne ustawianie `created_by`**: Obsługiwane przez logikę aplikacji (warstwa serwisów)
- **Automatyczna aktualizacja quantity**: Obsługiwana przez logikę aplikacji przy zapisywaniu przyjęć i korekt

### Brak w obecnej implementacji:
- Brak triggera dla `updated_at` w tabeli products (pole nie istnieje w modelu)
- Brak automatycznych triggerów na poziomie bazy danych - cała logika jest w warstwie aplikacji

## 6. Kluczowe zapytania i wzorce użycia

### 6.1. Raportowanie niskich stanów (główna funkcjonalność biznesowa):
```sql
SELECT id, name, sku, quantity, min_order_level
FROM products
WHERE deleted = false
  AND quantity < :threshold
ORDER BY quantity ASC;
```

### 6.2. Historia produktu (przyjęcia + korekty):
```sql
-- Przyjęcia produktów
SELECT id, quantity, source, arrival_date, created_by
FROM product_arrivals
WHERE product_id = :productId
ORDER BY arrival_date DESC;

-- Korekty stanów
SELECT id, quantity, reason, correction_date, created_by
FROM stock_corrections
WHERE product_id = :productId
ORDER BY correction_date DESC;
```

### 6.3. Lista aktywnych produktów:
```sql
SELECT id, name, sku, quantity, min_order_level, unit, unit_price
FROM products
WHERE deleted = false
ORDER BY name ASC;
```

### 6.4. Aktualizacja quantity przy przyjęciu:
```sql
UPDATE products
SET quantity = quantity + :arrivalQuantity
WHERE id = :productId;
```

### 6.5. Aktualizacja quantity przy korekcie:
```sql
UPDATE products
SET quantity = quantity + :correctionQuantity
WHERE id = :productId;
```

## 7. Migracje i zarządzanie schematem

- **ORM**: Spring Data JPA z Hibernate
- **Strategia schematów**: 
  - Development: `spring.jpa.hibernate.ddl-auto=update` (automatyczne tworzenie/aktualizacja tabel)
  - Production: Zalecane użycie Flyway lub Liquibase dla kontrolowanych migracji
- **Wersjonowanie**: Brak formalnego systemu migracji w obecnej implementacji

### Zalecane migracje dla produkcji:
1. Użycie Flyway lub Liquibase
2. Wersjonowanie każdej zmiany schematu
3. Rollback scripts dla każdej migracji
4. Testowanie migracji na środowisku testowym przed produkcją

## 8. Typy danych i mapowania JPA

| Kotlin Type   | JPA/Database Type | Przykładowe kolumny             |
|---------------|-------------------|---------------------------------|
| Long          |  BIGINT           | id, product_id                  |
| String        | VARCHAR           | name, sku, username, created_by |
| String        | TEXT              | description                     |
| Int           | INTEGER           | quantity, min_order_level       |
| BigDecimal    | DECIMAL(19,4)     | unit_price                      |
| Boolean       | BOOLEAN           | deleted                         |
| ZonedDateTime | TIMESTAMPTZ       | arrival_date, correction_date   |

## 9. Dodatkowe uwagi

### 9.1. Miękkie usuwanie (Soft Delete)
- Produkty nie są fizycznie usuwane z bazy danych
- Pole `deleted` = true oznacza produkt usunięty
- Zachowuje integralność referencyjną z product_arrivals i stock_corrections
- Pozwala na pełny audyt historii, nawet dla usuniętych produktów

### 9.2. Ślad audytowy (Audit Trail)
- Każda zmiana stanu magazynowego jest logowana przez product_arrivals lub stock_corrections
- Pole `created_by` we wszystkich tabelach transakcyjnych zapewnia pełną identyfikowalność
- Timestampy (arrival_date, correction_date) umożliwiają chronologiczną rekonstrukcję zmian

### 9.3. Integralność danych (Data Integrity)
- Klucze obce (Foreign keys) zapewniają integralność referencyjną między tabelami
- Ograniczenia CHECK zapewniają poprawność danych biznesowych
- Ograniczenia NOT NULL zapobiegają brakom krytycznych danych

### 9.4. Względy wydajnościowe (Performance Considerations)
- LAZY loading dla relacji ManyToOne zapobiega problemowi N+1 zapytań
- Indeksy na kluczach obcych przyspieszają joiny
- Miękkie usuwanie wymaga filtrowania `deleted = false` w większości zapytań

### 9.5. Skalowanie
- Obecna struktura jest odpowiednia dla małych i średnich magazynów (do ~100k produktów)
- Dla większej skali zalecane:
  - Partycjonowanie tabel arrivals i corrections po dacie
  - Archiwizacja starych rekordów
  - Dodatkowe indeksy dla często używanych zapytań
  - Cache'owanie często używanych produktów

