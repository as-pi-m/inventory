# Dokument wymagań produktu (PRD) – System magazynowy (Inwentarz)

## 1. Przegląd produktu

Celem projektu jest stworzenie lekkiego, bezpiecznego systemu do zarządzania magazynem/inwentarzem dla małych i średnich organizacji. **Główną funkcjonalnością biznesową aplikacji jest możliwość generowania raportów o niskich stanach magazynowych na żądanie użytkownika**, umożliwiające proaktywne zarządzanie zamówieniami i zapobieganie brakom w magazynie. Aplikacja pozwala na definiowanie produktów z minimalnymi poziomami zamówienia, śledzenie stanów magazynowych, rejestrowanie ruchów towaru oraz wykonywanie raportów. System zbudowany w oparciu o Spring Boot (Kotlin), z użyciem Gradle jako narzędzia budującego.

## 2. Problem użytkownika

Firmy i zespoły często tracą czas i pieniądze przez nieaktualne stany magazynowe oraz brak możliwości szybkiego przeglądu produktów o niskich stanach. **Kluczowym problemem jest trudność w identyfikacji produktów wymagających uzupełnienia**, co prowadzi do przestojów w produkcji i utraty sprzedaży. Potrzebują prostego narzędzia do monitorowania poziomów magazynowych, generowania raportów na żądanie o produktach wymagających zamówienia oraz do śledzenia historii ruchów magazynowych.

## 3. Wymagania funkcjonalne

1. **Generowanie raportów o niskich stanach** (funkcjonalność główna):
    - Możliwość ręcznego wygenerowania raportu produktów z ilością poniżej minimalnego poziomu zamówienia.
    - Eksport raportów do CSV z listą produktów wymagających uzupełnienia.
    - Użytkownik określa próg minimalnej ilości przy generowaniu raportu.
    - Raport zawiera bieżący stan oraz informacje o produktach poniżej progu.

2. Definiowanie produktów:
    - Formularz dodawania produktu z polami: nazwa, SKU, opis, jednostka miary.
    - Edycja i usuwanie produktu (usuwanie logiczne jeśli produkt powiązany z historią ruchów).

3. Zarządzanie stanami magazynowymi:
    - Rejestrowanie przyjęć (przyjęcie), wydań (wydanie) i korekt ilościowych (inwentaryzacja).
    - Możliwość ręcznej korekty stanu z obowiązkowym powodem i autorem zmiany.

4. Śledzenie lokalizacji:
    - Obsługa lokalizacji/magazynów.

5. Import/eksport:
    - Eksport raportów stanów i historii ruchów (CSV).
    - **Priorytet: eksport raportów o niskich stanach**.

6. Raporty i alerty:
    - **Widok dedykowany dla produktów o niskim stanie z możliwością szybkiego eksportu** (raport generowany na żądanie użytkownika).
    - Widok aktualnych stanów z filtrowaniem i wyszukiwaniem.
    - Podstawowe raporty: historia ruchów dla produktu.

7. Audyt i historia:
    - Zapisywanie historii wszystkich zmian stanu (kto, kiedy, typ operacji, ilość, powód).

8. Uwierzytelnianie i uprawnienia:
    - Rejestracja i logowanie użytkowników.
    - Role: admin (pełne prawa), magazynier (ruchy i korekty), czytelnik (tylko podgląd).
    - Operacje wrażliwe wymagają autoryzacji.

9. Bezpieczeństwo:
    - Dane przechowywane zgodnie z zasadami bezpieczeństwa.
    - Proxy Cloudflare dla ochrony przed atakami.

10. Monitoring:
    - Healthcheck endpoint dla weryfikacji dostępności aplikacji.

11. CI/CD:
    - Automatyczne budowanie, testowanie i wdrażanie aplikacji po zatwierdzeniu merge requesta.

## 4. Architektura techniczna

- **Backend**: Spring Boot 3.x (Kotlin)
- **Build tool**: Gradle
- **Baza danych**: (do uzupełnienia zgodnie z aktualną konfiguracją)
- **Deployment**: Serwer Linux z Nginx jako reverse proxy
- **CI/CD**: GitLab CI/CD z pipeline wykonującym build, testy i deployment
- **DNS/Security**: Cloudflare jako proxy i CDN
- **Deployment method**: SSH deployment z użyciem `sshpass` i zmiennych środowiskowych z `~/.profile`

## 5. Historyjki użytkowników

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
Tytuł: Dodanie healthcheck <br/>
Opis: Jako deweloper muszę po poprawnej instalacji sprawdzić, czy aplikacja działa poprawnie. <br/>
Kryteria akceptacji:
- Dodany healthcheck do aplikacji

**ID: US-INV-008** <br/>
Tytuł: Stworzenie serwera <br/>
Opis: Jako deweloper muszę mieć gdzie zainstalować aplikację. <br/>
Kryteria akceptacji:
- Działający serwer linux z włączonym nginx

**ID: US-INV-009** <br/>
Tytuł: Stworzenie domeny i dodanie jej do Cloudflare <br/>
Opis: Jako deweloper muszę odpowiednio zabezpieczyć stronę przed atakami. <br/>
Kryteria akceptacji:
- Dodana domena wskazująca na serwer
- Dodana proxy cloudflare

**ID: US-INV-010** <br/>
Tytuł: Dodanie CI/CD pipeline <br/>
Opis: Jako deweloper po dodaniu kodu chcę mieć automatyczne testy i wdrożenie na serwerze, aby zapewnić jakość i szybkie dostarczanie zmian. <br/>
Kryteria akceptacji:
- Po zatwierdzeniu kodu w repozytorium (MR do głównej gałęzi) uruchamiane są testy jednostkowe i integracyjne wraz z instalacją na serwerze.

## 6. Metryki sukcesu

1. **Skuteczność raportowania**:
    - Cel: 100% produktów o niskim stanie poprawnie identyfikowanych w raportach generowanych na żądanie.
    - Czas generowania raportu o niskich stanach < 5 sekund dla magazynu do 10000 produktów.
2. **Dokładność stanów**:
    - Cel: powyżej 98% zgodności między zapisem systemowym a inwentaryzacją fizyczną.
3. **Czas operacyjny**:
    - Średni czas rejestracji przyjęcia/wydania < 2 minuty.
4. **Dostępność systemu**:
    - Uptime > 99% (monitoring przez healthcheck).
5. **CI/CD**:
    - Automatyczne wdrożenie w < 10 minut od zatwierdzenia MR.

## 7. Wymagania prawne i bezpieczeństwo

- Dane użytkowników i operacji przechowywane zgodnie z lokalnymi przepisami (RODO).
- Role i uprawnienia ograniczają dostęp do operacji wpływających na stan.
- Szyfrowane połączenia (HTTPS przez Cloudflare).

## 8. Dalsze kroki / roadmapa po MVP

- **Automatyczne powiadomienia email o niskich stanach** (rozszerzenie głównej funkcjonalności).
- **Dashboard z wykresami trendów magazynowych** (wizualizacja raportów).
- Dodanie obsługi wielu magazynów/lokalizacji.
- Wprowadzenie obsługi kodów kreskowych.
- Zaawansowane raporty finansowe (wycena zapasów).
- Integracja z zewnętrznymi systemami (API).
- Obsługa partii i dat ważności produktów.
