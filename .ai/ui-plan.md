# Architektura UI dla Inventory Management System

## 1. Przegląd struktury UI

Interfejs użytkownika jest zbudowany w oparciu o server-side rendering z wykorzystaniem Thymeleaf i Bootstrap 5. Struktura opiera się na głównym layoucie z nawigacją, widokach zarządzania produktami, rejestracji ruchów magazynowych oraz generowania raportów o niskich stanach. Aplikacja wykorzystuje responsywny design oparty na Bootstrap, komponenty HTML5 oraz minimalne użycie JavaScript dla progressive enhancement.

## 2. Lista widoków

- **Strona główna (Dashboard)**
  - **Ścieżka:** `/`
  - **Główny cel:** Prezentacja przeglądu funkcjonalności systemu i szybki dostęp do głównych modułów.
  - **Kluczowe informacje:** Karty z opisem 4 głównych funkcji (zarządzanie produktami, przyjęcia, korekty, alerty), informacje o bezpieczeństwie i funkcjach audytowych.
  - **Kluczowe komponenty:** Grid kart (4 główne funkcje), sekcja informacyjna z ikonami Bootstrap Icons, linki nawigacyjne do poszczególnych modułów.
  - **UX, dostępność i względy bezpieczeństwa:** Przejrzysty układ kart, semantyczny HTML, ikony dla lepszej rozpoznawalności funkcji, wymaga uwierzytelnienia.

- **Ekran logowania**
  - **Ścieżka:** `/login`
  - **Główny cel:** Umożliwienie użytkownikowi bezpiecznego logowania do systemu.
  - **Kluczowe informacje:** Formularz z polami username i password, komunikaty o błędach logowania, potwierdzenie wylogowania.
  - **Kluczowe komponenty:** Formularz logowania z Spring Security, komunikaty alert dla błędów/sukcesu, CSRF token.
  - **UX, dostępność i względy bezpieczeństwa:** Minimalistyczny formularz, autofocus na polu username, czytelne komunikaty błędów, BCrypt password hashing, session-based authentication, CSRF protection.

- **Profil użytkownika**
  - **Ścieżka:** `/profile`
  - **Główny cel:** Wyświetlenie informacji o zalogowanym użytkowniku.
  - **Kluczowe informacje:** Nazwa użytkownika, przypisane uprawnienia/role.
  - **Kluczowe komponenty:** Karta z danymi użytkownika, lista authorities.
  - **UX, dostępność i względy bezpieczeństwa:** Prosty widok readonly, dostęp tylko dla zalogowanych użytkowników, wyświetlanie ról z Spring Security context.

- **Lista produktów**
  - **Ścieżka:** `/products`
  - **Główny cel:** Przegląd wszystkich produktów w magazynie z możliwością zarządzania nimi.
  - **Kluczowe informacje:** Tabela z produktami zawierająca: nazwa, SKU, jednostka, ilość, minimalny poziom zamówienia, cena jednostkowa, twórca.
  - **Kluczowe komponenty:** Tabela Bootstrap z sortowaniem, przyciski akcji (View, Edit, Delete, History), przycisk "Add product", formularz DELETE z CSRF.
  - **UX, dostępność i względy bezpieczeństwa:** Responsywna tabela, wyraźne przyciski akcji z kolorami semantycznymi (primary, warning, danger), soft delete dla zachowania integralności danych, potwierdzenie usunięcia przez formularz POST.

- **Formularz produktu (tworzenie/edycja)**
  - **Ścieżka:** `/products/new` (nowy), `/products/{id}/edit` (edycja)
  - **Główny cel:** Dodanie nowego produktu lub edycja istniejącego.
  - **Kluczowe informacje:** Pola: nazwa, SKU, opis, jednostka, minimalny poziom zamówienia, cena jednostkowa, ilość początkowa (tylko przy tworzeniu).
  - **Kluczowe komponenty:** Formularz z walidacją Bean Validation, komunikaty błędów inline, przyciski Save/Cancel.
  - **UX, dostępność i względy bezpieczeństwa:** Walidacja po stronie serwera, automatyczne wypełnianie pól przy edycji, ukrycie pola quantity przy edycji (tylko przez arrivals/corrections), komunikaty błędów przy polach, semantyczne labele, CSRF protection.

