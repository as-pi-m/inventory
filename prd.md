# Dokument wymagań produktu (PRD) – System magazynowy (Inwentarz)

## 1. Przegląd produktu

Celem projektu jest stworzenie lekkiego, bezpiecznego systemu do zarządzania magazynem/inwentarzem dla małych i średnich organizacji. Aplikacja pozwoli na definiowanie produktów, śledzenie stanów magazynowych, rejestrowanie ruchów towaru (przyjęcia, wydania, transfery) oraz wykonywanie inwentaryzacji i raportów podstawowych.

## 2. Problem użytkownika

Firmy i zespoły często tracą czas i pieniądze przez nieaktualne stany magazynowe, wartościowanie zapasów i błędy przy ręcznym prowadzeniu ewidencji. Potrzebują prostego narzędzia do szybkiego dodawania produktów, śledzenia ilości i lokalizacji oraz do wykonywania korekt i audytu zmian.

## 3. Wymagania funkcjonalne

1. Definiowanie produktów:
   - Formularz dodawania produktu z polami: nazwa, SKU, opis, jednostka miary, domyślna lokalizacja, minimalny poziom zamówienia, cena jednostkowa (opcjonalnie), kategorie/tagi.
   - Edycja i usuwanie produktu (usuwanie logiczne jeśli produkt powiązany z historią ruchów).

2. Zarządzanie stanami magazynowymi:
   - Rejestrowanie przyjęć (przyjęcie), wydań (wydanie) i korekt ilościowych (inwentaryzacja).
   - Obsługa transferów między lokalizacjami magazynowymi.
   - Możliwość ręcznej korekty stanu z obowiązkowym powodem i autorem zmiany.

3. Śledzenie lokalizacji i partii:
   - Obsługa wielu lokalizacji/magazynów.
   - (MVP opcjonalnie) pole numer partii/exp date dla produktów wymagających śledzenia.

4. Import/eksport:
   - Import/eksport CSV z produktami i stanami.
   - Eksport raportów stanów i historii ruchów.

5. Raporty i alerty:
   - Widok aktualnych stanów z filtrowaniem i wyszukiwaniem.
   - Alerty o niskim stanie (na podstawie minimalnego poziomu).
   - Podstawowe raporty: historia ruchów dla produktu, stan według lokalizacji.

6. Audyt i historia:
   - Zapisywanie historii wszystkich zmian stanu (kto, kiedy, typ operacji, ilość, powód).
   - Możliwość przeprowadzenia inwentaryzacji i porównania z zapisanym stanem.

7. Uwierzytelnianie i uprawnienia:
   - Rejestracja i logowanie użytkowników.
   - Role: admin (pełne prawa), magazynier (ruchy i korekty), czytelnik (tylko podgląd).
   - Operacje wrażliwe wymagają autoryzacji.

8. Skalowalność i bezpieczeństwo:
   - Dane przechowywane zgodnie z zasadami bezpieczeństwa (szyfrowanie w spoczynku/transit).
   - Projektowanie bazy danych umożliwiające skalowanie (indeksy, paginacja).

## 4. Granice produktu (MVP)

1. Poza zakresem MVP:
   - Zaawansowane prognozowanie zapasów i optymalizacja zamówień.
   - Integracja z zewnętrznymi systemami ERP/ERP connectors (poza prostym CSV/REST API).
   - Obsługa kodów kreskowych/automatycznej identyfikacji (może być dodane później).
   - Złożone reguły wyceny kosztów (FIFO/LIFO) — domyślnie prosta cena jednostkowa.
   - Obsługa wielu walut i zaawansowanego rozliczania kosztów.

## 5. Historyjki użytkowników (przykładowe)

**ID: US-INV-001** <br/>
Tytuł: Dodanie nowego produktu <br/>
Opis: Jako magazynier chcę dodać nowy produkt z podstawowymi danymi (nazwa, SKU, jednostka), aby móc rejestrować jego ruchy. <br/>
Kryteria akceptacji:
- Formularz zawiera pola: nazwa, SKU, jednostka, minimalny poziom.
- Po zapisaniu produkt pojawia się na liście produktów.
- Produkt ma unikalne SKU (walidacja).

**ID: US-INV-002** <br/>
Tytuł: Dodanie obsługi logowania <br/>
Opis: Jako użytkownik chcę mieć możliwość bezpiecznego logowania do systemu, aby chronić dane magazynowe. <br/>
Kryteria akceptacji:
- Dostęp do swojego profilu po podaniu poprawnych danych uwierzytelniających.
- Logowanie do systemu.