- **Widok szczegółów produktu**
  - **Ścieżka:** `/products/{id}`
  - **Główny cel:** Wyświetlenie pełnych informacji o produkcie z szybkim dostępem do akcji.
  - **Kluczowe informacje:** Wszystkie dane produktu w formacie karty, przyciski nawigacyjne do historii, przyjęć i korekt.
  - **Kluczowe komponenty:** Bootstrap card, przyciski akcji (View History, Register Arrival, Register Correction, Back to List).
  - **UX, dostępność i względy bezpieczeństwa:** Przejrzysty układ karty, wyraźne CTA buttons, łatwy dostęp do powiązanych operacji.

- **Widok historii produktu**
  - **Ścieżka:** `/history/{productId}`
  - **Główny cel:** Wyświetlenie pełnej historii ruchów magazynowych dla danego produktu (przyjęcia i korekty).
  - **Kluczowe informacje:** Dane produktu (SKU, obecna ilość), tabela przyjęć (data, ilość, źródło, autor), tabela korekt (data, ilość, powód, autor).
  - **Kluczowe komponenty:** Dwie osobne tabele (Arrivals i Stock Corrections), przyciski do rejestracji nowych ruchów, komunikat sukcesu po operacji, kolorowanie ilości (zielony dla dodatnich, czerwony dla ujemnych).
  - **UX, dostępność i względy bezpieczeństwa:** Chronologiczne sortowanie, wyraźne rozróżnienie między przyjęciami a korektami, pełny audit trail z timestampami i autorami, komunikaty o braku danych gdy listy puste.

- **Formularz rejestracji przyjęcia**
  - **Ścieżka:** `/arrivals/new/{productId}`
  - **Główny cel:** Rejestracja nowego przyjęcia towaru z automatyczną aktualizacją stanu.
  - **Kluczowe informacje:** Nazwa produktu w nagłówku, pola: ilość (min: 1), źródło/dostawca.
  - **Kluczowe komponenty:** Formularz z walidacją, hidden input dla productId, komunikaty błędów inline, przyciski Submit.
  - **UX, dostępność i względy bezpieczeństwa:** Walidacja min value (>= 1), automatyczne ustawienie autora z Security context, timestamp automatyczny, redirect do history po sukcesie z flash message, CSRF protection.

- **Formularz rejestracji korekty**
  - **Ścieżka:** `/corrections/new/{productId}`
  - **Główny cel:** Rejestracja korekty stanu magazynowego z obowiązkowym powodem.
  - **Kluczowe informacje:** Nazwa produktu, pola: ilość (może być ujemna), powód (textarea, max 500 znaków, obowiązkowe).
  - **Kluczowe komponenty:** Formularz z walidacją, textarea dla reason, komunikaty błędów inline, przyciski Submit/Cancel.
  - **UX, dostępność i względy bezpieczeństwa:** Wyraźne oznaczenie, że ilość może być + lub -, obowiązkowe pole reason dla audytu, walidacja NOT_BLANK, ControllerAdvice dla obsługi błędów, redirect do history po sukcesie, CSRF protection.

- **Widok alertów niskiego stanu (Low Stock Alert)**
  - **Ścieżka:** `/alerts/low-stock`
  - **Główny cel:** Generowanie raportu produktów poniżej zadanego progu (główna funkcjonalność biznesowa).
  - **Kluczowe informacje:** Formularz z polem threshold, tabela wyników z: SKU, nazwa, obecna ilość, minimalny poziom, jednostka, deficyt.
  - **Kluczowe komponenty:** Formularz do ustawienia progu, tabela wyników z kolorowaniem (red badge dla qty=0, yellow dla qty>0), przycisk "Export to CSV", badge z liczbą produktów, komunikat sukcesu gdy brak produktów poniżej progu.
  - **UX, dostępność i względy bezpieczeństwa:** Intuicyjny formularz z opisem, walidacja min=0, wyraźne kolorowanie stanów krytycznych, przycisk eksportu widoczny tylko gdy są wyniki, sortowanie według ilości, linki do View i History dla każdego produktu.

- **Eksport CSV alertów**
  - **Ścieżka:** `/alerts/low-stock/export?threshold={value}`
  - **Główny cel:** Pobranie raportu w formacie CSV dla integracji z systemami zamówień.
  - **Kluczowe informacje:** Plik CSV z nagłówkami: Product Name, SKU, Current Quantity, Minimum Order Level.
  - **Kluczowe komponenty:** Endpoint generujący plik CSV z timestampem w nazwie.
  - **UX, dostępność i względy bezpieczeństwa:** Automatyczne pobranie pliku, nazwa z timestampem (low_stock_report_YYYYMMDD_HHmmss.csv), Content-Type: text/csv.

- **Strony błędów**
  - **Ścieżki:** `/error/400`, `/error/404`, `/error/500`
  - **Główny cel:** Przyjazne komunikaty błędów dla użytkownika.
  - **Kluczowe informacje:** Kod błędu, opis, link powrotny do strony głównej lub produktów.
  - **Kluczowe komponenty:** Bootstrap alerts, przyciski nawigacyjne.
  - **UX, dostępność i względy bezpieczeństwa:** Czytelne komunikaty bez szczegółów technicznych, łatwa nawigacja powrotna.

## 3. Mapa podróży użytkownika

1. Użytkownik uzyskuje dostęp do aplikacji i trafia na ekran logowania (`/login`).
2. Po poprawnym uwierzytelnieniu użytkownik zostaje przekierowany na stronę główną (`/`) z przeglądem funkcjonalności.
3. Użytkownik przechodzi do listy produktów (`/products`) aby przejrzeć stan magazynu.
4. Użytkownik może:
   - **Ścieżka A - Dodanie produktu:**
     - Kliknięcie "Add product" → formularz (`/products/new`)
     - Wypełnienie danych (nazwa, SKU, jednostka, min order level, cena, ilość początkowa)
     - Walidacja i zapis → redirect do `/products` z komunikatem sukcesu
   - **Ścieżka B - Edycja produktu:**
     - Kliknięcie "Edit" → formularz edycji (`/products/{id}/edit`)
     - Modyfikacja danych (bez możliwości edycji quantity)
     - Zapis → redirect do `/products` z komunikatem sukcesu
   - **Ścieżka C - Podgląd produktu:**
     - Kliknięcie "View" → szczegóły produktu (`/products/{id}`)
     - Dostęp do szybkich akcji (History, Register Arrival, Register Correction)
   - **Ścieżka D - Historia produktu:**
     - Kliknięcie "History" → widok historii (`/history/{id}`)
     - Przegląd wszystkich przyjęć i korekt
     - Możliwość rejestracji nowego ruchu
5. **Rejestracja przyjęcia towaru:**
   - Z poziomu history lub product view: kliknięcie "Register Arrival"
   - Formularz przyjęcia (`/arrivals/new/{id}`)
   - Wprowadzenie ilości i źródła
   - Walidacja i zapis → automatyczna aktualizacja quantity → redirect do `/history/{id}` z komunikatem
6. **Rejestracja korekty:**
   - Z poziomu history lub product view: kliknięcie "Register Correction"
   - Formularz korekty (`/corrections/new/{id}`)
   - Wprowadzenie ilości (+ lub -) i obowiązkowego powodu
   - Walidacja i zapis → automatyczna aktualizacja quantity → redirect do `/history/{id}` z komunikatem
7. **Generowanie raportu niskich stanów (główna funkcjonalność):**
   - Użytkownik przechodzi do `/alerts/low-stock` (z nawigacji lub home page)
   - Wprowadza próg ilościowy w formularzu
   - Kliknięcie "Generate Report" → wyświetlenie tabeli produktów poniżej progu
   - Przegląd wyników z kolorowaniem stanów krytycznych
   - Opcjonalnie: eksport do CSV dla integracji z systemami zamówień
8. **Zarządzanie profilem:**
   - Użytkownik klika "My Profile" w nawigacji
   - Przegląd swoich danych i uprawnień (`/profile`)
9. **Wylogowanie:**
   - Użytkownik klika "Logout" → formularz POST → wylogowanie → redirect do `/login` z komunikatem
10. W przypadku błędów walidacji lub innych problemów, użytkownik otrzymuje komunikaty inline w formularzach lub dedykowane strony błędów.

## 4. Układ i struktura nawigacji

- **Główna nawigacja:** Górne menu (header) dostępne na każdej stronie po zalogowaniu.
- **Elementy nawigacyjne:**
  - **Logo + nazwa aplikacji:** Link do strony głównej (`/`)
  - **Products:** Link do listy produktów (`/products`)
  - **Low Stock Alert:** Link do generowania raportów (`/alerts/low-stock`)
  - **My Profile:** Przycisk dostępu do profilu (`/profile`)
  - **Logout:** Przycisk wylogowania (formularz POST z CSRF)