**ID: US-INV-003** <br/>
Tytuł: Zarejestrowanie przyjęcia towaru <br/>
Opis: Jako magazynier chcę zarejestrować przyjęcie określonej ilości produktu, aby zaktualizować stany magazynowe. <br/>
Kryteria akceptacji:
- Można wybrać produkt i wprowadzić ilość oraz źródło przyjęcia (nazwę doręczyciela).
- System aktualizuje stan i zapisuje wpis w historii operacji z autorem i timestampem.

**ID: US-INV-004** <br/>
Tytuł: Wykonanie korekty stanu (inwentaryzacja) <br/>
Opis: Jako magazynier chcę wprowadzić korektę ilości z powodem, aby zsynchronizować stan magazynu z fizycznym. <br/>
Kryteria akceptacji:
- Nie można edytować stanu za pomocą "Edit" — tylko przez korektę.
- Korekta wymaga podania powodu i autora.
- Historia korekty jest zapisana i widoczna w historii produktu.
- Błędy powinny być odpowiednio obsłużone.

**ID: US-INV-005** <br/>
Tytuł: Alert niskiego stanu <br/>
Opis: Jako manager chcę otrzymywać listę produktów poniżej minimalnego poziomu, aby móc zamówić brakujące zapasy. <br/>
Kryteria akceptacji:
- System generuje widok/filtr produktów z ilością poniżej progu.
- Możliwość eksportu listy do CSV.

**ID: US-INV-006** <br/>
Tytuł: Dodanie dokumentacji projektowej <br/>
Opis: Jako deweloper muszę mieć dokumentację aplikacji. <br/>
Kryteria akceptacji:
- README.md zawierające całośc dokumentacji o aplikacji z opisem funkcjonalności.

**ID: US-INV-007** <br/>
Tytuł: Stworzenie serwera <br/>
Opis: Jako deweloper muszę mieć gdzie zainstalować aplikację. <br/>
Kryteria akceptacji:
- Działający serwer linux z włączonym nginx

**ID: US-INV-008** <br/>
Tytuł: Stworzenie domeny i dodanie jej do Cloudflare <br/>
Opis: Jako deweloper muszę odpowiednio zabezpieczyć stronę przed atakami. <br/>
Kryteria akceptacji:
- Dodana domena wskazująca na serwer
- Dodana proxy cloudflare

**ID: US-INV-009** <br/>
Tytuł: Dodanie CI/CD pipeline <br/>
Opis: Jako deweloper po dodaniu kodu chcę mieć automatyczne testy i wdrożenie na serwerze, aby zapewnić jakość i szybkie dostarczanie zmian. <br/>
Kryteria akceptacji:
- Po zatwierdzeniu kodu w repozytorium (MR do głównej gałęzi) uruchamiane są testy jednostkowe i integracyjne wraz z instalacją na serwerze.

## 6. Metryki sukcesu

1. Dokładność stanów:
   - Cel: powyżej 98% zgodności między zapisem systemowym a inwentaryzacją fizyczną (w ciągu 6 miesięcy).
2. Czas operacyjny:
   - Średni czas rejestracji przyjęcia/ wydania < 2 minuty.
3. Redukcja braków:
   - Spadek liczby krytycznych braków (stockouts) o 50% w ciągu 3 miesięcy od wdrożenia alertów.
4. Onboarding:
   - Możliwość zaimportowania katalogu produktowego i uruchomienia systemu w mniej niż 1 dzień pracy administratora.

## 7. Wymagania prawne i bezpieczeństwo

- Dane użytkowników i operacji przechowywane zgodnie z lokalnymi przepisami (RODO/OS Privacy) — możliwość usunięcia konta i powiązanych danych na żądanie.
- Role i uprawnienia muszą ograniczać dostęp do operacji wpływających na stan.
- Regularne kopie zapasowe i logowanie działań krytycznych.

## 8. Dalsze kroki / roadmapa po MVP

- Dodanie integracji z systemami zamówień i dostaw (EDI/REST).
- Wprowadzenie obsługi kodów kreskowych i skanerów mobilnych.
- Zaawansowane algorytmy prognozowania popytu i optymalizacji zapasów.
- Rozszerzenie raportów finansowych (wycena zapasów, FIFO/LIFO).