- **Layout structure:**
  - Fragment `nav.html` zawierający wspólną nawigację
  - Fragment `links.html` z Bootstrap CSS i Bootstrap Icons
  - Fragment `scripts.html` z Bootstrap JS
  - Główny szablon `layout.html` dla strony głównej
  - Wszystkie widoki używają fragmentów dla spójności
- **Responsywność:** Bootstrap 5 grid system zapewnia responsywność na urządzeniach mobilnych i desktopowych.
- **Przepływ:** Nawigacja jest dostępna na każdej stronie, zachowuje kontekst użytkownika przez Spring Security session.

## 5. Kluczowe komponenty

- **Nawigacja (nav fragment):**
  - Logo jako SVG favicon
  - Linki do głównych sekcji
  - Przyciski profilu i wylogowania (widoczne tylko dla zalogowanych)
  - Responsywny layout z Flexbox

- **Formularz logowania:**
  - Spring Security form-based authentication
  - Pola: username (autofocus), password
  - CSRF token automatycznie dodawany przez Thymeleaf
  - Komunikaty błędów (param.error) i sukcesu (param.logout)

- **Tabela produktów:**
  - Bootstrap striped table
  - Kolumny: Name, SKU, Unit, Quantity, Min Order, Unit Price, Created By, Actions
  - Przyciski akcji z semantycznymi kolorami
  - Formularz DELETE inline z CSRF token

- **Formularz produktu:**
  - Bean Validation annotations w backend
  - Thymeleaf field binding (`th:field`)
  - Komunikaty błędów inline (`th:errors`)
  - Conditional rendering (quantity field tylko przy tworzeniu)
  - Przyciski Save/Cancel

- **Tabele historii:**
  - Dwie osobne tabele dla arrivals i corrections
  - Formatowanie dat z Thymeleaf (`#temporals.format`)
  - Kolorowanie ilości (text-success dla dodatnich, text-danger dla ujemnych)
  - Komunikaty o braku danych (`th:if="${#lists.isEmpty()}"`)

- **Formularz Low Stock Alert:**
  - Input number z walidacją min=0
  - Conditional rendering wyników
  - Badge z liczbą znalezionych produktów
  - Przycisk eksportu widoczny tylko gdy są wyniki
  - Tabela z kolorowaniem stanów (bg-danger dla qty=0, bg-warning dla qty>0)

- **Flash messages:**
  - Bootstrap alerts z `alert-dismissible`
  - RedirectAttributes w kontrolerach
  - Thymeleaf conditional rendering (`th:if="${success}"`)
  - Automatyczne zamykanie z przyciskiem close

- **Karty informacyjne (cards):**
  - Bootstrap card component
  - Ikony Bootstrap Icons dla lepszej wizualizacji
  - Grid layout (row + col-md-6) dla responsywności
  - Semantyczne kolory przycisków

- **CSRF Protection:**
  - Automatycznie dodawany przez Thymeleaf w formularzach
  - Hidden input z tokenem
  - Spring Security wymusza CSRF dla wszystkich POST/PUT/DELETE

## 6. System stylowania

- **Framework:** Bootstrap 5.3
  - Grid system dla layoutu responsywnego
  - Utility classes (mb-3, d-flex, gap-2, text-center, etc.)
  - Komponenty (cards, tables, forms, buttons, alerts, badges)
  
- **Ikony:** Bootstrap Icons
  - Semantyczne ikony dla akcji (bi-box-seam, bi-arrow-down-circle, bi-exclamation-triangle, etc.)
  - Ikony inline w tekstach i przyciskach
  
- **Kolory semantyczne:**
  - Primary (niebieski) - główne akcje (View, Edit, Register Arrival button)
  - Success (zielony) - pozytywne akcje (Register Arrival)
  - Warning (żółty) - korekty (Register Correction, Edit)
  - Danger (czerwony) - destrukcyjne akcje (Delete), alerty
  - Info (jasnoniebieski) - podgląd (View)
  - Secondary (szary) - anulowanie, powrót

- **Typografia:**
  - Bootstrap typography classes (display-4, lead, h1-h6)
  - Font stack: system fonts dla szybkiego ładowania

- **Spacing:**
  - Bootstrap spacing utilities (mb-3, mt-4, py-3, gap-2)
  - Container class dla marginesów

- **Responsywność:**
  - Breakpoints Bootstrap (col-md-6, col-lg-4)
  - Responsywne tabele (table-responsive)
  - Flexbox utilities (d-flex, justify-content-between)

## 7. Względy dostępności (Accessibility)

- **Semantyczny HTML:**
  - Użycie właściwych tagów (header, main, nav, form, table)
  - Labele dla wszystkich inputów
  - Atrybuty `required` dla obowiązkowych pól

- **Nawigacja klawiaturowa:**
  - Wszystkie interaktywne elementy dostępne przez Tab
  - Focus states z Bootstrap
  - Autofocus na pierwszym polu w formularzach logowania

- **Komunikaty dla screen readerów:**
  - Aria labels gdzie potrzebne
  - Alert roles dla komunikatów błędów i sukcesu
  - Znaczące teksty przycisków (nie tylko ikony)

- **Kontrast kolorów:**
  - Bootstrap domyślne kolory spełniają WCAG AA
  - Wyraźne różnice między stanami (success, warning, danger)

- **Responsywność:**
  - Dostosowanie do różnych rozmiarów ekranów
  - Czytelne na urządzeniach mobilnych

## 8. Bezpieczeństwo UI

- **Uwierzytelnianie:**
  - Spring Security form-based authentication
  - Session-based z HttpOnly cookies
  - Automatyczne przekierowanie do `/login` dla niezalogowanych

- **CSRF Protection:**
  - Token w każdym formularzu POST/PUT/DELETE
  - Automatyczne dodawanie przez Thymeleaf Security
  - Walidacja po stronie serwera

- **XSS Prevention:**
  - Thymeleaf automatycznie escapuje HTML
  - Bezpieczne renderowanie user input

- **Autoryzacja:**
  - Wszystkie widoki (oprócz `/login`) wymagają uwierzytelnienia
  - Conditional rendering z `sec:authorize`
  - Server-side validation w kontrolerach

- **Walidacja danych:**
  - Bean Validation annotations (@NotBlank, @Min, @Size)
  - Server-side validation przed zapisem
  - Komunikaty błędów bez szczegółów technicznych

- **Audit Trail:**
  - Automatyczne zapisywanie `createdBy` z Security context
  - Timestampy dla wszystkich operacji
  - Niemodyfikowalna historia w UI

## 9. Wydajność i optymalizacja

- **Server-Side Rendering:**
  - Brak potrzeby budowania JavaScript bundle
  - Szybkie First Contentful Paint
  - SEO-friendly (choć dla internal tool mniej istotne)

- **Minimalny JavaScript:**
  - Tylko Bootstrap JS dla interaktywnych komponentów (collapse, alerts)
  - Brak heavy JavaScript frameworks
  - Progressive enhancement

- **CSS:**
  - Bootstrap z CDN (cache browser)
  - Brak custom CSS poza inline styles
  - Minimalne ładowanie zasobów

- **Database queries:**
  - LAZY loading dla relacji ManyToOne
  - Brak N+1 query problem
  - Indeksy na foreign keys

- **Caching:**
  - Static resources (favicon, CSS, JS) cache'owalne
  - Session cache dla authenticated users

## 10. Przyszłe ulepszenia UI

- **Paginacja:**
  - Dodanie paginacji dla listy produktów przy dużej liczbie rekordów
  - Page size selector

- **Sortowanie i filtrowanie:**
  - Sortowanie kolumn w tabelach
  - Filtry dla szybkiego wyszukiwania produktów
  - Search bar w liście produktów

- **Dashboard z wykresami:**
  - Wykresy trendów stanów magazynowych
  - Top produktów o niskim stanie
  - Statystyki przyjęć i korekt

- **Batch operations:**
  - Zaznaczanie wielu produktów do operacji grupowych
  - Bulk delete, bulk export

- **Enhanced mobile experience:**
  - Dedykowane widoki mobilne
  - Swipe gestures
  - Bottom navigation

- **Real-time notifications:**
  - WebSocket notifications o niskich stanach
  - Toast notifications dla operacji w tle

- **Dark mode:**
  - Przełącznik light/dark theme
  - Preferencje użytkownika zapisywane w session/localStorage

- **Internationalization (i18n):**
  - Wsparcie dla wielu języków
  - Thymeleaf message bundles

- **Advanced export options:**
  - PDF reports
  - Excel format
  - Custom report templates

- **Inline editing:**
  - Edycja produktów bezpośrednio w tabeli
  - AJAX requests dla szybszych operacji bez pełnego refresh

